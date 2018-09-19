package initialization;

import dataProvider.getSimulationData.*;
import dataProvider.getSimulationData;
import commonClass.forInitialization.*;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;
import initialization.trafficInitialization.*;

import java.util.*;

public class assignVehicleWithODAndLane {

    /**
     *
     * @param VehiclesForThreeMovements int[]{Left Turn, Through, Right Turn}
     * @param centroidStatisticsList List<CentroidStatistics> class
     * @param aimsunApproach AimsunApproach class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> InitialAssignment(int[]VehiclesForThreeMovements, List<CentroidStatistics>
            centroidStatisticsList, AimsunApproach aimsunApproach ){
        // This function is used to assign vehicles with OD and Lane

        List<SimVehicle> VehWithODAndLane=new ArrayList<SimVehicle>();
        // Loop for each movement: Left-turn, through, and right-turn
        for(int i=0;i<3;i++){
            if(VehiclesForThreeMovements[i]>0){
                String Movement;
                if(i==0){
                    Movement="Left Turn";
                }else if(i==1){
                    Movement="Through";
                }else{
                    Movement="Right Turn";
                }
                List<Integer> SelectLanes=getFunctions.GetLanesForGivenMovement(aimsunApproach, Movement); // Lanes for the right movement
                if(SelectLanes.size()==0){
                    // No such a turn, then no vehicles are generated
                    System.out.println("No such a turn for Movement:"+Movement+" at Junction:"+aimsunApproach.getJunctionID());
                }else{
                    // If have such a turn with one for more lanes
                    // Get the OD information from the downstream section, which has a bettern distribution of OD among lanes
                    int FirstSectionID=aimsunApproach.getFirstSectionID();
                    List<int[]> SelectODs=getFunctions.GetODsForGivenLanes(FirstSectionID,SelectLanes,centroidStatisticsList);

                    if(SelectODs.size()==0){ // If no OD information, no vehicles are generated
                        System.out.println("No OD information for for Movement:"+Movement+" at Junction:"+aimsunApproach.getJunctionID());
                    }else {
                        //Randomly assign OD to each vehicle
                        Random random = new Random();
                        for (int j = 0; j < VehiclesForThreeMovements[i]; j++) { // Loop for each vehicle
                            // Randomly select the OD: it is ok to pick the same index for successive calls since we sample that
                            // from the distribution of SelectODs.
                            // random.nextInt(n): Returns a random number between 0 (inclusive) and n (exclusive).
                            int[] OD = SelectODs.get(random.nextInt(SelectODs.size()));
                            int Origin = OD[0]; // Origin ID
                            int Destination = OD[1]; // Destimation ID
                            int VehicleType = OD[2]; // Vehicle Type in Aimsun

                            int SectionID = FirstSectionID;// Will be updated if not enough space to accommodate
                            int LaneID = SelectLanes.get(random.nextInt(SelectLanes.size())); // Will be updated if not enough space to accommodate
                            int InitialPosition = -1; // Undetermined
                            int InitialSpeed = -1;// Undetermined
                            boolean Tracking = false; // No tracking
                            SimVehicle simVehicle = new SimVehicle(SectionID, LaneID, VehicleType, Origin, Destination, InitialPosition,
                                    InitialSpeed, Tracking);
                            VehWithODAndLane.add(simVehicle);
                        }
                    }
                }
            }
        }
        return VehWithODAndLane;
    }

    /**
     *
     * @param vehicleAssignmentTmpStr VehicleAssignmentTmpStr class
     * @param aimsunApproach AimsunApproach class
     * @param NexSectionIdx Next Section Index
     * @param simulationStatistics SimulationStatistics class
     * @return VehicleAssignmentTmpStr class
     */
    public static VehicleAssignmentTmpStr ReAssignment(VehicleAssignmentTmpStr vehicleAssignmentTmpStr, AimsunApproach aimsunApproach
            , int NexSectionIdx, SimulationStatistics simulationStatistics){
        // This function is used to reassign section and lane to unassigned vehicles

        int SectionID=aimsunApproach.getSectionBelongToApproach().getListOfSections().get(NexSectionIdx);
        AimsunSection aimsunSection=aimsunApproach.getSectionBelongToApproach().getProperty().get(NexSectionIdx);
        int NumLanes=aimsunSection.getNumLanes();
        List<SimVehicle> VehicleUnAssignedQueued=vehicleAssignmentTmpStr.getVehicleUnAssignedQueued();
        List<SimVehicle> VehicleUnAssignedMoving=vehicleAssignmentTmpStr.getVehicleUnAssignedMoving();

        // Get next section's centroid statistics
        getSimulationData.CentroidStatistics centroidStatistics=null;
        for(int i=0;i<simulationStatistics.getCentroidStatisticsList().size();i++){
            if(simulationStatistics.getCentroidStatisticsList().get(i).getSectionID()==SectionID){
                centroidStatistics=simulationStatistics.getCentroidStatisticsList().get(i);
            }
        }

        // Assign section and lanes
        // Different from the most downstream section, for the upstream sections,
        // we consider vehicles can be randomly assigned to any lanes within the same centroid
        Random random = new Random();
        if(centroidStatistics==null){// No centroid statistics, Randomly assign lanes
            // For queued vehicles first
            for(int i=0;i<VehicleUnAssignedQueued.size();i++){
                int LaneID=random.nextInt(NumLanes)+1;
                VehicleUnAssignedQueued.get(i).setLaneID(LaneID);
                VehicleUnAssignedQueued.get(i).setSectionID(SectionID);
            }
            // For moving vehicles next
            for(int i=0;i<VehicleUnAssignedMoving.size();i++){
                int LaneID=random.nextInt(NumLanes)+1;
                VehicleUnAssignedMoving.get(i).setLaneID(LaneID);
                VehicleUnAssignedMoving.get(i).setSectionID(SectionID);
            }
        }else{ // Have centroid statistics
            Map<Integer,List<Integer>> LaneCentroidMap=getFunctions.GetCentroidLaneInformationFromCentroidStatistics(centroidStatistics);
            // For queued vehicles first
            for(int i=0;i<VehicleUnAssignedQueued.size();i++){ // Loop for each unassigned vehicle
                int CentroidID=VehicleUnAssignedQueued.get(i).getDestinationCentroid(); // Get the destination centroid
                int LaneID;
                if(LaneCentroidMap.containsKey(CentroidID)){ // Find the right centroid?
                    List<Integer> AvailableLanes=LaneCentroidMap.get(CentroidID);
                    Collections.shuffle(AvailableLanes);// Redistribute the lanes
                    LaneID=AvailableLanes.get(0); // Get the first value
                }else{
                    LaneID=random.nextInt(NumLanes)+1;
                }
                VehicleUnAssignedQueued.get(i).setLaneID(LaneID);
                VehicleUnAssignedQueued.get(i).setSectionID(SectionID);
            }
            // For moving vehicles next
            for(int i=0;i<VehicleUnAssignedMoving.size();i++){ // Loop for each unassigned vehicle
                int CentroidID=VehicleUnAssignedMoving.get(i).getDestinationCentroid(); // Get the destination centroid
                int LaneID;
                if(LaneCentroidMap.containsKey(CentroidID)){ // Find the right centroid?
                    List<Integer> AvailableLanes=LaneCentroidMap.get(CentroidID);
                    Collections.shuffle(AvailableLanes);// Redistribute the lanes
                    LaneID=AvailableLanes.get(0); // Get the first value
                }else{
                    LaneID=random.nextInt(NumLanes)+1;
                }
                VehicleUnAssignedMoving.get(i).setLaneID(LaneID);
                VehicleUnAssignedMoving.get(i).setSectionID(SectionID);
            }
        }

        // Update the information and return
        vehicleAssignmentTmpStr.setVehicleUnAssignedMoving(VehicleUnAssignedMoving);
        vehicleAssignmentTmpStr.setVehicleUnAssignedQueued(VehicleUnAssignedQueued);
        return vehicleAssignmentTmpStr;
    }


}
