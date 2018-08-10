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

import org.apache.poi.ss.formula.functions.T;
import sun.misc.Signal;

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
    }

    public static class VehicleAssignmentTmpStr{
        // This is the temporary structure during the vehicle assignment process
        public VehicleAssignmentTmpStr(int _IdxQueuedVeh, int _IdxMovingVeh, List<SimVehicle> _VehicleAssigned,
                                       double [][] _RearBoundaryByLane){
            this.IdxQueuedVeh=_IdxQueuedVeh;
            this.IdxMovingVeh=_IdxMovingVeh;
            this.VehicleAssigned=_VehicleAssigned;
            this.RearBoundaryByLane=_RearBoundaryByLane;
        }
        protected int IdxQueuedVeh; // Index of queue vehicles
        protected int IdxMovingVeh; // Index of moving vehicles
        protected List<SimVehicle> VehicleAssigned; // List of vehicles assigned to a particular link and lane
        protected double [][] RearBoundaryByLane; // Rear boundary by lane at a given section
    }

    //***************************************************************************************************************
    //***************************************************************************************************************
    //************************** Vehicle Generation  ****************************************************************
    //***************************************************************************************************************
    //***************************************************************************************************************

    //******************************************* Main Function *****************************************************
    public static List<SimVehicle> VehicleGeneration(List<AimsunApproach> aimsunNetworkByApproach, Map<Integer,EstimationResults> estimationResultsList
            ,List<AimsunControlPlanJunction> ActiveControlPlans, SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles for Aimsun

        List<SimVehicle> simVehicleList=new ArrayList<SimVehicle>();

        if(aimsunNetworkByApproach.size()>0){
            for(int i=0;i<aimsunNetworkByApproach.size();i++){
                if(aimsunNetworkByApproach.get(i).getSignalized()==1){ // if it is a signalized junction
                    int FirstSectionID=aimsunNetworkByApproach.get(i).getFirstSectionID();
                    if(estimationResultsList.containsKey(FirstSectionID)){
                        // If we have estimated queues(non-negative values: -1 means no information; -2 means no such a movement)
                        List<SimVehicle> tmpListSimVehicle=VehicleGenerationForSignalizedJunctionWithEstimation(aimsunNetworkByApproach.get(i),
                                estimationResultsList.get(FirstSectionID),ActiveControlPlans,simulationStatistics);
                        simVehicleList.addAll(tmpListSimVehicle);

                    }else{// When no queue estimates, use the simulated traffic states from Aimsun instead
                        List<SimVehicle> tmpListSimVehicle=VehicleGenerationForSignalizedJunctionWithSimulation(aimsunNetworkByApproach.get(i),simulationStatistics);
                        simVehicleList.addAll(tmpListSimVehicle);
                    }
                }
                else{// If it is not a signalized junction, e.g., freeway junction
                    // For unsignalized junction

                    // If BEATs simulation is not available, use the Aimsun simulation results
                    List<SimVehicle> tmpListSimVehicle=VehicleGenerationForUnsignalizedJunctionWithSimulation(aimsunNetworkByApproach.get(i),simulationStatistics);
                    simVehicleList.addAll(tmpListSimVehicle);
                }
            }
        }
        return simVehicleList;
    }


    //***************************************************************************************************************
    //*************** Vehicle Generation At Signalized Junction *****************************************************
    //***************************************************************************************************************

    //*************** Vehicle Generation At Signalized Junction With Estimation *************************************
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
            List<SimVehicle> tmpSimVehicleAll=null;
            for(int i=0;i<5;i++){
                // Empirical adjustment
                // This loop is used to adjust the gap for moving vehicles (Jam spacing, jam spacing*threshold]
                // There may be more estimated vehicles than the link can handle, which sometimes requires to
                // shorten the gaps between moving vehicles.

                // Initialization of the list of vehicles
                tmpSimVehicleAll=new ArrayList<SimVehicle>();
                int IdxQueuedVeh=0;
                int IdxMovingVeh=0;

                double Threshold=3-i*0.5;
                for(int j=0;j<ListOfSections.size();j++){// Note: Sections are ordered from downstream to upstream
                    VehicleAssignmentTmpStr vehicleAssignmentTmpStr=AssignVehicleToOneSection(EstimatedStates,VehWithODAndLaneQueued,VehWithODAndLaneMoving
                            ,IdxQueuedVeh,IdxMovingVeh,aimsunApproach,simulationStatistics,j,Threshold,parameters);
                    tmpSimVehicleAll.addAll(vehicleAssignmentTmpStr.VehicleAssigned);

                    if(VehWithODAndLaneQueued.size()<=vehicleAssignmentTmpStr.IdxQueuedVeh+1 &&
                            VehWithODAndLaneMoving.size()<=vehicleAssignmentTmpStr.IdxMovingVeh+1){
                        // If the assignment is done!
                        AssignmentDone=true;
                        break;
                    }else{ //If not, update the addresses
                        IdxMovingVeh=vehicleAssignmentTmpStr.IdxMovingVeh;
                        IdxQueuedVeh=vehicleAssignmentTmpStr.IdxQueuedVeh;

                        if(j>0){// Not the first section
                            // Reassign link ID and lane ID
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
            simVehicleList.addAll(tmpSimVehicleAll);
        }
        return simVehicleList;
    }

    public static VehicleAssignmentTmpStr AssignVehicleToOneSection(String[] Status,List<SimVehicle> VehWithODAndLaneQueued,List<SimVehicle>
            VehWithODAndLaneMoving,int IdxQueuedVeh,int IdxMovingVeh,AimsunApproach aimsunApproach,SimulationStatistics simulationStatistics
            , int SectionIdx,double Threshold,Parameters parameters){
        // This function is used to assign vehicles to one section

        // Get the indicator of RearBoundaryByLane for the give section
        int SectionID=aimsunApproach.getSectionBelongToApproach().getListOfSections().get(SectionIdx);
        int NumOfLanes=aimsunApproach.getSectionBelongToApproach().getProperty().get(SectionIdx).getNumLanes();
        double[] LaneLengths=aimsunApproach.getSectionBelongToApproach().getProperty().get(SectionIdx).getLaneLengths();
        double [][] RearBoundaryByLane=new double[NumOfLanes][3];
        for(int i=0;i<NumOfLanes;i++){
            RearBoundaryByLane[i][0]=i+1;// Lane ID from left to Right
            RearBoundaryByLane[i][1]=LaneLengths[i]; // Max rear boundary, i.e., max lane length
            RearBoundaryByLane[i][2]=0; // Current rear boundary=0
        }
        // Initialization
        List<SimVehicle> tmpSimVehicle=new ArrayList<SimVehicle>(); // Empty set
        VehicleAssignmentTmpStr vehicleAssignmentTmpStr=new  VehicleAssignmentTmpStr(IdxQueuedVeh,IdxMovingVeh,tmpSimVehicle,RearBoundaryByLane);

        // Assign positions and speeds for queued vehicles
        vehicleAssignmentTmpStr=AssignVehicleToOneSectionForGivenVehicleCategory(Status,vehicleAssignmentTmpStr
                , VehWithODAndLaneQueued,"Queued", parameters,simulationStatistics,Threshold, aimsunApproach,SectionID);

        // Assign positions and speeds for moving vehicles
        vehicleAssignmentTmpStr=AssignVehicleToOneSectionForGivenVehicleCategory(Status,vehicleAssignmentTmpStr
                , VehWithODAndLaneMoving,"Moving", parameters,simulationStatistics,Threshold, aimsunApproach,SectionID);

        return vehicleAssignmentTmpStr;
    }

    public static VehicleAssignmentTmpStr AssignVehicleToOneSectionForGivenVehicleCategory(String[] Status,VehicleAssignmentTmpStr vehicleAssignmentTmpStr
            ,List<SimVehicle> VehWithODAndLane,String Catetory,Parameters parameters,SimulationStatistics simulationStatistics,double Threshold
            , AimsunApproach aimsunApproach,int SectionID){
        // This function is used to assign vehicle to one section for the given vehicle categority: queued or moving

        // Initialization
        int Idx=-1;
        if(Catetory.equals("Queued")){
            Idx=vehicleAssignmentTmpStr.IdxQueuedVeh;
        }else if(Catetory.equals("Moving")){
            Idx=vehicleAssignmentTmpStr.IdxMovingVeh;
        }else{
            System.out.println("Error in vehicle catetory: neither Queued nor Moving!");
            System.exit(-1);
        }
        double[][] RearBoundaryByLane=vehicleAssignmentTmpStr.RearBoundaryByLane;
        List<SimVehicle> tmpSimVehicle=vehicleAssignmentTmpStr.VehicleAssigned;

        // Assign vehicles for given category
        int NumOfLanes=RearBoundaryByLane.length;
        int VehicleType=0; // Vehicle Type=0 means the statistics for all vehicles, just want to make it simple; Can be overwritten in the future
        for(int i=0;i<NumOfLanes;i++){
            int LaneID=i+1;
            LaneStatistics laneStatistics=GetLaneInSectionStatistics(simulationStatistics,SectionID, LaneID,VehicleType);
        }


        // Update information and return
        if(Catetory.equals("Queued")){
            vehicleAssignmentTmpStr.IdxQueuedVeh=Idx;
        }else if(Catetory.equals("Moving")){
            vehicleAssignmentTmpStr.IdxMovingVeh=Idx;
        }
        vehicleAssignmentTmpStr.VehicleAssigned=tmpSimVehicle;
        vehicleAssignmentTmpStr.RearBoundaryByLane=RearBoundaryByLane;
        return vehicleAssignmentTmpStr;
    }


    public static LaneStatistics GetLaneInSectionStatistics(SimulationStatistics simulationStatistics, int SectionID, int LaneID, int VehicleType){
        // This function is used to get lane statistics for a given section and lane.
        LaneStatistics laneStatistics=null;

        //List<LaneStatistics> laneStatisticsList=simulationStatistics.getLaneStatisticsByObjectIDList()

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
                    boolean TrackOrNot=true;
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
