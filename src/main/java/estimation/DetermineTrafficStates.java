package estimation;

import commonClass.detectorData.SelectedDetectorData;
import commonClass.forEstimation.ThresholdAndRate;
import commonClass.forEstimation.TrafficStateByApproach;
import commonClass.forEstimation.TrafficStateByDetectorType;
import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.forGeneralNetwork.detector.DetectorMovementProperty;
import commonClass.parameters.Parameters;
import commonClass.query.QueryMeasures;
import dataProvider.getDetectorData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class determineTrafficStates {


    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @param parameters Parameters class
     * @return TrafficStateByApproach class
     */
    public static TrafficStateByApproach StateIdentification(Connection con, AimsunApproach aimsunApproach, QueryMeasures queryMeasures, Parameters parameters){
        // This function is used to make decision for traffic states at different types of detectors (By "Detector", not By "Movement")

        // Get the end time of the query measures
        int Time=queryMeasures.getTimeOfDay()[1];
        // Get traffic states
        List<TrafficStateByDetectorType> ExclusiveLeftTurnDetectors=GetTrafficState(con,
                aimsunApproach.getDetectorProperty().getExclusiveLeftTurn(), queryMeasures,parameters, "Exclusive Left Turn");
        List<TrafficStateByDetectorType> ExclusiveRightTurnDetectors=GetTrafficState(con,
                aimsunApproach.getDetectorProperty().getExclusiveRightTurn(), queryMeasures,parameters, "Exclusive Right Turn");
        List<TrafficStateByDetectorType> GeneralStopbarDetectors=GetTrafficState(con,
                aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors(), queryMeasures,parameters, "General Stopbar Detectors");
        List<TrafficStateByDetectorType> AdvanceDetectors=GetTrafficState(con,
                aimsunApproach.getDetectorProperty().getAdvanceDetectors(), queryMeasures,parameters, "Advance Detectors");

        TrafficStateByApproach trafficState=new TrafficStateByApproach(Time,AdvanceDetectors,ExclusiveLeftTurnDetectors,
                ExclusiveRightTurnDetectors,GeneralStopbarDetectors,null,null,null);
        return trafficState;
    }


    /**
     *
     * @param con Database connection
     * @param Detectors  List<DetectorMovementProperty> class
     * @param queryMeasures  QueryMeasures class
     * @param parameters Parameters class
     * @param DetectorGroup String
     * @return List<TrafficStateByDetectorType> class
     */
    public static List<TrafficStateByDetectorType> GetTrafficState(Connection con,List<DetectorMovementProperty>
            Detectors, QueryMeasures queryMeasures,Parameters parameters, String DetectorGroup){
        // This function is used to get traffic states for detector group: exclusive left/right turns, general stopbar detectors,
        // and advance detectors

        List<TrafficStateByDetectorType> trafficStateByDetectorGroups=new ArrayList<TrafficStateByDetectorType>();
        if(Detectors.size()>0){
            for (int i=0;i<Detectors.size();i++){
                // Even for the same Detector Group, there exist different types of detectors, e.g.
                // At the stopbar, we can have detectors that detect left&right, left&through, through&right, & all movements
                TrafficStateByDetectorType tmpDetectors=CheckDetectorGroupStatus(con,
                        Detectors.get(i),DetectorGroup,parameters,queryMeasures);
                trafficStateByDetectorGroups.add(tmpDetectors);
            }
        }
        return trafficStateByDetectorGroups;
    }


    /**
     *
     * @param con Database connection
     * @param detectorMovementProperty DetectorMovementProperty class
     * @param DetectorGroup String
     * @param parameters Parameters class
     * @param queryMeasures QueryMeasures class
     * @return TrafficStateByDetectorType class
     */
    public static TrafficStateByDetectorType CheckDetectorGroupStatus(Connection con, DetectorMovementProperty detectorMovementProperty, String
            DetectorGroup, Parameters parameters, QueryMeasures queryMeasures){
        // This function is used to check the detector group status

        // Get the detector type
        String DetectorType=detectorMovementProperty.getMovement();

        // Get the detector data
        List<Double> Occupancies=new ArrayList<Double>();
        List<Double> Flows=new ArrayList<Double>();
        double AvgFlow=0;
        double AvgOcc=0;
        double AvgLength=0;
        double TotalLanes=0;
        double OccUnit;

        try{
            if(con.getCatalog().equals("arcadia_tcs_server_data"))
                // Arcadia TCS database: occupancy is defined as seconds per hour
                OccUnit=3600.0/100.0; // in %
            else
                OccUnit=1.0; // in %

            Statement ps= con.createStatement();
            for (int i=0;i<detectorMovementProperty.getDetectorIDs().size();i++){ // Loop for each detector
                // Get the data
                SelectedDetectorData selectedDetectorData=getDetectorData.getDataForGivenDetector(ps,
                        detectorMovementProperty.getDetectorIDs().get(i), queryMeasures);
                if(selectedDetectorData.getDataAll()!=null){
                    if(selectedDetectorData.getHealth().equals("Good")){
                        // Only use good data
                        // Get the median flow and occupancy
                        Flows.add(selectedDetectorData.getFlowOccMid()[0]);
                        Occupancies.add(selectedDetectorData.getFlowOccMid()[1]/OccUnit);
                        // Do the calculation
                        TotalLanes=TotalLanes+detectorMovementProperty.getNumberOfLanes().get(i);
                        AvgFlow=AvgFlow+selectedDetectorData.getFlowOccMid()[0]*detectorMovementProperty.getNumberOfLanes().get(i);
                        AvgOcc=AvgOcc+selectedDetectorData.getFlowOccMid()[1]*detectorMovementProperty.getNumberOfLanes().get(i)/OccUnit;
                        AvgLength=AvgLength+detectorMovementProperty.getDetectorLengths().get(i)*detectorMovementProperty.getNumberOfLanes().get(i);
                    }
                }
            }// End of "for"
            if(TotalLanes>0) { // If have good detectors
                AvgFlow = AvgFlow / TotalLanes;
                AvgOcc = AvgOcc / TotalLanes;
                AvgLength = AvgLength / TotalLanes;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        // Get the traffic state assessment: Rate, [OccThresholdLow,OccThresholdHigh]
        ThresholdAndRate thresholdAndRate=new ThresholdAndRate("Unknown",new double[]{100,100});
        if(Flows.size()>0) {
            if(DetectorGroup.equals("Exclusive Left Turn")|| DetectorGroup.equals("Exclusive Right Turn") ||
                    DetectorGroup.equals("General Stopbar Detectors")){
                thresholdAndRate =getThresholdAndRate.AtStopbarDetector(AvgOcc, AvgLength, parameters, DetectorType);
            }else if(DetectorGroup.equals("Advance Detectors")) {
                thresholdAndRate=getThresholdAndRate.AtAdvanceDetector(AvgOcc,AvgLength, parameters, DetectorType);
            }else{
                System.out.println("Wrong Detector Group!");
                System.exit(-1);
            }
        }

        // Return the traffic states
        TrafficStateByDetectorType trafficStateByDetectorGroup=new TrafficStateByDetectorType(DetectorType,thresholdAndRate.getRate(),
                thresholdAndRate.getThresholds(),AvgOcc, AvgFlow,TotalLanes,Occupancies,Flows);
        return trafficStateByDetectorGroup;
    }



}
