package initialization;

import dataProvider.getSignalData;
import dataProvider.getSignalData.*;
import dataProvider.getSimulationData;
import dataProvider.getSimulationData.*;
import estimation.trafficStateEstimation;
import estimation.trafficStateEstimation.*;
import commonClass.forInitialization.*;
import networkInput.readFromAimsun;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork;
import networkInput.reconstructNetwork.*;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.util.*;

public class getFunctions {


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
     * @param FirstSectionID First section ID
     * @param SelectLanes Selected Lane IDs
     * @param centroidStatisticsList List<CentroidStatistics> class
     * @return List<int[]> ODForFirstSection
     */
    public static List<int[]> GetODsForGivenLanes(int FirstSectionID, List<Integer> SelectLanes,List<CentroidStatistics> centroidStatisticsList){
        // This function is used to get OD information for given lanes associated with a movement

        // If OD information in the downstream proportion (<200ft?) is available for the selected lanes, use it
        // If not, use the information either of the whole downstream section or of the whole approach

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
     * @param simVehicleList List<SimVehicle> class
     * @return Map<Integer,List<Integer>> LaneCentroidMap
     */
    public static Map<Integer,List<Integer>> GetCentroidLaneInformationFromSimVehicle(List<SimVehicle> simVehicleList){
        // This function is used to get the lane-centroid information from sim-vehicles

        // Get the list of centroids (destination)
        List<Integer> ListOfCentroids=GetListOfDestinationsFromSimVehicles(simVehicleList);

        Map<Integer,List<Integer>> LaneCentroidMap=new HashMap<Integer, List<Integer>>();
        for(int i=0;i<ListOfCentroids.size();i++){ // Loop for each destination centroid
            int CentroidID=ListOfCentroids.get(i);
            List<Integer> AvailableLanes=new ArrayList<Integer>();
            HashSet<Integer> uniqueLanes=new HashSet<Integer>();
            for(int j=0;j<simVehicleList.size();j++){ // Get the associated lanes
                if(simVehicleList.get(j).getDestinationCentroid()==CentroidID && uniqueLanes.add(simVehicleList.get(j).getLaneID())){
                    AvailableLanes.add(simVehicleList.get(j).getLaneID());
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
        // This function is used to get the list of destinations from the given Sim-Vehicles

        List<Integer> destinationList=new ArrayList<Integer>();
        HashSet<Integer> uniqueDestination=new HashSet<Integer>();
        for(int i=0;i<simVehicleList.size();i++){
            if(uniqueDestination.add(simVehicleList.get(i).getDestinationCentroid())){
                destinationList.add(simVehicleList.get(i).getDestinationCentroid());
            }
        }
        return destinationList;
    }


    /**
     *
     * @param LaneCentroidMap Map<Integer,List<Integer>>
     * @param RearBoundaryByLane double[][]
     * @param CentroidID Destination centroid ID
     * @param parameters Parameters class
     * @param Threshold Threshold of the maximum distance for vehicle assignment
     * @return List<Integer> AvailableLanes (that can accommodate new vehicles)
     */
    public static List<Integer> GetAvailableLanesForGivenCentroid(Map<Integer,List<Integer>> LaneCentroidMap, double[][] RearBoundaryByLane
            ,int CentroidID,Parameters parameters, double Threshold){
        // This function is used to get available lanes for a given centroid to assign at least one new vehicle

        List<Integer> AvailableLanes=new ArrayList<Integer>();

        List<Integer> Lanes=LaneCentroidMap.get(CentroidID); // Get the lane information for the given centroid
        for(int i=0;i<Lanes.size();i++){ // Loop for each lane
            if(RearBoundaryByLane[Lanes.get(i)-1][1]>RearBoundaryByLane[Lanes.get(i)-1][2]+Threshold*parameters.getVehicleParams().getJamSpacing()){
                // Still can assign a least one vehicle?
                AvailableLanes.add(Lanes.get(i));
            }
        }
        return AvailableLanes;
    }

    /**
     *
     * @param centroidStatistics CentroidStatistics class
     * @return  Map<Integer,List<Integer>> LaneCentroidMap
     */
    public static Map<Integer,List<Integer>> GetCentroidLaneInformationFromCentroidStatistics(CentroidStatistics centroidStatistics){
        // This function is used to get the lane-centroid information from centroid statistics

        // Get the list of unique centroids
        List<Integer> ListOfCentroids=new ArrayList<Integer>();
        HashSet<Integer> UniqueCentroids=new HashSet<Integer>();
        for(int i=0;i<centroidStatistics.getODList().size();i++){
            if(UniqueCentroids.add(centroidStatistics.getODList().get(i)[1])){
                ListOfCentroids.add(centroidStatistics.getODList().get(i)[1]);
            }
        }

        // Get the centroid-lane mapping
        Map<Integer,List<Integer>> LaneCentroidMap=new HashMap<Integer, List<Integer>>();
        for(int i=0;i<ListOfCentroids.size();i++){ // Loop for each destination centtoid

            int CentroidID=ListOfCentroids.get(i);
            List<Integer> AvailableLanes=new ArrayList<Integer>();
            HashSet<Integer> uniqueLanes=new HashSet<Integer>();
            List<ODByLane> ODByLaneList=centroidStatistics.getODListByLane();
            for(int j=0;j<ODByLaneList.size();j++){ // For each lane
                int LaneID=ODByLaneList.get(j).getLaneID(); // Get the lane IC
                for(int k=0;k<ODByLaneList.get(j).getODList().size();k++){ // Loop for each OD pair [Origin, Destination, VehicleType]
                    if(ODByLaneList.get(j).getODList().get(k)[1]==CentroidID){// The same centroid?
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
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @return DistanceToStopbar
     */
    public static double GetDistanceToStopbar(AimsunApproach aimsunApproach, Parameters parameters){
        // This function is used to get distance to the stopbar
        // This is taken as: min(first section length, turn pocket length, advance detector location)

        // Get the distance to stopbar from advance detectors
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
            DistanceToStopbar=parameters.getIntersectionParams().getDistanceAdvanceDetector();
        }

        // Adjust distance to stopbar according to the section length
        double[] LaneLengths=aimsunApproach.getSectionBelongToApproach().getProperty().get(0).getLaneLengths().clone();
        Arrays.sort(LaneLengths);
        double SectionLength=LaneLengths[LaneLengths.length-1]; // Get the last one (maximum)
        DistanceToStopbar=Math.min(DistanceToStopbar,SectionLength);

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
     * @param laneStatistics LaneStatistics class
     * @param Category Queued or Moving
     * @param parameters Parameter class
     * @param Threshold Spacing threshold
     * @return double [Spacing, Speed]
     */
    public static double [] GetSpacingAndSpeed(LaneStatistics laneStatistics, String Category,Parameters parameters, double Threshold
            ,int TypeOfCurrentVehicle){
        // This function is used to get spacing and speed for given category

        double Spacing=0;
        double pce;// Passenger car equivalent
        if(TypeOfCurrentVehicle==1){ // Regular car
            pce=1;
        }else if(TypeOfCurrentVehicle>1&&TypeOfCurrentVehicle<4) { // Regular trucks?
            pce=1.5;
        }else{ // Heavy duty trucks?
            pce=2;
        }
        double JamSpacing=parameters.getVehicleParams().getJamSpacing()*pce;// Get the default value

        double Speed=0;
        if(Category.equals("Queued")){// For queued vehicles
            Speed=0;
            Spacing=JamSpacing;
        }else if(Category.equals("Moving")){ // For moving vehicles
            // Spacing is uniformly selected between JamSpacing and JamSpacing*Threshold
            RealDistribution rnd=new UniformRealDistribution(JamSpacing,JamSpacing*Threshold);
            Spacing=rnd.sample();

            if(laneStatistics.getLaneSpeed()>=0 && laneStatistics.getLaneSpeedStd()>=0) { // If lane speed and speed std are available
                NormalDistribution rnd2 = new NormalDistribution(laneStatistics.getLaneSpeed(), laneStatistics.getLaneSpeedStd());
                Speed=Math.max(rnd2.sample(),5); // Minimum of 5mph
            }else{ // If not availalbe
                Random random = new Random();
                Speed=Math.max(parameters.getIntersectionParams().getSaturationSpeedThrough()*random.nextFloat(),5); // Ramdomly select between 0 and Saturation Speed For Through Vehicles
            }
        }else{
            System.out.println("Unknown vehicle category: neither Queued nor Moving!");
            System.exit(-1);
        }
        return new double[]{Spacing, Speed};
    }

    /**
     *
     * @param RearBoundaryByLane double[][]
     * @param parameters Parameters class
     * @param aimsunSection AimsunSection class
     * @param Threshold Threshold of the maximum distance for vehicle assignment
     * @return List<Integer> AvailableFullLanes
     */
    public static List<Integer> GetAvailableFullLanes(double[][] RearBoundaryByLane,Parameters parameters, AimsunSection aimsunSection
            , double Threshold){
        // This function is used to get available full lanes to assign vehicles to
        // The candidate must: (i)have enough space, and (ii) is a full lane
        // We do not consider candidates that are not full lanes.

        List<Integer> AvailableFullLanes=new ArrayList<Integer>();

        int[] IsFullLane=aimsunSection.getIsFullLane(); // Get the full-lane indicator
        for(int i=0;i<IsFullLane.length;i++){// Loop for each lane
            if(RearBoundaryByLane[i][1]>=RearBoundaryByLane[i][2]+Threshold*parameters.getVehicleParams().getJamSpacing()){ // Still have space
                if(IsFullLane[i]==1){// Is a full lane
                    AvailableFullLanes.add(i+1); // Append the lane ID to the end
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
    public static List<Integer> GetAvailableAdjacentLanesForGivenLane(double[][] RearBoundaryByLane,Parameters parameters, int LaneID,
                                                                      double Threshold){
        // This function is to find available adjacent lanes for a given lane

        List<Integer> AvailableAdjacentLanes=new ArrayList<Integer>();

        int StartLane=Math.max(LaneID-1,0); // Adjacent lane on the left
        int Numlanes=RearBoundaryByLane.length;
        int EndLane=Math.min(Numlanes-1,LaneID); // Adjacent lane on the right

        for(int i=StartLane;i<=EndLane;i++){
            if(RearBoundaryByLane[i][1]>=RearBoundaryByLane[i][2]+Threshold*parameters.getVehicleParams().getJamSpacing()){
                // Still able to add vehicles
                AvailableAdjacentLanes.add(i+1); // Add the lane ID
            }
        }
        return AvailableAdjacentLanes;
    }

    /**
     *
     * @param simulationStatistics SimulationStatistics class
     * @param SectionID Section ID
     * @param LaneID Lane ID
     * @param VehicleType Vehicle Type: Vehicle Type=0 means the statistics for all vehicles, just want to make it simple; Can be overwritten in the future
     * @return LaneStatistics class
     */
    public static LaneStatistics GetLaneInSectionStatistics(SimulationStatistics simulationStatistics, int SectionID, int LaneID, int VehicleType){
        // This function is used to get lane statistics for a given section and lane.

        LaneStatistics laneStatistics=null;
        List<LaneStatistics> laneStatisticsList;

        // Retrieve and return the latest lane statistics
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

        // Loop for the right lane statistics from "laneStatisticsList"
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

        // Return results: with max green time if multiple signals associated with a movement
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
     * @param simVehicleList List<SimVehicle> class
     * @param LaneID Lane ID
     * @return List<SimVehicle>
     */
    public static List<SimVehicle> GetVehicleByLane(List<SimVehicle> simVehicleList, int LaneID){
        // This function is used to select vehicles by lane ID
        List<SimVehicle> tmpSimVehicle=new ArrayList<SimVehicle>();

        for(int i=0;i<simVehicleList.size();i++){
            if(simVehicleList.get(i).getLaneID()==LaneID){
                tmpSimVehicle.add(simVehicleList.get(i));
            }
        }
        return tmpSimVehicle;
    }


}
