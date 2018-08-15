package estimation;

/**
 * Created by Qijian-Gan on 5/23/2018.
 */
import com.sun.org.apache.xpath.internal.operations.Or;
import gherkin.lexer.Fi;
import main.MainFunction;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;
import dataProvider.*;
import dataProvider.getSignalData;
import dataProvider.getSignalData.*;
import dataProvider.getDetectorData.*;
import estimation.trafficStateEstimation.*;
import dataProvider.getSimulationData.*;
import dataProvider.getEstimationResults.*;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.poi.ss.formula.functions.T;
import org.omg.Dynamic.Parameter;
import sun.misc.Signal;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.sql.*;
import java.util.*;

public class trafficInitialization {

    public static class SimVehicle{
        // This is the profile of simulated vehicles to be inserted into Aimsun
        public SimVehicle(int _SectionID, int _LaneID, int _VehicleTypeInAimsun, int _OriginCentroid, int _DestinationCentroid,
                          double _InitialPosition, double _InitialSpeed, boolean _TrackOrNot){
            this.SectionID=_SectionID;
            this.LaneID=_LaneID;
            this.VehicleTypeInAimsun=_VehicleTypeInAimsun;
            this.OriginCentroid=_OriginCentroid;
            this.DestinationCentroid=_DestinationCentroid;
            this.InitialPosition=_InitialPosition;
            this.InitialSpeed=_InitialSpeed;
            this.TrackOrNot=_TrackOrNot;
        }
        protected int SectionID;
        protected int LaneID;
        protected int VehicleTypeInAimsun;
        protected int OriginCentroid;
        protected int DestinationCentroid;
        protected double InitialPosition;
        protected double InitialSpeed;
        protected boolean TrackOrNot;

        public int getSectionID() {
            return SectionID;
        }

        public int getLaneID() {
            return LaneID;
        }

        public int getVehicleTypeInAimsun() {
            return VehicleTypeInAimsun;
        }

        public int getOriginCentroid() {
            return OriginCentroid;
        }

        public int getDestinationCentroid() {
            return DestinationCentroid;
        }

        public double getInitialPosition() {
            return InitialPosition;
        }

        public double getInitialSpeed() {
            return InitialSpeed;
        }

    }

    public static class VehicleAssignmentTmpStr{
        // This is the temporary structure during the vehicle assignment process
        public VehicleAssignmentTmpStr(List<SimVehicle> _VehicleUnAssignedQueued, List<SimVehicle> _VehicleUnAssignedMoving,
                                       List<SimVehicle> _VehicleAssigned, double [][] _RearBoundaryByLane){
            this.VehicleUnAssignedQueued=_VehicleUnAssignedQueued;
            this.VehicleUnAssignedMoving=_VehicleUnAssignedMoving;
            this.VehicleAssigned=_VehicleAssigned;
            this.RearBoundaryByLane=_RearBoundaryByLane;
        }
        protected List<SimVehicle> VehicleUnAssignedQueued; // List of unassigned queued vehicles
        protected List<SimVehicle> VehicleUnAssignedMoving; // List of unassigned moving vehicles
        protected List<SimVehicle> VehicleAssigned; // List of vehicles assigned to a particular link and lane
        protected double [][] RearBoundaryByLane; // Rear boundary by lane at a given section
    }

    //***************************************************************************************************************
    //***************************************************************************************************************
    //************************** Main Function: Vehicle Generation  *************************************************
    //***************************************************************************************************************
    //***************************************************************************************************************
    /**
     *
     * @param aimsunNetworkByApproach List<AimsunApproach>
     * @param estimationResultsList Map<Integer,EstimationResults> <FirstSectionID, EstimationResults>
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGeneration(List<AimsunApproach> aimsunNetworkByApproach, Map<Integer,EstimationResults> estimationResultsList
            ,List<AimsunControlPlanJunction> ActiveControlPlans, SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles for Aimsun

        List<SimVehicle> simVehicleList=new ArrayList<SimVehicle>();

        if(aimsunNetworkByApproach.size()>0){
            for(int i=0;i<aimsunNetworkByApproach.size();i++){// Loop for each approach
                System.out.println("Junction ID="+ aimsunNetworkByApproach.get(i).getJunctionID()+", Junction Name="+
                        aimsunNetworkByApproach.get(i).getJunctionName()+ ", First Section ID="+aimsunNetworkByApproach.get(i).getFirstSectionID());
                if(aimsunNetworkByApproach.get(i).getSignalized()==1){ // if it is a signalized junction
                    int FirstSectionID=aimsunNetworkByApproach.get(i).getFirstSectionID(); // Get the first section in the downstream
                    if(estimationResultsList.containsKey(FirstSectionID)){
                        // If we have estimated queues(non-negative values: -1 means no information; -2 means no such a movement)
                        List<SimVehicle> tmpListSimVehicle=VehicleGenerationForSignalizedJunctionWithEstimation(aimsunNetworkByApproach.get(i),
                                estimationResultsList.get(FirstSectionID),ActiveControlPlans,simulationStatistics);
                        simVehicleList.addAll(tmpListSimVehicle);
                    }else{// When no queue estimates, use the simulated traffic states from Aimsun instead
                        //List<SimVehicle> tmpListSimVehicle=VehicleGenerationForSignalizedJunctionWithSimulation(aimsunNetworkByApproach.get(i),
                        //        simulationStatistics);
                        //simVehicleList.addAll(tmpListSimVehicle);
                    }
                }
                else{// If it is not a signalized junction, e.g., freeway junction
                    // If BEATs simulation is not available, use the Aimsun simulation results
                    //List<SimVehicle> tmpListSimVehicle=VehicleGenerationForUnsignalizedJunctionWithSimulation(aimsunNetworkByApproach.get(i),
                    //        simulationStatistics);
                    //simVehicleList.addAll(tmpListSimVehicle);
                }
            }
        }

        // Re-adjust lane ID before inserting vehicles back to Aimsun
        // This is SO SAD that: Lane ID is labeled from 0 to N-1 (left to right)in GK functions, but from N to 1 (right to left) in AKI functions
        // We need to use the AKI function to insert vehicles back to Aimsun
        simVehicleList=ReAdjustLaneIDForSimVehicles(simVehicleList, aimsunNetworkByApproach);

        // Return the list of sim vehicles
        return simVehicleList;
    }

    //***************************************************************************************************************
    //*************** Lane ID Adjustment ****************************************************************************
    //***************************************************************************************************************
    /**
     *
     * @param simVehicleList List<SimVehicle> class
     * @param aimsunNetworkByApproach List<AimsunApproach> class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> ReAdjustLaneIDForSimVehicles(List<SimVehicle> simVehicleList,List<AimsunApproach> aimsunNetworkByApproach){
        // This function is used to adjust lane ID for sim vehicles
        Map<Integer,Integer> SectionNumLanesMap=GenerateSectionNumLanesMap(aimsunNetworkByApproach);

        for(int i=0;i<simVehicleList.size();i++){ // Loop for each sim vehicle
            int SectionID=simVehicleList.get(i).SectionID;
            int CurLaneID=simVehicleList.get(i).LaneID;
            int NumLanes=SectionNumLanesMap.get(SectionID);
            int LaneIDAimsun=NumLanes-CurLaneID+1; // Update the lane ID
            simVehicleList.get(i).LaneID=LaneIDAimsun; // Reset it
        }
        return simVehicleList;
    }

    /**
     *
     * @param aimsunNetworkByApproach List<AimsunApproach> aimsunNetworkByApproach class
     * @return Map<Integer,Integer>
     */
    public static Map<Integer,Integer> GenerateSectionNumLanesMap(List<AimsunApproach> aimsunNetworkByApproach){
        // This function is used to generate section-Num Lanes map

        Map<Integer, Integer> SectionNumLanes=new HashMap<Integer, Integer>();
        for(int i=0;i<aimsunNetworkByApproach.size();i++){ // Loop for each approach
            for(int j=0;j<aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().size();j++){// Loop for each section
                int SectionID=aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().get(j).getSectionID();
                int NumOfLanes=aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().get(j).getNumLanes();
                SectionNumLanes.put(SectionID,NumOfLanes); // Create the hash map
            }
        }
        return SectionNumLanes;
    }

    //***************************************************************************************************************
    //*************** Vehicle Generation At Signalized Junction *****************************************************
    //***************************************************************************************************************

    //*************** Vehicle Generation At Signalized Junction With Estimation *************************************

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param estimationResults EstimationResults class
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGenerationForSignalizedJunctionWithEstimation(AimsunApproach aimsunApproach, EstimationResults estimationResults,
            List<AimsunControlPlanJunction> ActiveControlPlans,SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles from queue estimation results

        // Get necessary inputs
        Parameters parameters=estimationResults.getParameters(); // Parameters
        int [] EstimatedQueues=estimationResults.getAssessmentStateAndQueue().QueueAssessment; // Estimated queues
        String[] EstimatedStates=estimationResults.getAssessmentStateAndQueue().StatusAssessment; // Estimated states
        int EstimationTime=estimationResults.getTime(); // Current estimation time

        // A 2*3 Matrix: row (queued, moving), column (left turn, through, right turn)
        int[][] QueuedAndMovingVehicles=AssignQueuedAndMovingAccordingToPhaseInfAndCurrentTime(EstimationTime,EstimatedQueues
                ,aimsunApproach,parameters, ActiveControlPlans);

        // Generate vehicles according to estimation results
        List<SimVehicle> simVehicleList=GenerateVehicleWithEstimation(aimsunApproach,EstimatedStates,QueuedAndMovingVehicles,
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
    public static List<SimVehicle> GenerateVehicleWithEstimation(AimsunApproach aimsunApproach,String[] EstimatedStates
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
            // Assign vehicles with OD, vehicle type, section id (may be updated), and lane id (may be updated)
            List<SimVehicle> VehWithODAndLaneQueued=AssignVehicleWithODAndLane(CurrentQueue, SelectedCentroidStatistics, aimsunApproach);
            List<SimVehicle> VehWithODAndLaneMoving=AssignVehicleWithODAndLane(CurrentMoving, SelectedCentroidStatistics, aimsunApproach);

            // Place vehicles to the correct links
            boolean AssignmentDone=false; // Indicator to see whether assignment is done or not
            VehicleAssignmentTmpStr vehicleAssignmentTmpStr=new VehicleAssignmentTmpStr(null,null,
                    null,null);
            for(int i=0;i<5;i++){
                // Empirical adjustment
                // This loop is used to adjust the gap for moving vehicles (Jam spacing, jam spacing*threshold]
                // There may be more estimated vehicles than the link can handle, which sometimes requires to
                // shorten the gaps between moving vehicles.

                // Initialization of the list of vehicles: RearBoundaryByLane is not updated yet
                vehicleAssignmentTmpStr.VehicleUnAssignedQueued=new ArrayList<SimVehicle>(VehWithODAndLaneQueued.size());
                vehicleAssignmentTmpStr.VehicleUnAssignedQueued.addAll(VehWithODAndLaneQueued);
                vehicleAssignmentTmpStr.VehicleUnAssignedMoving=new ArrayList<SimVehicle>(VehWithODAndLaneMoving.size());
                vehicleAssignmentTmpStr.VehicleUnAssignedMoving.addAll(VehWithODAndLaneMoving);
                vehicleAssignmentTmpStr.VehicleAssigned=new ArrayList<SimVehicle>();

                double Threshold=3.05-i*0.5;
                for(int j=0;j<ListOfSections.size();j++){// Note: Sections are ordered from downstream to upstream
                    vehicleAssignmentTmpStr=AssignVehicleToOneSection(EstimatedStates,vehicleAssignmentTmpStr,aimsunApproach,
                            simulationStatistics,j,Threshold,parameters);

                    if(vehicleAssignmentTmpStr.VehicleUnAssignedQueued.size()==0 &&
                            vehicleAssignmentTmpStr.VehicleUnAssignedMoving.size()==0){
                        // If the assignment is done!
                        AssignmentDone=true;
                        break;
                    }else{ //If not, update the addresses
                        if(j<ListOfSections.size()-1){// Not the last section
                            // Reassign link ID and lane ID
                            vehicleAssignmentTmpStr=ReAssignedSectionAndLane(vehicleAssignmentTmpStr,aimsunApproach,j+1,simulationStatistics);
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
            simVehicleList.addAll(vehicleAssignmentTmpStr.VehicleAssigned);
        }
        return simVehicleList;
    }

    /**
     *
     * @param vehicleAssignmentTmpStr VehicleAssignmentTmpStr class
     * @param aimsunApproach AimsunApproach class
     * @param NexSectionIdx Next Section Index
     * @param simulationStatistics SimulationStatistics class
     * @return VehicleAssignmentTmpStr class
     */
    public static VehicleAssignmentTmpStr ReAssignedSectionAndLane(VehicleAssignmentTmpStr vehicleAssignmentTmpStr,AimsunApproach aimsunApproach
            ,int NexSectionIdx,SimulationStatistics simulationStatistics){
        // This function is used to reassign section and lane to unassigned vehicles

        int SectionID=aimsunApproach.getSectionBelongToApproach().getListOfSections().get(NexSectionIdx);
        AimsunSection aimsunSection=aimsunApproach.getSectionBelongToApproach().getProperty().get(NexSectionIdx);
        int NumLanes=aimsunSection.getNumLanes();
        List<SimVehicle> VehicleUnAssignedQueued=vehicleAssignmentTmpStr.VehicleUnAssignedQueued;
        List<SimVehicle> VehicleUnAssignedMoving=vehicleAssignmentTmpStr.VehicleUnAssignedMoving;

        // Get next section's centroid statistics
        CentroidStatistics centroidStatistics=null;
        for(int i=0;i<simulationStatistics.getCentroidStatisticsList().size();i++){
            if(simulationStatistics.getCentroidStatisticsList().get(i).getSectionID()==SectionID){
                centroidStatistics=simulationStatistics.getCentroidStatisticsList().get(i);
            }
        }

        // Assign section and lanes
        Random random = new Random();
        if(centroidStatistics==null){// No centroid statistics, Randomly assign lanes
            // For queued vehicles first
            for(int i=0;i<VehicleUnAssignedQueued.size();i++){
                int LaneID=random.nextInt(NumLanes)+1;
                VehicleUnAssignedQueued.get(i).LaneID=LaneID;
                VehicleUnAssignedQueued.get(i).SectionID=SectionID;
            }
            // For moving vehicles next
            for(int i=0;i<VehicleUnAssignedMoving.size();i++){
                int LaneID=random.nextInt(NumLanes)+1;
                VehicleUnAssignedMoving.get(i).LaneID=LaneID;
                VehicleUnAssignedMoving.get(i).SectionID=SectionID;
            }
        }else{ // Have centroid statistics

            Map<Integer,List<Integer>> LaneCentroidMap=GetCentroidLaneInformationFromCentroidStatistics(centroidStatistics);
            // For queued vehicles first
            for(int i=0;i<VehicleUnAssignedQueued.size();i++){ // Loop for each unassigned vehicle
                int CentroidID=VehicleUnAssignedQueued.get(i).DestinationCentroid; // Get the destination centroid
                int LaneID;
                if(LaneCentroidMap.containsKey(CentroidID)){ // Find the right centroid?
                    List<Integer> AvailableLanes=LaneCentroidMap.get(CentroidID);
                    Collections.shuffle(AvailableLanes);// Redistribute the lanes
                    LaneID=AvailableLanes.get(0); // Get the first value
                }else{
                    LaneID=random.nextInt(NumLanes)+1;
                }
                VehicleUnAssignedQueued.get(i).LaneID=LaneID;
                VehicleUnAssignedQueued.get(i).SectionID=SectionID;
            }
            // For moving vehicles next
            for(int i=0;i<VehicleUnAssignedMoving.size();i++){ // Loop for each unassigned vehicle
                int CentroidID=VehicleUnAssignedMoving.get(i).DestinationCentroid; // Get the destination centroid
                int LaneID;
                if(LaneCentroidMap.containsKey(CentroidID)){ // Find the right centroid?
                    List<Integer> AvailableLanes=LaneCentroidMap.get(CentroidID);
                    Collections.shuffle(AvailableLanes);// Redistribute the lanes
                    LaneID=AvailableLanes.get(0); // Get the first value
                }else{
                    LaneID=random.nextInt(NumLanes)+1;
                }
                VehicleUnAssignedMoving.get(i).LaneID=LaneID;
                VehicleUnAssignedMoving.get(i).SectionID=SectionID;
            }
        }

        // Update the information and return
        vehicleAssignmentTmpStr.VehicleUnAssignedMoving=VehicleUnAssignedMoving;
        vehicleAssignmentTmpStr.VehicleUnAssignedQueued=VehicleUnAssignedQueued;
        return vehicleAssignmentTmpStr;
    }


    /**
     *
     * @param centroidStatistics CentroidStatistics class
     * @return  Map<Integer,List<Integer>> LaneCentroidMap
     */
    public static Map<Integer,List<Integer>> GetCentroidLaneInformationFromCentroidStatistics(CentroidStatistics centroidStatistics){
        // This function is used to get the lane-centroid information from centroid statistics

        // Get the list of centroids
        List<Integer> ListOfCentroids=new ArrayList<Integer>();
        HashSet<Integer> UniqueCentroids=new HashSet<Integer>();
        for(int i=0;i<centroidStatistics.getODList().size();i++){
            if(UniqueCentroids.add(centroidStatistics.getODList().get(i)[1])){
                ListOfCentroids.add(centroidStatistics.getODList().get(i)[1]);
            }
        }

        // Get the centroid-lane mapping
        Map<Integer,List<Integer>> LaneCentroidMap=new HashMap<Integer, List<Integer>>();
        for(int i=0;i<ListOfCentroids.size();i++){ // Loop for each destination centorid
            int CentroidID=ListOfCentroids.get(i);
            List<Integer> AvailableLanes=new ArrayList<Integer>();
            HashSet<Integer> uniqueLanes=new HashSet<Integer>();
            List<ODByLane> ODByLaneList=centroidStatistics.getODListByLane();
            for(int j=0;j<ODByLaneList.size();j++){ // For each lane
                int LaneID=ODByLaneList.get(j).getLaneID(); // Get the lane IC
                for(int k=0;k<ODByLaneList.get(j).getODList().size();k++){ // Loop for each OD pair [Origin, Destination, VehicleType]
                    if(ODByLaneList.get(j).getODList().get(k)[1]==CentroidID){
                        if(uniqueLanes.add(LaneID)){// Find a new lane?
                            AvailableLanes.add(LaneID);
                        }
                    }
                }
            }
            LaneCentroidMap.put(CentroidID,AvailableLanes);
        }
        return LaneCentroidMap;
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
        double[] LaneLengths=aimsunApproach.getSectionBelongToApproach().getProperty().get(SectionIdx).getLaneLengths();
        double [][] RearBoundaryByLane=new double[NumOfLanes][3];
        double SectionLength=0;
        for(int i=0;i<NumOfLanes;i++){
            RearBoundaryByLane[i][0]=i+1;// Lane ID from left to Right
            RearBoundaryByLane[i][1]=LaneLengths[i]; // Max rear boundary, i.e., max lane length
            RearBoundaryByLane[i][2]=0; // Current rear boundary=0
            if(LaneLengths[i]>SectionLength){
                SectionLength=LaneLengths[i];
            }
        }
        // Update RearBoundaryByLane according to Status for the most downstream section (the first section)
        if(SectionIdx==0){// The first downstream section
             RearBoundaryByLane=ReAdjustRearBoundaryWithState(RearBoundaryByLane, Status, aimsunApproach,parameters);
        }
        vehicleAssignmentTmpStr.RearBoundaryByLane=RearBoundaryByLane; // Update the RearBoundaryByLane

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
     * @param RearBoundaryByLane double [][]
     * @param Status String[] Estimated traffic states
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @return double [][] RearBoundaryByLane
     */
    public static double[][] ReAdjustRearBoundaryWithState(double [][] RearBoundaryByLane, String[] Status, AimsunApproach aimsunApproach,
                                                           Parameters parameters){
        // This function is used to adjust the rear boundaries according to the estimated traffic states, particularly for left turns and right turns

        for(int i=0;i<Status.length;i++){ // Loop for each turning movement
            if(Status[i].contains("Lane Blockage")){// If we find lane blockage
                // get the lane-turning property
                List<LaneTurningProperty> laneTurningPropertyList=aimsunApproach.getLaneTurningProperty();
                int FirstSectionID=aimsunApproach.getFirstSectionID();
                LaneTurningProperty laneTurningProperty=null;
                for(int j=0;j<laneTurningPropertyList.size();j++){
                    if(laneTurningPropertyList.get(j).getSectionID()==FirstSectionID){
                        laneTurningProperty=laneTurningPropertyList.get(j);
                        break;
                    }
                }

                // Get the aimsun turning property
                List<AimsunTurning> aimsunTurningList=aimsunApproach.getTurningBelongToApproach().getTurningProperty();

                // Get the movement information
                String Movement="";
                if(i==0){
                    Movement="Left Turn";
                }else if(i==1){
                    Movement="Through";
                }else if(i==2){
                    Movement="Right Turn";
                }

                // Adjust the rear boundary condition
                for(int j=0;j<laneTurningProperty.getLanes().size();j++){// Loop for each lane
                    if(laneTurningProperty.getLanes().get(j).getIsExclusive()==1){// If it is exclusive lane
                        int TurnID=laneTurningProperty.getLanes().get(j).getTurningMovements().get(0);// Get the turn ID
                        int LaneID=laneTurningProperty.getLanes().get(j).getLaneID(); // Get the lane ID
                        for(int k=0;k<aimsunTurningList.size();k++){ // Try to find the turn movement information
                            if(aimsunTurningList.get(k).getTurnID()==TurnID){
                                if(aimsunTurningList.get(k).getMovement().contains(Movement)){// Get the right turn with given movement
                                    double DistanceToStopbar=GetDistanceToStopbar(aimsunApproach,parameters); // Get the distance to stopbar
                                    RearBoundaryByLane[LaneID-1][2]=DistanceToStopbar; // Adjust the rear boundary
                                }
                            }
                        }
                    }
                }
            }
        }

        return RearBoundaryByLane;
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
            ,String Category,Parameters parameters,SimulationStatistics simulationStatistics,double Threshold, AimsunApproach aimsunApproach
            ,int SectionID,double LinkLength){
        // This function is used to assign vehicle to one section for the given vehicle categority: queued or moving

        // Initialization
        List<SimVehicle> tmpSimVehicleUnassign=null;
        if(Category.equals("Queued")){
            tmpSimVehicleUnassign=vehicleAssignmentTmpStr.VehicleUnAssignedQueued;
        }else if(Category.equals("Moving")){
            tmpSimVehicleUnassign=vehicleAssignmentTmpStr.VehicleUnAssignedMoving;
        }else{
            System.out.println("Error in vehicle catetory: neither Queued nor Moving!");
            System.exit(-1);
        }
        double[][] RearBoundaryByLane=vehicleAssignmentTmpStr.RearBoundaryByLane;
        List<SimVehicle> tmpSimVehicleAssign=vehicleAssignmentTmpStr.VehicleAssigned;
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
            Map<Integer,List<Integer>> LaneCentroidMap=GetCentroidLaneInformationFromSimVehicle(tmpSimVehicleUnassign);

            // Get lane statistics
            int NumOfLanes = RearBoundaryByLane.length;
            int VehicleType = 0; // Vehicle Type=0 means the statistics for all vehicles, just want to make it simple; Can be overwritten in the future
            Map<Integer,LaneStatistics> LaneStatisticsMap=new HashMap<Integer, LaneStatistics>();
            for(int i=0;i<NumOfLanes;i++){
                LaneStatistics tmpLaneStatistics = GetLaneInSectionStatistics(simulationStatistics, SectionID, i+1, VehicleType);
                LaneStatisticsMap.put(i+1,tmpLaneStatistics);
            }

            //****************Level 1 assignment: assign vehicles to their predefined lanes********************
            for (int i = 0; i < NumOfLanes; i++) { // loop for each lane
                int LaneID = i + 1;
                // Get the list of simulation vehicles by lane
                List<SimVehicle> simVehiclesByLane = SelectVehicleByLane(tmpSimVehicleUnassign, LaneID);

                if (simVehiclesByLane.size() > 0) {// If is not empty
                    // Get the lane statistics
                    LaneStatistics laneStatistics = LaneStatisticsMap.get(i+1);
                    // Loop for each vehicles
                    for (int j = 0; j < simVehiclesByLane.size(); j++) {
                        double[] SpacingAndSpeed = GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold); // Spacing & Speed
                        // Adjust the spacing for different vehicle types
                        int TypeForIndividualVehicle=simVehiclesByLane.get(j).VehicleTypeInAimsun;
                        SpacingAndSpeed=AdjustSpacingForDifferentVehicleTypes(TypeForIndividualVehicle,SpacingAndSpeed, parameters,Threshold);

                        if (RearBoundaryByLane[i][1] < RearBoundaryByLane[i][2]+SpacingAndSpeed[0]/2) {
                            // Reach the upstream boundary of the given lane "i"
                            break; // Do not assign vehicles
                        } else {
                            // Generate a new vehicle
                            double InitialPosition = LinkLength - RearBoundaryByLane[i][2] - SpacingAndSpeed[0] / 2;
                            // Re-adjust Initial position, not out of bound
                            // It only affects moving vehicles & queued vehicles should be find
                            double LaneLength=RearBoundaryByLane[simVehiclesByLane.get(j).LaneID-1][1];
                            InitialPosition=Math.max(InitialPosition,LinkLength-LaneLength);

                            double Speed = SpacingAndSpeed[1];
                            SimVehicle simVehicle = new SimVehicle(simVehiclesByLane.get(j).SectionID, simVehiclesByLane.get(j).LaneID,
                                    simVehiclesByLane.get(j).VehicleTypeInAimsun, simVehiclesByLane.get(j).OriginCentroid, simVehiclesByLane.get(j).DestinationCentroid,
                                    InitialPosition, Speed, simVehiclesByLane.get(j).TrackOrNot);
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
                    int CentroidID=tmpSimVehicleUnassign.get(i).DestinationCentroid; // Get the centroid ID
                    List<Integer> AvailableLanes=GetAvailableLanesForGivenCentroid(LaneCentroidMap,RearBoundaryByLane,CentroidID,parameters);
                    Collections.shuffle(AvailableLanes);// Redistribute the available lanes

                    if(AvailableLanes.size()>0){// Still have available lanes to assign vehicles
                        for(int j=0;j<AvailableLanes.size();j++){ // Loop for each available lane
                            int LaneID=AvailableLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            double[] SpacingAndSpeed = GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold); // Spacing & Speed

                            int TypeForIndividualVehicle=tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun;
                            SpacingAndSpeed=AdjustSpacingForDifferentVehicleTypes(TypeForIndividualVehicle,SpacingAndSpeed, parameters,Threshold);

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]/2<=RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2] - SpacingAndSpeed[0] / 2;
                                // Re-adjust Initial position, not out of bound
                                // It only affects moving vehicles & queued vehicles should be find
                                double LaneLength=RearBoundaryByLane[LaneID-1][1];
                                InitialPosition=Math.max(InitialPosition,LinkLength-LaneLength);

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).SectionID, LaneID,
                                        tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun, tmpSimVehicleUnassign.get(i).OriginCentroid,
                                        tmpSimVehicleUnassign.get(i).DestinationCentroid, InitialPosition, Speed, tmpSimVehicleUnassign.get(i).TrackOrNot);
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
                RearBoundaryByLane=ReAdjustRearBoundaryForAssignment(RearBoundaryByLane,aimsunApproach,parameters,SectionID);

                for(int i=0;i<tmpSimVehicleUnassign.size();i++){ // Loop for each unassigned vehicle
                    int LaneID = tmpSimVehicleUnassign.get(i).LaneID;
                    List<Integer> AvailableAdjacentLanes=GetAvailableAdjacentLanesForGivenLane(RearBoundaryByLane,parameters, LaneID);
                    Collections.shuffle(AvailableAdjacentLanes); // Redistribute the available adjacent lanes

                    if(AvailableAdjacentLanes.size()>0){
                        // Adjacent lanes are available
                        for(int j=0;j<AvailableAdjacentLanes.size();j++){ // Loop for each available lane
                            LaneID=AvailableAdjacentLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            double[] SpacingAndSpeed = GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold); // Spacing & Speed

                            int TypeForIndividualVehicle=tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun;
                            SpacingAndSpeed=AdjustSpacingForDifferentVehicleTypes(TypeForIndividualVehicle,SpacingAndSpeed, parameters,Threshold);

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]/2<=RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2] - SpacingAndSpeed[0] / 2;
                                // Re-adjust Initial position, not out of bound
                                // It only affects moving vehicles & queued vehicles should be find
                                double LaneLength=RearBoundaryByLane[LaneID-1][1];
                                InitialPosition=Math.max(InitialPosition,LinkLength-LaneLength);

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).SectionID, LaneID,
                                        tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun, tmpSimVehicleUnassign.get(i).OriginCentroid,
                                        tmpSimVehicleUnassign.get(i).DestinationCentroid, InitialPosition, Speed, tmpSimVehicleUnassign.get(i).TrackOrNot);
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

                List<Integer> AvailableFullLanes=GetAvailableFullLanes(RearBoundaryByLane,parameters, aimsunSection);
                if(AvailableFullLanes.size()>0){
                    for(int i=0;i<tmpSimVehicleUnassign.size();i++){
                        // Shuffle the available full lanes
                        Collections.shuffle(AvailableFullLanes);

                        for(int j=0;j<AvailableFullLanes.size();j++){ // Loop for each available lane
                            int LaneID=AvailableFullLanes.get(j); // Get the lane ID
                            LaneStatistics laneStatistics = LaneStatisticsMap.get(LaneID); // Get the lane statistics
                            double[] SpacingAndSpeed = GetSpacingAndSpeed(laneStatistics, Category, parameters, Threshold); // Spacing & Speed

                            int TypeForIndividualVehicle=tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun;
                            SpacingAndSpeed=AdjustSpacingForDifferentVehicleTypes(TypeForIndividualVehicle,SpacingAndSpeed, parameters,Threshold);

                            if(RearBoundaryByLane[LaneID-1][2]+SpacingAndSpeed[0]/2<=RearBoundaryByLane[LaneID-1][1])// Have enough space to add
                            {
                                // Generate a new vehicle
                                double InitialPosition = LinkLength - RearBoundaryByLane[LaneID - 1][2] - SpacingAndSpeed[0] / 2;
                                // Re-adjust Initial position, not out of bound
                                // It only affects moving vehicles & queued vehicles should be find
                                double LaneLength=RearBoundaryByLane[LaneID-1][1];
                                InitialPosition=Math.max(InitialPosition,LinkLength-LaneLength);

                                double Speed = SpacingAndSpeed[1];
                                // New vehicle with a new lane ID
                                SimVehicle simVehicle = new SimVehicle(tmpSimVehicleUnassign.get(i).SectionID, LaneID,
                                        tmpSimVehicleUnassign.get(i).VehicleTypeInAimsun, tmpSimVehicleUnassign.get(i).OriginCentroid,
                                        tmpSimVehicleUnassign.get(i).DestinationCentroid, InitialPosition, Speed, tmpSimVehicleUnassign.get(i).TrackOrNot);
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
                vehicleAssignmentTmpStr.VehicleUnAssignedQueued = tmpSimVehicleUnassign;
            } else if (Category.equals("Moving")) {
                vehicleAssignmentTmpStr.VehicleUnAssignedMoving = tmpSimVehicleUnassign;
            }
            vehicleAssignmentTmpStr.VehicleAssigned = tmpSimVehicleAssign;
            vehicleAssignmentTmpStr.RearBoundaryByLane = RearBoundaryByLane;
        }
        return vehicleAssignmentTmpStr;
    }

    /**
     *
     * @param TypeForIndividualVehicle Vehicle type
     * @param SpacingAndSpeed double[spacing, speed]
     * @param parameters Parameters class
     * @param Threshold Threshold for assignment
     * @return double[spacing, speed]
     */
    public static double[] AdjustSpacingForDifferentVehicleTypes(int TypeForIndividualVehicle,double[] SpacingAndSpeed,
                                                                 Parameters parameters,double Threshold){
        // This function is used to adjust spacing for different vehicle types

        if(TypeForIndividualVehicle>1){
            // Not regular vehicle
            if(SpacingAndSpeed[0]==parameters.getVehicleParams().getJamSpacing()){
                // Queued vehicle?
                SpacingAndSpeed[0]=SpacingAndSpeed[0]*2; // Passenger car equivalent
            }else{
                SpacingAndSpeed[0]=SpacingAndSpeed[0]*(Math.max(Threshold,2))/Threshold; // Passenger car equivalent
            }
        }
        return SpacingAndSpeed;
    }

    /**
     *
     * @param RearBoundaryByLane double[][]
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @param SectionID Section ID
     * @return double[][] RearBoundaryByLane (Updated one)
     */
    public static double[][] ReAdjustRearBoundaryForAssignment(double[][] RearBoundaryByLane,AimsunApproach aimsunApproach,Parameters parameters, int SectionID){
        // This function is used to adjust rear boundaries if the section is the most downstream one
        // It is necessary in the level-3 and level 4 assignment: not to assign vehicles within region from stopbar to advance detectors/Turning pocket
        if(SectionID==aimsunApproach.getFirstSectionID()){
            // Need to adjust the rear boundaries

            // Get the distance to stopbar
            double DistanceToStopbar=GetDistanceToStopbar(aimsunApproach,parameters);

            // Get the full-lane indicator
            int[] IsFullLane=aimsunApproach.getSectionBelongToApproach().getProperty().get(0).getIsFullLane(); // The first section

            // Adjust the rear boundaries
            for(int i=0;i<IsFullLane.length;i++){ // Loop for each lane
                if(IsFullLane[i]==1){// Is full lane?
                    RearBoundaryByLane[i][2]=Math.max(RearBoundaryByLane[i][2],DistanceToStopbar);// Get the maximum boundary
                }
            }
        }
        return RearBoundaryByLane;
    }

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @return DistanceToStopbar
     */
    public static double GetDistanceToStopbar(AimsunApproach aimsunApproach,Parameters parameters){
        // This function is used to get distance to the stopbar

        // Get the distance to stopbar
        List<DetectorMovementProperty> AdvanceDetectors=aimsunApproach.getDetectorProperty().getAdvanceDetectors();
        double DistanceToStopbar=0;
        if(AdvanceDetectors.size()>0){ // If advance detectors exist
            // Have advance detectors
            for(int i=0;i<AdvanceDetectors.size();i++){// Loop for each detector type
                for(int j=0;j<AdvanceDetectors.get(i).getDistancesToStopbar().size();j++){ // Loop for each detector with the same type
                    if(DistanceToStopbar<AdvanceDetectors.get(i).getDistancesToStopbar().get(j)){
                        DistanceToStopbar=AdvanceDetectors.get(i).getDistancesToStopbar().get(j);// Get the maximum distance
                    }
                }
            }
        }
        if(DistanceToStopbar==0){// If not found
            DistanceToStopbar=parameters.intersectionParams.DistanceAdvanceDetector;
        }
        // Adjust distance to stopbar according to the section length
        double[] LaneLengths=aimsunApproach.getSectionBelongToApproach().getProperty().get(0).getLaneLengths();
        Arrays.sort(LaneLengths);
        double SectionLength=LaneLengths[LaneLengths.length-1]; // Get the last one (maximum)
        DistanceToStopbar=Math.min(parameters.intersectionParams.DistanceAdvanceDetector,SectionLength);

        // Adjust distance to stopbar according to the turning pocket
        int[] IsFullLane=aimsunApproach.getSectionBelongToApproach().getProperty().get(0).getIsFullLane();
        double TurningPocket=0;
        for(int i=0;i<IsFullLane.length;i++){
            if(IsFullLane[i]==0 & TurningPocket<LaneLengths[i]){
                TurningPocket=LaneLengths[i]; // Get the maximum turning pocket
            }
        }
        if(TurningPocket>0){ // Have turning pocket?
            DistanceToStopbar=Math.min(DistanceToStopbar,TurningPocket);
        }

        // Return value
        return DistanceToStopbar;
    }

    /**
     *
     * @param RearBoundaryByLane double[][]
     * @param parameters Parameters class
     * @param aimsunSection AimsunSection class
     * @return List<Integer> AvailableFullLanes
     */
    public static List<Integer> GetAvailableFullLanes(double[][] RearBoundaryByLane,Parameters parameters, AimsunSection aimsunSection){
        // This function is used to get available full lanes
        List<Integer> AvailableFullLanes=new ArrayList<Integer>();

        int[] IsFullLane=aimsunSection.getIsFullLane(); // Get the full-lane indicator
        for(int i=0;i<IsFullLane.length;i++){// Loop for each lane
            if(RearBoundaryByLane[i][1]>=RearBoundaryByLane[i][2]+parameters.getVehicleParams().getJamSpacing()){ // Still have space
                if(IsFullLane[i]==1){// Is a full lane
                    AvailableFullLanes.add(i+1); // Append it to the end
                }
            }
        }
        return AvailableFullLanes;
    }

    /**
     *
     * @param RearBoundaryByLane double[][]
     * @param parameters Parameter class
     * @param LaneID Lane ID
     * @return List<Integer> AvailableAdjacentLanes
     */
    public static List<Integer> GetAvailableAdjacentLanesForGivenLane(double[][] RearBoundaryByLane,Parameters parameters, int LaneID){
        // This function is to find available adjacent lanes for a given lane
        List<Integer> AvailableAdjacentLanes=new ArrayList<Integer>();

        int StartLane=Math.max(LaneID-1,0); // Adjacent lane on the left
        int Numlanes=RearBoundaryByLane.length;
        int EndLane=Math.min(Numlanes-1,LaneID); // Adjacent lane on the right

        for(int i=StartLane;i<=EndLane;i++){
            if(RearBoundaryByLane[i][1]>=RearBoundaryByLane[i][2]+parameters.getVehicleParams().getJamSpacing()){
                // Still able to add vehicles
                AvailableAdjacentLanes.add(i+1); // Add the lane ID
            }
        }
        return AvailableAdjacentLanes;
    }

    /**
     *
     * @param LaneCentroidMap Map<Integer,List<Integer>>
     * @param RearBoundaryByLane double[][]
     * @param CentroidID Destination centroid ID
     * @param parameters Parameters class
     * @return List<Integer> AvailableLanes (that can accommodate new vehicles)
     */
    public static List<Integer> GetAvailableLanesForGivenCentroid(Map<Integer,List<Integer>> LaneCentroidMap, double[][] RearBoundaryByLane
            ,int CentroidID,Parameters parameters){
        // This function is used to get available lanes for a given centorid

        List<Integer> AvailableLanes=new ArrayList<Integer>();

        List<Integer> Lanes=LaneCentroidMap.get(CentroidID); // Get the lane information for the given centroid
        for(int i=0;i<Lanes.size();i++){ // Loop for each lane
            if(RearBoundaryByLane[Lanes.get(i)-1][1]>RearBoundaryByLane[Lanes.get(i)-1][2]+parameters.vehicleParams.getJamSpacing()){
                // Still can assign a least one vehicle?
                AvailableLanes.add(Lanes.get(i));
            }
        }
        return AvailableLanes;
    }

    /**
     *
     * @param simVehicleList List<SimVehicle> class
     * @return Map<Integer,List<Integer>> LaneCentroidMap
     */
    public static Map<Integer,List<Integer>> GetCentroidLaneInformationFromSimVehicle(List<SimVehicle> simVehicleList){
        // This function is used to get the lane-centroid information from sim-vehicles

        // Get the list of centroids (destination)
        List<Integer> ListOfCentroids=GetListOfDestinationsFromSimVehicles(simVehicleList);

        Map<Integer,List<Integer>> LaneCentroidMap=new HashMap<Integer, List<Integer>>();
        for(int i=0;i<ListOfCentroids.size();i++){ // Loop for each destination centorid
            int CentroidID=ListOfCentroids.get(i);
            List<Integer> AvailableLanes=new ArrayList<Integer>();
            HashSet<Integer> uniqueLanes=new HashSet<Integer>();
            for(int j=0;j<simVehicleList.size();j++){ // Get the associated lanes
                if(simVehicleList.get(j).DestinationCentroid==CentroidID && uniqueLanes.add(simVehicleList.get(j).LaneID)){
                    AvailableLanes.add(simVehicleList.get(j).LaneID);
                }
            }
            LaneCentroidMap.put(CentroidID,AvailableLanes);
        }
        return LaneCentroidMap;
    }

    /**
     *
     * @param simVehicleList List<SimVehicle> class
     * @return List<Integer> destinationList
     */
    public static List<Integer> GetListOfDestinationsFromSimVehicles(List<SimVehicle> simVehicleList){
        // This function is used to get the list of desinations from the given Sim-Vehicles

        List<Integer> destinationList=new ArrayList<Integer>();
        HashSet<Integer> uniqueDestination=new HashSet<Integer>();
        for(int i=0;i<simVehicleList.size();i++){
            if(uniqueDestination.add(simVehicleList.get(i).DestinationCentroid)){
                destinationList.add(simVehicleList.get(i).DestinationCentroid);
            }
        }
        return destinationList;
    }

    /**
     *
     * @param laneStatistics LaneStatistics class
     * @param Category Queued or Moving
     * @param parameters Parameter class
     * @param Threshold Spacing threshold
     * @return double [Spacing, Speed]
     */
    public static double [] GetSpacingAndSpeed(LaneStatistics laneStatistics, String Category,Parameters parameters, double Threshold){
        // This function is used to get spacing and speed for given category

        double Spacing=0;
        double Speed=0;
        double JamSpacing=parameters.vehicleParams.JamSpacing;
        if(Category.equals("Queued")){// For queued vehicles
            Speed=0;
            Spacing=JamSpacing;
        }else if(Category.equals("Moving")){ // For moving vehicles
            // Spacing is uniformly selected between JamSpacing and JamSpacing*Threshold
            RealDistribution rnd=new UniformRealDistribution(JamSpacing,JamSpacing*Threshold);
            Spacing=rnd.sample();

            if(laneStatistics.getLaneSpeed()>=0 && laneStatistics.getLaneSpeedStd()>=0) { // If lane speed and speed std are available
                NormalDistribution rnd2 = new NormalDistribution(laneStatistics.getLaneSpeed(), laneStatistics.getLaneSpeedStd());
                Speed=rnd2.sample();
            }else{ // If not availalbe
                Random random = new Random();
                Speed=parameters.intersectionParams.SaturationSpeedThrough*random.nextFloat(); // Ramdomly select between 0 and Saturation Speed For Through Vehicles
            }
        }else{
            System.out.println("Unknown vehicle category: neither Queued nor Moving!");
            System.exit(-1);
        }
        return new double[]{Spacing, Speed};
    }

    /**
     *
     * @param simVehicleList List<SimVehicle> class
     * @param LaneID Lane ID
     * @return List<SimVehicle>
     */
    public static List<SimVehicle> SelectVehicleByLane(List<SimVehicle> simVehicleList, int LaneID){
        // This function is used to select vehicles by lane ID
        List<SimVehicle> tmpSimVehicle=new ArrayList<SimVehicle>();

        for(int i=0;i<simVehicleList.size();i++){
            if(simVehicleList.get(i).LaneID==LaneID){
                tmpSimVehicle.add(simVehicleList.get(i));
            }
        }
        return tmpSimVehicle;
    }

    /**
     *
     * @param simulationStatistics SimulationStatistics class
     * @param SectionID Section ID
     * @param LaneID Lane ID
     * @param VehicleType Vehicle Type
     * @return LaneStatistics class
     */
    public static LaneStatistics GetLaneInSectionStatistics(SimulationStatistics simulationStatistics, int SectionID, int LaneID, int VehicleType){
        // This function is used to get lane statistics for a given section and lane.

        LaneStatistics laneStatistics=null;
        List<LaneStatistics> laneStatisticsList;
        List<LaneStatisticsByObjectID> laneStatisticsByObjectIDList=simulationStatistics.getLaneStatisticsByObjectIDList();
        if(laneStatisticsByObjectIDList.size()>1){
            // More than one object inside the sqlite database
            long LatestExecDateTime=laneStatisticsByObjectIDList.get(0).getExecDateTime();
            int Index=0;
            for(int i=1;i<laneStatisticsByObjectIDList.size();i++){
                if(LatestExecDateTime<laneStatisticsByObjectIDList.get(i).getExecDateTime()){
                    // Find the latest one?
                    LatestExecDateTime=laneStatisticsByObjectIDList.get(i).getExecDateTime();
                    Index=i; // Update the index
                }
            }
            laneStatisticsList=laneStatisticsByObjectIDList.get(Index).getLaneStatisticsList();// Return the latest simulation information
        }else{// Only one object inside the sqlite database
            laneStatisticsList=laneStatisticsByObjectIDList.get(0).getLaneStatisticsList();
        }

        // Loop for the right lane statistics
        for(int i=0;i<laneStatisticsList.size();i++){
            if(laneStatisticsList.get(i).getSectionID()==SectionID && laneStatisticsList.get(i).getLaneID()==LaneID &&
                    laneStatisticsList.get(i).getVehicleType()==VehicleType){
                laneStatistics=laneStatisticsList.get(i);
                break;
            }
        }
        return laneStatistics;
    }

    /**
     *
     * @param VehiclesForThreeMovements int[]{Left Turn, Through, Right Turn}
     * @param centroidStatisticsList List<CentroidStatistics> class
     * @param aimsunApproach AimsunApproach class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> AssignVehicleWithODAndLane(int[]VehiclesForThreeMovements, List<CentroidStatistics>
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
                List<Integer> SelectLanes=GetLanesForGivenMovement(aimsunApproach, Movement); // Lanes for the right movement
                if(SelectLanes.size()==0){
                    // No such a turn, then no vehicles are generated
                    System.out.println("No such a turn for Movement:"+Movement+" at Junction:"+aimsunApproach.getJunctionID());
                }else{
                    // If have such a turn with one for more lanes
                    // Get the OD information from the downstream section, which has a bettern distribution of OD among lanes
                    int FirstSectionID=aimsunApproach.getFirstSectionID();
                    List<int[]> SelectODs=GetODsForGivenLanes(FirstSectionID,SelectLanes,centroidStatisticsList);

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
     * @param FirstSectionID First section ID
     * @param SelectLanes Selected Lane IDs
     * @param centroidStatisticsList List<CentroidStatistics> class
     * @return List<int[]> ODForFirstSection
     */
    public static List<int[]> GetODsForGivenLanes(int FirstSectionID, List<Integer> SelectLanes
            ,List<CentroidStatistics> centroidStatisticsList){
        // This function is used to get OD information for given lanes associated with a movement

        // Get the OD information for the whole approach
        List<int []> ODForApproach=new ArrayList<int[]>();
        CentroidStatistics centroidStatistics=null;
        for(int i=0;i<centroidStatisticsList.size();i++){
            ODForApproach.addAll(centroidStatisticsList.get(i).getODList());
            if(centroidStatisticsList.get(i).getSectionID()==FirstSectionID){ // If find the same section ID
                centroidStatistics=centroidStatisticsList.get(i); //Get the centroid statistics
            }
        }

        if(centroidStatistics.equals(null)){
            // If no OD information for the first section, return the values for the whole approach
            return ODForApproach;
        }else{// If yes
            List<int[]> ODForFirstSection=new ArrayList<int[]>();
            for(int i=0;i<centroidStatistics.getDownstreamODListByLane().size();i++){ // Loop for the downstream list
                for(int j=0;j<SelectLanes.size();j++){ // Loop for the select lanes
                    if(centroidStatistics.getDownstreamODListByLane().get(i).getLaneID()==SelectLanes.get(j)){ // Find the same lane ID
                        ODForFirstSection.addAll(centroidStatistics.getDownstreamODListByLane().get(i).getODList());
                        break;
                    }
                }
            }

            if(ODForFirstSection.size()==0){ // If no downstream OD information, check the OD in the section by lane
                for(int i=0;i<centroidStatistics.getODListByLane().size();i++){
                    for(int j=0;j<SelectLanes.size();j++){
                        if(centroidStatistics.getODListByLane().get(i).getLaneID()==SelectLanes.get(j)){ // Find the same lane ID
                            ODForFirstSection.addAll(centroidStatistics.getODListByLane().get(i).getODList());
                            break;
                        }
                    }
                }
            }

            if(ODForFirstSection.size()==0){ // Still no information?
                ODForFirstSection=ODForApproach;      // Use the OD information for the whole approach
            }
            return ODForFirstSection;
        }
    }

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param Movement Movement: Left Turn, Through, and Right Turn
     * @return List<Integer> SelectLanes
     * Note: Lanes are labeled from left to right (1 to N), which is different from Aimsun's AKI functions
     */
    public static List<Integer> GetLanesForGivenMovement(AimsunApproach aimsunApproach, String Movement){
        // This function is used to get the lanes a movements is using

        List<Integer> SelectLanes=new ArrayList<Integer>();
        HashSet<Integer> hashSet=new HashSet<Integer>();
        // Turning Property of the first section at the given approach
        List<AimsunTurning> TurningProperty=aimsunApproach.getTurningBelongToApproach().getTurningProperty();

        for(int i=0;i<TurningProperty.size();i++){ // Loop for each turn
            if(TurningProperty.get(i).getMovement().equals(Movement)){ // Find the right movement
                int FromLane= TurningProperty.get(i).getOrigFromLane();
                int ToLane=TurningProperty.get(i).getOrigToLane();
                for(int j=FromLane;j<=ToLane;j++){
                    if(hashSet.add(j)){ // Add the lanes, if possible
                        SelectLanes.add(j);
                    }
                }
            }
        }
        return SelectLanes;
    }

    /**
     *
     * @param EstimationTime Estimation Time
     * @param EstimationQueues Estimation Queues int[3]
     * @param aimsunApproach AimsunApproach (class)
     * @param parameters Parameters (class)
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @return QueuedAndMovingVehicles: A 2*3 Matrix: row (queued, moving), column (left turn, through, right turn)
     */
    public static int[][] AssignQueuedAndMovingAccordingToPhaseInfAndCurrentTime(int EstimationTime,int[] EstimationQueues, AimsunApproach aimsunApproach
            , Parameters parameters, List<AimsunControlPlanJunction> ActiveControlPlans){
        // This function is used to assign queued and moving vehicles according to the phase information and current estimation time

        // Initialization
        int [][] QueuedAndMovingVehicles=new int[2][3];
        for(int i=0;i<2;i++){ // Queued & Moving
            for(int j=0;j<3;j++){ // Left-turn, Through, Right-turn
                QueuedAndMovingVehicles[i][j]=0;
            }
        }

        if(ActiveControlPlans.size()==0){
            // If no active control plans available
            for(int j=0;j<3;j++){
                if(EstimationQueues[j]>0){ // Half for queued vehicles, and the other half for moving vehicles
                    QueuedAndMovingVehicles[0][j]= (int) Math.ceil(EstimationQueues[j]*0.5); // Queued vehicles
                    QueuedAndMovingVehicles[1][j]= (int) Math.ceil(EstimationQueues[j]*0.5); // Moving Vehicles
                }
            }
        }else{
            // Get the phase information for the given approach and time with the list of active control plans
            // The control plans can be the actual ones from the field or the one extracted from Aimsun
            PhaseInfForApproach phaseInfForApproach=getSignalData.GetSignalPhasingForGivenApproachAndTimeFromAimsun
                    (aimsunApproach, EstimationTime, ActiveControlPlans,parameters);

            for(int i=0;i<EstimationQueues.length;i++){
                if(EstimationQueues[i]>0){
                    double Headway=parameters.intersectionParams.SaturationHeadway;
                    // Get the signal by Movement
                    String Movement=null;
                    if(i==0){
                        Movement="Left Turn";
                    }
                    if(i==1){
                        Movement="Through";
                    }
                    if(i==2){
                        Movement="Right Turn";
                    }
                    SignalByMovement signalByMovement=GetCorrespondingSigalByMovement(phaseInfForApproach.getSignalByMovementList(),Movement);

                    // Get the Maximum and Minimum numbers of queued vehicles (some engineering adjustments inside)
                    int [] MaxMinQueuedVehicles=DetermineMaxAndMinVehicles(EstimationQueues[i],signalByMovement,Headway);

                    // Determine the proportions of queued and moving vehicles
                    int[] NumQueuedAndMoving=DetermineQueuedAndMovingVehicles(MaxMinQueuedVehicles, signalByMovement);

                    QueuedAndMovingVehicles[0][i]=NumQueuedAndMoving[0]; // Queued vehicles
                    QueuedAndMovingVehicles[1][i]=NumQueuedAndMoving[1]; // Moving vehicles
                }
            }
        }
        return QueuedAndMovingVehicles;
    }

    /**
     *
     * @param signalByMovementList  List<SignalByMovement> class
     * @param Movement Movement: Left Turn, Through, Right Turn
     * @return SignalByMovement class
     */
    public static SignalByMovement GetCorrespondingSigalByMovement(List<SignalByMovement> signalByMovementList, String Movement){
        // Get the corresponding signal by movement

        // Find the right signal information with the given movement
        List<SignalByMovement> selectedSignalByMovement=new ArrayList<SignalByMovement>();
        for(int i=0;i<signalByMovementList.size();i++){
            if(signalByMovementList.get(i).getMovement().equals(Movement)){
                // Find the right movement
                selectedSignalByMovement.add(signalByMovementList.get(i));
            }
        }

        // Return results
        if(selectedSignalByMovement.size()==1){ // Find the right signal by movement
            return selectedSignalByMovement.get(0);
        }else if(selectedSignalByMovement.size()>1){
            // Have more than one turns with the given movement, e.g. U-turn and Left-turn
            double MaxGreen=selectedSignalByMovement.get(0).getGreenTime();
            int Index=0;
            // Get the one with max green
            for(int j=1;j<selectedSignalByMovement.size();j++){
                if(selectedSignalByMovement.get(j).getGreenTime()>MaxGreen){
                    MaxGreen=selectedSignalByMovement.get(j).getGreenTime();
                    Index=j;
                }
            }
            return selectedSignalByMovement.get(Index);
        }else{
            System.out.println("Can not find the signal information with the given movement!");
            System.exit(-1);
            return null;
        }
    }

    /**
     *
     * @param AverageQueue Average Queue
     * @param signalByMovement SignalByMovement class
     * @param Headway Vehcle saturation headway
     * @return MaxMinQueue: int [min, max]
     */
    public static int[] DetermineMaxAndMinVehicles(int AverageQueue,SignalByMovement signalByMovement,double Headway){
        // This function is used to determine the maximum and minimum numbers of queued vehicles
        int [] MaxMinQueue =new int[]{0,0}; // [Min, Max]

        double GreenTime=signalByMovement.getGreenTime();
        double NumVehicleByGreen=GreenTime/Headway;

        // Get the minimum number of queued vehicles
        if(NumVehicleByGreen/2<AverageQueue){
            // If the green time is not enough to clear all queued vehicles
            MaxMinQueue[0]=(int) Math.ceil((AverageQueue*2-NumVehicleByGreen)/2); // Residual queue
        }else{
            MaxMinQueue[0]=0;
        }

        // Get the maximum number of queued vehicles
        // This is a very rough estimate, assuming the maximum queue is at most twice of the average queue
        MaxMinQueue[1]=Math.min(AverageQueue*2,(int)Math.ceil(NumVehicleByGreen)+MaxMinQueue[0]);
        return MaxMinQueue;
    }

    /**
     *
     * @param MaxMinQueuedVehicles int[min, max]
     * @param signalByMovement SignalByMovement class
     * @return NumQueuedAndMoving: int[queued, moving]
     */
    public static int[] DetermineQueuedAndMovingVehicles(int [] MaxMinQueuedVehicles, SignalByMovement signalByMovement){
        // This function is used to determine the proportions of queued and moving vehicles based on the signal input
        // This part contains some engineering adjustments

        int [] NumQueuedAndMoving=new int[]{0,0}; // Queued (first)& Moving (next)

        // We are assuming the number of vehicles within an approach is the maximum number of queued vehicles.
        // Depending on the signal settings, there will be a proportion of moving vehicles, and a proportion of queued ones
        if(signalByMovement.getCurrentStatus().equals("Green")){
            if(signalByMovement.getDurationSinceActivated()>signalByMovement.getGreenTime()){
                System.out.println("Wrong green time and activation duration for the given movement: "+ signalByMovement.getMovement());
                System.exit(-1);
            }else{
                double proportion=signalByMovement.getDurationSinceActivated()/signalByMovement.getGreenTime();
                NumQueuedAndMoving[1]=(int) Math.ceil((MaxMinQueuedVehicles[1]-MaxMinQueuedVehicles[0])*proportion); // Moving
                NumQueuedAndMoving[0]=Math.max(0,MaxMinQueuedVehicles[1]-NumQueuedAndMoving[1]); // The rest are queued vehicles
            }
        }else if(signalByMovement.getCurrentStatus().equals("Red")){
            if(signalByMovement.getDurationSinceActivated()>signalByMovement.getRedTime()){
                System.out.println("Wrong red time and activation duration for the given movement: "+ signalByMovement.getMovement());
                System.exit(-1);
            }else{
                double proportion=signalByMovement.getDurationSinceActivated()/signalByMovement.getRedTime();
                NumQueuedAndMoving[0]=MaxMinQueuedVehicles[0]+(int) Math.ceil((MaxMinQueuedVehicles[1]-MaxMinQueuedVehicles[0])*proportion); // Queued
                NumQueuedAndMoving[1]=Math.max(0,MaxMinQueuedVehicles[1]-NumQueuedAndMoving[0]); // The rest are moving vehicles
            }
        }else{
            System.out.println("Unknown signal status for the given movement: "+ signalByMovement.getMovement());
            System.exit(-1);
        }
        return NumQueuedAndMoving;
    }



    //*************** Vehicle Generation At Signalized Junction With Simulation *************************************
    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGenerationForSignalizedJunctionWithSimulation(AimsunApproach aimsunApproach
            , SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles from the backup simulations in Aimsun

        List<SimVehicle> simVehicleList=VehicleGenerationFromSimulation(aimsunApproach, simulationStatistics.getSimVehInfBySectionList());
        return simVehicleList;
    }

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param simVehInfBySectionList List<SimVehInfBySection> class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGenerationFromSimulation(AimsunApproach aimsunApproach,
                                                                   List<SimVehInfBySection> simVehInfBySectionList){
        // This function is used to generate vehicles using simulation results

        // Columns in the MySQl Table:
        // Time, VehicleID, VehicleType, SectionID, LaneID, CurrentPosition, CurrentSpeed, CentroidOrigin,
        // CentroidDestination, DistanceToEnd
        List<SimVehicle> simVehicleList=new ArrayList<SimVehicle>();

        int JunctionID=aimsunApproach.getJunctionID();
        int FirstSectionID=aimsunApproach.getFirstSectionID();
        List<Integer> ListOfSections=aimsunApproach.getSectionBelongToApproach().getListOfSections();

        // Select the vehicle trajectories within the ListOfSections
        List<SimVehInfBySection> SelectedSimVehInf=new ArrayList<SimVehInfBySection>();
        for(int i=0;i<ListOfSections.size();i++){// Loop for each section
            for(int j=0;j<simVehInfBySectionList.size();j++){ // Loop for the available set of sections
                if(simVehInfBySectionList.get(j).getSectionID()==ListOfSections.get(i)){// Find the right one
                    SelectedSimVehInf.add(simVehInfBySectionList.get(j)); // Append it to the end
                    break;
                }
            }
        }

        // Generate simulated vehicles
        if(SelectedSimVehInf.size()==0){
            System.out.println("No simulation data available for the approach with Junction:"+JunctionID+
                    " and Section:"+FirstSectionID);
        }else{
            for(int i=0;i<SelectedSimVehInf.size();i++){
                for(int j=0;j<SelectedSimVehInf.get(i).getAimsunVehInfList().size();j++){
                    int SectionID=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getSectionID();
                    int LaneID=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getLaneID();
                    int VehicleTypeInAimsun=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getVehicleType();
                    int OriginCentroid=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getCentroidOrigin();
                    int DestinationCentroid=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getCentroidDestination();
                    double InitialPosition=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getCurrentPosition();
                    double InitialSpeed=SelectedSimVehInf.get(i).getAimsunVehInfList().get(j).getCurrentSpeed();
                    boolean TrackOrNot=false;
                    SimVehicle simVehicle=new SimVehicle(SectionID,LaneID,VehicleTypeInAimsun,OriginCentroid,DestinationCentroid
                            ,InitialPosition,InitialSpeed,TrackOrNot);
                    simVehicleList.add(simVehicle);
                }
            }
        }
        return simVehicleList;
    }

    //***************************************************************************************************************
    //*************** Vehicle Generation At UnSignalized Junction (Freeway) *****************************************
    //***************************************************************************************************************

    //*************** Vehicle Generation At UnSignalized Junction With Simulation ***********************************
    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGenerationForUnsignalizedJunctionWithSimulation(AimsunApproach aimsunApproach
            , SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles for the unsignalized junction with Aimsun simulation backup
        List<SimVehicle> simVehicleList=VehicleGenerationFromSimulation(aimsunApproach, simulationStatistics.getSimVehInfBySectionList());
        return simVehicleList;
    }

    //***************************************************************************************************************
    //***************************************************************************************************************
    //************************** Active Phase Determination**********************************************************
    //***************************************************************************************************************
    //***************************************************************************************************************
    public static void ActivePhaseDetermination(){
        // This function is used to determine active phases



    }


}
