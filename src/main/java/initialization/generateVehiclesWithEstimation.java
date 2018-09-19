package initialization;

import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;
import estimation.trafficStateEstimation.*;
import dataProvider.getSimulationData.*;
import dataProvider.getEstimationResults.*;
import commonClass.forInitialization.*;
import initialization.trafficInitialization.*;
import test.DebugCases;

import java.util.*;

public class generateVehiclesWithEstimation {

    //***************************************************************************************************************
    //*************** Vehicle Generation At Signalized Junction With Estimation *************************************
    //***************************************************************************************************************

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param estimationResults EstimationResults class
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> ForSignalizedJunction(AimsunApproach aimsunApproach, EstimationResults estimationResults,
                                                                                        List<AimsunControlPlanJunction> ActiveControlPlans,SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles from queue estimation results

        // Get necessary inputs
        Parameters parameters=estimationResults.getParameters(); // Parameters
        int [] EstimatedQueues=estimationResults.getAssessmentStateAndQueue().getQueueAssessment(); // Estimated queues
        String[] EstimatedStates=estimationResults.getAssessmentStateAndQueue().getStatusAssessment(); // Estimated states
        int EstimationTime=estimationResults.getTime(); // Current estimation time

        // A 2*3 Matrix: row (queued, moving), column (left turn, through, right turn)
        int[][] QueuedAndMovingVehicles=determinationOfQueuedAndMovingVehicles.AssignAccordingToPhaseInfAndCurrentTime
                (EstimationTime,EstimatedQueues,aimsunApproach,parameters, ActiveControlPlans);

        // Generate vehicles according to estimation results
        List<SimVehicle> simVehicleList=GenerateVehicles(aimsunApproach,EstimatedStates,QueuedAndMovingVehicles,
                simulationStatistics,parameters);
        return simVehicleList;
    }


    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param EstimatedStates String[] {for LT, for Through, for RT}
     * @param QueuedAndMovingVehicles A 2*3 Matrix: row (queued, moving), column (left turn, through, right turn)
     * @param simulationStatistics SimulationStatistics class
     * @param parameters Parameters class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> GenerateVehicles(AimsunApproach aimsunApproach,String[] EstimatedStates
            ,int [][] QueuedAndMovingVehicles,SimulationStatistics simulationStatistics,Parameters parameters){
        // This function is used to generate vehicle using the estimation results

        List<SimVehicle> simVehicleList = new ArrayList<SimVehicle>();

        // First: get the number of queued and moving vehicles
        int[] CurrentQueue=QueuedAndMovingVehicles[0]; //[left, through, right]
        int[] CurrentMoving=QueuedAndMovingVehicles[1]; //[left, through, right]

        // Second: assign vehicles to the corresponding links
        // Get the OD information within the links
        List<CentroidStatistics>  SelectedCentroidStatistics=new ArrayList<CentroidStatistics>();
        List<Integer> ListOfSections=aimsunApproach.getSectionBelongToApproach().getListOfSections();
        for(int i=0;i<ListOfSections.size();i++) { // Loop for each section
            for(int j=0;j<simulationStatistics.getCentroidStatisticsList().size();j++){ // Find the corresponding one
                if(simulationStatistics.getCentroidStatisticsList().get(j).getSectionID()==ListOfSections.get(i)){
                    SelectedCentroidStatistics.add(simulationStatistics.getCentroidStatisticsList().get(j));
                    break;
                }
            }
        }
        if(SelectedCentroidStatistics.size()==0){
            // No OD information, do nothing
            System.out.println("No OD information for the approach at Junction:"+aimsunApproach.getJunctionID()+
                    " and Section:"+aimsunApproach.getFirstSectionID());
        }else {

            // Place vehicles to the correct links
            boolean AssignmentDone=false; // Indicator to see whether assignment is done or not
            VehicleAssignmentTmpStr vehicleAssignmentTmpStr=new VehicleAssignmentTmpStr(null,null,
                    null,null);
            for(int i=0;i<5;i++){
                // Empirical adjustment
                // This loop is used to adjust the gap for moving vehicles (Jam spacing, jam spacing*threshold]
                // There may be more estimated vehicles than the link can handle, which sometimes requires to
                // shorten the gaps between moving vehicles.

                // Assign vehicles with OD, vehicle type, section id (may be updated), and lane id (may be updated)
                List<SimVehicle> VehWithODAndLaneQueued=assignVehicleWithODAndLane.InitialAssignment(CurrentQueue,
                        SelectedCentroidStatistics, aimsunApproach);
                List<SimVehicle> VehWithODAndLaneMoving=assignVehicleWithODAndLane.InitialAssignment(CurrentMoving,
                        SelectedCentroidStatistics, aimsunApproach);

                // Initialization of the list of vehicles: RearBoundaryByLane is not updated yet
                vehicleAssignmentTmpStr.setVehicleUnAssignedQueued(VehWithODAndLaneQueued);
                vehicleAssignmentTmpStr.setVehicleUnAssignedMoving(VehWithODAndLaneMoving);
                vehicleAssignmentTmpStr.setVehicleAssigned(new ArrayList<SimVehicle>());

                double Threshold=3.05-i*0.5;
                for(int j=0;j<ListOfSections.size();j++){// Note: Sections are ordered from downstream to upstream

                    //if(!DebugCases.CheckConsistencyOfSectionAssignment(vehicleAssignmentTmpStr.getVehicleAssigned())){
                    //    System.out.println("Inconsistent section ID in the initial assignment: Section="+ListOfSections.get(j)+" & Threshold="+Threshold);
                    //}

                    vehicleAssignmentTmpStr=AssignVehicleToOneSection(EstimatedStates,vehicleAssignmentTmpStr,aimsunApproach,
                            simulationStatistics,j,Threshold,parameters);

                    if(vehicleAssignmentTmpStr.getVehicleUnAssignedQueued().size()==0 &&
                            vehicleAssignmentTmpStr.getVehicleUnAssignedMoving().size()==0){
                        // If the assignment is done!
                        AssignmentDone=true;
                        break;
                    }else{ //If not, update the addresses
                        if(j<ListOfSections.size()-1){// Not the last section
                            // Reassign link ID and lane ID
                            vehicleAssignmentTmpStr=assignVehicleWithODAndLane.ReAssignment
                                    (vehicleAssignmentTmpStr,aimsunApproach,j+1,simulationStatistics);
                        }
                    }
                }
                if(AssignmentDone){ // After looping for all sections, check the status
                    break; // If all vehicles are assigned, break; else, reduce the threshold and try again
                }
            }
            if(!AssignmentDone){ // Still have unassigned vehicles
                System.out.println("Warning: there are still unassigned vehicles for Intersection:"+aimsunApproach.getJunctionID()+ " @ Section:"+
                        aimsunApproach.getFirstSectionID());
            }
            simVehicleList.addAll(vehicleAssignmentTmpStr.getVehicleAssigned());
        }
        return simVehicleList;
    }

    /**
     *
     * @param Status Estimation status
     * @param vehicleAssignmentTmpStr VehicleAssignmentTmpStr class
     * @param aimsunApproach AimsunApproach class
     * @param simulationStatistics SimulationStatistics class
     * @param SectionIdx Section Index
     * @param Threshold Vehicle assignment threshold
     * @param parameters Parameters class
     * @return VehicleAssignmentTmpStr class
     */
    public static VehicleAssignmentTmpStr AssignVehicleToOneSection(String[] Status,VehicleAssignmentTmpStr vehicleAssignmentTmpStr
            ,AimsunApproach aimsunApproach,SimulationStatistics simulationStatistics, int SectionIdx,double Threshold,Parameters parameters){
        // This function is used to assign vehicles to one section

        // Get the indicator of RearBoundaryByLane for the give section
        int SectionID=aimsunApproach.getSectionBelongToApproach().getListOfSections().get(SectionIdx);
        int NumOfLanes=aimsunApproach.getSectionBelongToApproach().getProperty().get(SectionIdx).getNumLanes();
        double[] LaneLengths=aimsunApproach.getSectionBelongToApproach().getProperty().get(SectionIdx).getLaneLengths().clone();
        double [][] RearBoundaryByLane=new double[NumOfLanes][3];
        double SectionLength=0;
        for(int i=0;i<NumOfLanes;i++){
            RearBoundaryByLane[i][0]=i+1;// Lane ID from left to Right
            RearBoundaryByLane[i][1]=LaneLengths[i]; // Max rear boundary, i.e., max lane length
            RearBoundaryByLane[i][2]=0; // Current rear boundary=0
            if(LaneLengths[i]>SectionLength){// Get section length
                SectionLength=LaneLengths[i];
            }
        }
        // Update RearBoundaryByLane according to Status for the most downstream section (the first section)
        if(SectionIdx==0){// The first downstream section
            RearBoundaryByLane=rearBoundaryAdjustment.AdjustWithState(RearBoundaryByLane, Status, aimsunApproach,parameters);
        }
        vehicleAssignmentTmpStr.setRearBoundaryByLane(RearBoundaryByLane); // Update the RearBoundaryByLane

        // Assign positions and speeds for queued vehicles
        vehicleAssignmentTmpStr=AssignVehicleToOneSectionForGivenVehicleCategory(vehicleAssignmentTmpStr
                ,"Queued", parameters,simulationStatistics,Threshold, aimsunApproach,SectionID,SectionLength);

        // Assign positions and speeds for moving vehicles
        vehicleAssignmentTmpStr=AssignVehicleToOneSectionForGivenVehicleCategory(vehicleAssignmentTmpStr
                , "Moving", parameters,simulationStatistics,Threshold, aimsunApproach,SectionID,SectionLength);

        return vehicleAssignmentTmpStr;
    }


    /**
     *
     * @param vehicleAssignmentTmpStr VehicleAssignmentTmpStr class
     * @param Category Queued or Moving
     * @param parameters Parameters class
     * @param simulationStatistics SimulationStatistics class
     * @param Threshold Vehicle assignment threshold
     * @param aimsunApproach AimsunApproach class
     * @param SectionID Section ID
     * @return VehicleAssignmentTmpStr class
     */
    public static VehicleAssignmentTmpStr AssignVehicleToOneSectionForGivenVehicleCategory(VehicleAssignmentTmpStr vehicleAssignmentTmpStr
            ,String Category,Parameters parameters,SimulationStatistics simulationStatistics,double Threshold
            , AimsunApproach aimsunApproach,int SectionID,double LinkLength){
        // This function is used to assign vehicle to one section for the given vehicle categority: queued or moving

        // Initialization
        List<SimVehicle> tmpSimVehicleUnassign=null;
        if(Category.equals("Queued")){
            tmpSimVehicleUnassign=vehicleAssignmentTmpStr.getVehicleUnAssignedQueued();
        }else if(Category.equals("Moving")){
            tmpSimVehicleUnassign=vehicleAssignmentTmpStr.getVehicleUnAssignedMoving();
        }else{
            System.out.println("Error in vehicle catetory: neither Queued nor Moving!");
            System.exit(-1);
        }

        double[][] RearBoundaryByLane=vehicleAssignmentTmpStr.getRearBoundaryByLane();
        List<SimVehicle> tmpSimVehicleAssign=vehicleAssignmentTmpStr.getVehicleAssigned();
        List<Integer> ListOfSections=aimsunApproach.getSectionBelongToApproach().getListOfSections();
        AimsunSection aimsunSection=null;
        for(int i=0;i<ListOfSections.size();i++){
            if(ListOfSections.get(i)==SectionID){
                aimsunSection=aimsunApproach.getSectionBelongToApproach().getProperty().get(i);
                break;
            }
        }

        if(tmpSimVehicleUnassign.size()>0) {// If the unassigned list is not empty

            List<SimVehicle> removeSimVehicles=new ArrayList<SimVehicle>(); // Temporarily store the vehicles just assigned to the network

            // Get the lane-centroid map
            Map<Integer,List<Integer>> LaneCentroidMap=getFunctions.GetCentroidLaneInformationFromSimVehicle(tmpSimVehicleUnassign);

            // Get lane statistics
            int NumOfLanes = RearBoundaryByLane.length;
            int VehicleType = 0; // Vehicle Type=0 means the statistics for all vehicles, just want to make it simple; Can be overwritten in the future
            Map<Integer,LaneStatistics> LaneStatisticsMap=new HashMap<Integer, LaneStatistics>();
            for(int i=0;i<NumOfLanes;i++){
                LaneStatistics tmpLaneStatistics =getFunctions.GetLaneInSectionStatistics(simulationStatistics, SectionID,
                        i+1, VehicleType);
                LaneStatisticsMap.put(i+1,tmpLaneStatistics);
            }

            //****************Level 1 assignment: assign vehicles to their predefined lanes********************
            for (int i = 0; i < NumOfLanes; i++) { // loop for each lane
                int LaneID = i + 1;
                // Get the list of simulation vehicles by lane
                List<SimVehicle> simVehiclesByLane = getFunctions.GetVehicleByLane(tmpSimVehicleUnassign, LaneID);

                if (simVehiclesByLane.size() > 0) {// If is not empty
                    // Get the lane statistics
                    LaneStatistics laneStatistics = LaneStatisticsMap.get(i+1);
                    // Loop for each vehicles
                    for (int j = 0; j < simVehiclesByLane.size(); j++) {
                        // Spacing & Speed
                        int TypeOfCurrentVehicle=simVehiclesByLane.get(j).getVehicleTypeInAimsun();
                        double[] SpacingAndSpeed =getFunctions.GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold,TypeOfCurrentVehicle);

                        if (RearBoundaryByLane[i][1] < RearBoundaryByLane[i][2]+SpacingAndSpeed[0]*2/3) {
                            // Reach the upstream boundary of the given lane "i"
                            break; // Do not assign vehicles
                        } else {
                            // Generate a new vehicle
                            double InitialPosition = LinkLength - RearBoundaryByLane[i][2];

                            double Speed = SpacingAndSpeed[1];
                            SimVehicle simVehicle = new SimVehicle(simVehiclesByLane.get(j).getSectionID(), simVehiclesByLane.get(j).getLaneID(),
                                    simVehiclesByLane.get(j).getVehicleTypeInAimsun(), simVehiclesByLane.get(j).getOriginCentroid(),
                                    simVehiclesByLane.get(j).getDestinationCentroid(), InitialPosition, Speed, simVehiclesByLane.get(j).isTrackOrNot());
                            // Add the new vehicle to the assigned list
                            tmpSimVehicleAssign.add(simVehicle);
                            // Update the rear boundary
                            RearBoundaryByLane[i][2] = RearBoundaryByLane[i][2] + SpacingAndSpeed[0];
                            // Remove the vehicle from the unassigned list
                            removeSimVehicles.add(simVehiclesByLane.get(j));
                        }
                    }
                }
            }
            // Remove all vehicles that have been assigned
            tmpSimVehicleUnassign.removeAll(removeSimVehicles);

            //****************Level 2 assignment: assign vehicles to the lanes with the same Destination********************
            if(tmpSimVehicleUnassign.size()>0){ // Still have unassigned vehicles

                removeSimVehicles=new ArrayList<SimVehicle>(); // Temporarily store the vehicles just assigned to the network

                // Redistribute the unassigned vehicles
                Collections.shuffle(tmpSimVehicleUnassign);

                for(int i=0;i<tmpSimVehicleUnassign.size();i++){// Loop for each unassigned vehicle
                    int CentroidID=tmpSimVehicleUnassign.get(i).getDestinationCentroid(); // Get the centroid ID
                    List<Integer> AvailableLanes=getFunctions.GetAvailableLanesForGivenCentroid(LaneCentroidMap,RearBoundaryByLane,
                            CentroidID,parameters,Threshold);
                    Collections.shuffle(AvailableLanes);// Redistribute the available lanes

                    if(AvailableLanes.size()>0){// Still have available lanes to assign vehicles
                        for(int j=0;j<AvailableLanes.size();j++){ // Loop for each available lane
                            int LaneID=AvailableLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            int TypeOfCurrentVehicle=tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun();
                            double[] SpacingAndSpeed =getFunctions.GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold,TypeOfCurrentVehicle); // Spacing & Speed

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]*2/3<RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2];

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).getSectionID(), LaneID,
                                        tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun(), tmpSimVehicleUnassign.get(i).getOriginCentroid(),
                                        tmpSimVehicleUnassign.get(i).getDestinationCentroid(), InitialPosition, Speed, tmpSimVehicleUnassign.get(i).isTrackOrNot());
                                // Add the new vehicle to the assigned list
                                tmpSimVehicleAssign.add(simVehicle);
                                // Update the rear boundary
                                RearBoundaryByLane[LaneID-1][2] = RearBoundaryByLane[LaneID-1][2] + SpacingAndSpeed[0];
                                // Remove the vehicle from the unassigned list
                                removeSimVehicles.add(tmpSimVehicleUnassign.get(i));
                                break; // Succeed, then break the loop for Available Lanes
                            }
                        }
                    }
                }
            }
            tmpSimVehicleUnassign.removeAll(removeSimVehicles);

            //****************Level 3 assignment: assign vehicles to adjacent lanes ******************************************
            if(tmpSimVehicleUnassign.size()>0) { // Still have unassigned vehicles

                removeSimVehicles=new ArrayList<SimVehicle>();

                // Redistribute the unassigned vehicles
                Collections.shuffle(tmpSimVehicleUnassign);

                // It is important to adjust the rear boundaries for level-3 and level-4 assignment for the most downstream link
                RearBoundaryByLane=rearBoundaryAdjustment.ReAdjustmentForAssignment(RearBoundaryByLane,aimsunApproach,parameters,SectionID);

                for(int i=0;i<tmpSimVehicleUnassign.size();i++){ // Loop for each unassigned vehicle
                    int LaneID = tmpSimVehicleUnassign.get(i).getLaneID();
                    List<Integer> AvailableAdjacentLanes=getFunctions.GetAvailableAdjacentLanesForGivenLane
                            (RearBoundaryByLane,parameters, LaneID,Threshold);
                    Collections.shuffle(AvailableAdjacentLanes); // Redistribute the available adjacent lanes

                    if(AvailableAdjacentLanes.size()>0){
                        // Adjacent lanes are available
                        for(int j=0;j<AvailableAdjacentLanes.size();j++){ // Loop for each available lane
                            LaneID=AvailableAdjacentLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            int TypeOfCurrentVehicle=tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun();
                            double[] SpacingAndSpeed =getFunctions.GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold,TypeOfCurrentVehicle); // Spacing & Speed

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]*2/3<RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2];

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).getSectionID(), LaneID,
                                        tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun(), tmpSimVehicleUnassign.get(i).getOriginCentroid(),
                                        tmpSimVehicleUnassign.get(i).getDestinationCentroid(), InitialPosition, Speed, tmpSimVehicleUnassign.get(i).isTrackOrNot());
                                // Add the new vehicle to the assigned list
                                tmpSimVehicleAssign.add(simVehicle);
                                // Update the rear boundary
                                RearBoundaryByLane[LaneID-1][2] = RearBoundaryByLane[LaneID-1][2] + SpacingAndSpeed[0];
                                // Remove the vehicle from the unassigned list
                                removeSimVehicles.add(tmpSimVehicleUnassign.get(i));
                                break; // Succeed, then break the loop for Available Lanes
                            }
                        }
                    }
                }
            }
            tmpSimVehicleUnassign.removeAll(removeSimVehicles);

            //****************Level 4 assignment: assign vehicles to available full lanes ******************************************
            if(tmpSimVehicleUnassign.size()>0){ // Still have unassigned vehicles
                removeSimVehicles=new ArrayList<SimVehicle>();

                List<Integer> AvailableFullLanes=getFunctions.GetAvailableFullLanes(RearBoundaryByLane,parameters, aimsunSection,Threshold);
                if(AvailableFullLanes.size()>0){
                    for(int i=0;i<tmpSimVehicleUnassign.size();i++){
                        // Shuffle the available full lanes
                        Collections.shuffle(AvailableFullLanes);

                        for(int j=0;j<AvailableFullLanes.size();j++){ // Loop for each available lane
                            int LaneID=AvailableFullLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            int TypeOfCurrentVehicle=tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun();
                            double[] SpacingAndSpeed =getFunctions.GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold,TypeOfCurrentVehicle); // Spacing & Speed

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]*2/3<RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2];

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).getSectionID(), LaneID,
                                        tmpSimVehicleUnassign.get(i).getVehicleTypeInAimsun(), tmpSimVehicleUnassign.get(i).getOriginCentroid(),
                                        tmpSimVehicleUnassign.get(i).getDestinationCentroid(), InitialPosition, Speed, tmpSimVehicleUnassign.get(i).isTrackOrNot());
                                // Add the new vehicle to the assigned list
                                tmpSimVehicleAssign.add(simVehicle);
                                // Update the rear boundary
                                RearBoundaryByLane[LaneID-1][2] = RearBoundaryByLane[LaneID-1][2] + SpacingAndSpeed[0];
                                // Remove the vehicle from the unassigned list
                                removeSimVehicles.add(tmpSimVehicleUnassign.get(i));
                                break; // Succeed, then break the loop for Available Lanes
                            }
                        }
                    }
                }
            }
            tmpSimVehicleUnassign.removeAll(removeSimVehicles);

            //****************Finally, update everything!******************************************
            // Update information and return
            if (Category.equals("Queued")) {
                vehicleAssignmentTmpStr.setVehicleUnAssignedQueued(tmpSimVehicleUnassign);
            } else if (Category.equals("Moving")) {
                vehicleAssignmentTmpStr.setVehicleUnAssignedMoving(tmpSimVehicleUnassign);
            }
            vehicleAssignmentTmpStr.setVehicleAssigned(tmpSimVehicleAssign);
            vehicleAssignmentTmpStr.setRearBoundaryByLane(RearBoundaryByLane);
        }
        return vehicleAssignmentTmpStr;
    }

}
