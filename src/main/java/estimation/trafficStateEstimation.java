package estimation;

import main.MainFunction;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;
import dataProvider.*;
import dataProvider.getSignalData;
import dataProvider.getDetectorData.*;
import sun.misc.Signal;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Qijian-Gan on 11/29/2017.
 */
public class trafficStateEstimation {

    // ********************************************************************
    // ***********************Settings*************************************
    // ********************************************************************

    // ***********************Parameters*******************************
    public static class Parameters{
        // Parameter settings
        public Parameters(VehicleParams _vehicleParams, IntersectionParams _intersectionParams,SignalSettings _signalSettings,
                          TurningPrortions _turningPrortions,EstimationParams _estimationParams){
            this.vehicleParams=_vehicleParams;
            this.intersectionParams=_intersectionParams;
            this.signalSettings=_signalSettings;
            this.turningPrortions=_turningPrortions;
            this.estimationParams=_estimationParams;
        }
        protected VehicleParams vehicleParams; // Vehicle parameters
        protected IntersectionParams intersectionParams; // Intersection parameters
        protected SignalSettings signalSettings; // Signal Settings
        protected TurningPrortions turningPrortions; // Turning proportions
        protected EstimationParams estimationParams; // Estimation parameters

        public SignalSettings getSignalSettings() {
            return signalSettings;
        }
    }

    public static class VehicleParams{
        // Vehicle parameters
        public VehicleParams(double _VehicleLength,double _StartupLostTime,double _JamSpacing){
            this.VehicleLength=_VehicleLength;
            this.StartupLostTime=_StartupLostTime;
            this.JamSpacing=_JamSpacing;
        }
        protected double VehicleLength;
        protected double StartupLostTime;
        protected double JamSpacing;
    }

    public static class IntersectionParams{
        // Intersection parameters
        public IntersectionParams(double _SaturationHeadway,double _SaturationSpeedLeft,
                                  double _SaturationSpeedRight,double _SaturationSpeedThrough,
                                  double _DistanceAdvandedDetector,double _LeftTurnPocket,
                                  double _RightTurnPocket,double _DistanceToEnd){
            this.SaturationHeadway=_SaturationHeadway;
            this.SaturationSpeedLeft=_SaturationSpeedLeft;
            this.SaturationSpeedThrough=_SaturationSpeedThrough;
            this.SaturationSpeedRight=_SaturationSpeedRight;
            this.DistanceAdvanceDetector=_DistanceAdvandedDetector;
            this.LeftTurnPocket=_LeftTurnPocket;
            this.RightTurnPocket=_RightTurnPocket;
            this.DistanceToEnd=_DistanceToEnd;
        }
        protected double SaturationHeadway;
        protected double SaturationSpeedLeft;
        protected double SaturationSpeedRight;
        protected double SaturationSpeedThrough;
        protected double DistanceAdvanceDetector;
        protected double LeftTurnPocket;
        protected double RightTurnPocket;
        protected double DistanceToEnd;
    }

    public static class EstimationParams{
        // Estimation parameters
        public EstimationParams(double _FFSpeedForAdvDet,double _OccThresholdForAdvDet){
            this.FFSpeedForAdvDet=_FFSpeedForAdvDet;
            this.OccThresholdForAdvDet=_OccThresholdForAdvDet;
        }
        protected double FFSpeedForAdvDet;
        protected double OccThresholdForAdvDet;
    }

    public static class SignalSettings{
        // Signal settings
        public SignalSettings(int _CycleLength,int _LeftTurnGreen,int _ThroughGreen,int _RightTurnGreen,String _LeftTurnSetting){
            this.CycleLength=_CycleLength;
            this.LeftTurnGreen=_LeftTurnGreen;
            this.ThroughGreen=_ThroughGreen;
            this.RightTurnGreen=_RightTurnGreen;
            this.LeftTurnSetting=_LeftTurnSetting;
        }
        protected int CycleLength;
        protected int LeftTurnGreen;
        protected int ThroughGreen;
        protected int RightTurnGreen;
        protected String LeftTurnSetting;

        public int getCycleLength() {
            return CycleLength;
        }

        public int getLeftTurnGreen() {
            return LeftTurnGreen;
        }

        public int getThroughGreen() {
            return ThroughGreen;
        }

        public int getRightTurnGreen() {
            return RightTurnGreen;
        }

        public String getLeftTurnSetting() {
            return LeftTurnSetting;
        }
    }

    public static class TurningPrortions{
        //Turning proportions
        public TurningPrortions(double [] _LeftTurn,double [] _LeftTurnQueue,double [] _AdvanceLeftTurn,
                                double [] _RightTurn,double [] _RightTurnQueue,double [] _AdvanceRightTurn,
                                double [] _Advance,double [] _AllMovements,double [] _AdvanceThrough,
                                double [] _Through,double [] _AdvanceLeftAndThrough,double [] _LeftAndThrough,
                                double [] _AdvanceLeftAndRight,double [] _LeftAndRight,double [] _AdvanceThroughAndRight,
                                double [] _ThroughAndRight){
            this.LeftTurn=_LeftTurn;
            this.LeftTurnQueue=_LeftTurnQueue;
            this.AdvanceLeftTurn=_AdvanceLeftTurn;
            this.RightTurn=_RightTurn;
            this.RightTurnQueue=_RightTurnQueue;
            this.AdvanceRightTurn=_AdvanceRightTurn;
            this.Advance=_Advance;
            this.AllMovements=_AllMovements;
            this.AdvanceThrough=_AdvanceThrough;
            this.Through=_Through;
            this.AdvanceLeftAndThrough=_AdvanceLeftAndThrough;
            this.LeftAndThrough=_LeftAndThrough;
            this.AdvanceLeftAndRight=_AdvanceLeftAndRight;
            this.LeftAndRight=_LeftAndRight;
            this.AdvanceThroughAndRight=_AdvanceThroughAndRight;
            this.ThroughAndRight=_ThroughAndRight;
        }
        protected double [] LeftTurn; // Exclusive left turn
        protected double [] LeftTurnQueue;
        protected double [] AdvanceLeftTurn;
        protected double [] RightTurn; // Exclusive right turn
        protected double [] RightTurnQueue;
        protected double [] AdvanceRightTurn;
        protected double [] Advance;   // Mixed through, left turn, and right turn
        protected double [] AllMovements;
        protected double [] AdvanceThrough; // Exclusive through
        protected double [] Through;
        protected double [] AdvanceLeftAndThrough; // Left turn and through only
        protected double [] LeftAndThrough;
        protected double [] AdvanceLeftAndRight;  // Left turn and right turn only
        protected double [] LeftAndRight;
        protected double [] AdvanceThroughAndRight; // Through and right turn only
        protected double [] ThroughAndRight;
    }

    // ***********************Query Measures*******************************
    public static class QueryMeasures{
        // Settings of data query
        public QueryMeasures(int _Year,int _Month,int _Day,int _DayOfWeek,boolean _Median,int [] _TimeOfDay,int _Interval){
            this.Year=_Year;
            this.Month=_Month;
            this.Day=_Day;
            this.DayOfWeek=_DayOfWeek;
            this.Median=_Median;
            this.TimeOfDay=_TimeOfDay;
            this.Interval=_Interval;
        }
        protected int Year;
        protected int Month;
        protected int Day;
        protected int DayOfWeek;
        protected boolean Median; // Whether to use median or not
        protected int [] TimeOfDay=null; // [From time, To time]
        protected int Interval; // Aggregation interval

        public int getYear() {
            return Year;
        }

        public int getMonth() {
            return Month;
        }

        public int getDay() {
            return Day;
        }

        public int getDayOfWeek() {
            return DayOfWeek;
        }

        public boolean isMedian() {
            return Median;
        }

        public int[] getTimeOfDay() {
            return TimeOfDay;
        }

        public int getInterval() {
            return Interval;
        }
    }

    // ***********************Traffic States*******************************
    public static class TrafficState{
        // Traffic states with complete information
        public TrafficState(AimsunApproach _aimsunApproach, Parameters _parameters,QueryMeasures _queryMeasures
                , TrafficStateByApproach _trafficStateByApproach){
            this.aimsunApproach=_aimsunApproach;
            this.parameters=_parameters;
            this.queryMeasures=_queryMeasures;
            this.trafficStateByApproach=_trafficStateByApproach;
        }
        protected AimsunApproach aimsunApproach;
        protected Parameters parameters;
        protected QueryMeasures queryMeasures;
        protected TrafficStateByApproach trafficStateByApproach;

        public TrafficStateByApproach getTrafficStateByApproach() {
            return trafficStateByApproach;
        }

        public AimsunApproach getAimsunApproach() {
            return aimsunApproach;
        }

        public QueryMeasures getQueryMeasures() {
            return queryMeasures;
        }
    }

    public static class TrafficStateByApproach{
        // Profile for traffic states for different groups of detectors: advance, exclusive left-/right-turn, general stopbar
        public TrafficStateByApproach(int _Time,List<TrafficStateByDetectorType> _AdvanceDetectors,List<TrafficStateByDetectorType> _ExclusiveLeftTurnDetectors
                , List<TrafficStateByDetectorType> _ExclusiveRightTurnDetectors,List<TrafficStateByDetectorType> _GeneralStopbarDetectors
                , QueueThreshold _queueThreshold,String[] _StateByMovement,int[] _QueueByMovement){
            this.Time=_Time;
            this.AdvanceDetectors=_AdvanceDetectors;
            this.ExclusiveLeftTurnDetectors=_ExclusiveLeftTurnDetectors;
            this.ExclusiveRightTurnDetectors=_ExclusiveRightTurnDetectors;
            this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
            this.queueThreshold=_queueThreshold;
            this.StateByMovement=_StateByMovement;
            this.QueueByMovement=_QueueByMovement;
        }
        protected int Time;
        // Traffic states at detectors
        protected List<TrafficStateByDetectorType> AdvanceDetectors; // At advance detectors
        protected List<TrafficStateByDetectorType> ExclusiveLeftTurnDetectors; // At exclusive left turn detectors
        protected List<TrafficStateByDetectorType> ExclusiveRightTurnDetectors; // At exclusive right turn detectors
        protected List<TrafficStateByDetectorType> GeneralStopbarDetectors; // At general stopbar detectors
        // Traffic states by movements
        protected QueueThreshold queueThreshold;
        protected String[] StateByMovement;
        protected int[] QueueByMovement;

        public QueueThreshold getQueueThreshold() {
            return queueThreshold;
        }

        public int getTime() {
            return Time;
        }

        public List<TrafficStateByDetectorType> getAdvanceDetectors() {
            return AdvanceDetectors;
        }

        public List<TrafficStateByDetectorType> getExclusiveLeftTurnDetectors() {
            return ExclusiveLeftTurnDetectors;
        }

        public List<TrafficStateByDetectorType> getExclusiveRightTurnDetectors() {
            return ExclusiveRightTurnDetectors;
        }

        public List<TrafficStateByDetectorType> getGeneralStopbarDetectors() {
            return GeneralStopbarDetectors;
        }

        public String[] getStateByMovement() {
            return StateByMovement;
        }

        public int[] getQueueByMovement() {
            return QueueByMovement;
        }
    }

    public static class TrafficStateByDetectorType{
        // Property of traffic states by detector type
        public TrafficStateByDetectorType(String _DetectorType,String _Rate, double[] _Thresholds,double _AvgOcc,
                                           double _AvgFlow, double _TotLanes,List<Double> _Occupancies,List<Double> _Flows){
            this.DetectorType=_DetectorType;
            this.Rate=_Rate;
            this.Thresholds=_Thresholds;
            this.AvgOcc=_AvgOcc;
            this.AvgFlow=_AvgFlow;
            this.TotLanes=_TotLanes;
            this.Occupancies=_Occupancies;
            this.Flows=_Flows;
        }
        protected String DetectorType; // Detector type
        protected String Rate; // This is a string
        protected double [] Thresholds;
        protected double AvgOcc; // Average occ and flow
        protected double AvgFlow;
        protected double TotLanes; // Total number of lanes
        protected List<Double> Occupancies; // List of occupancies
        protected List<Double> Flows; // List of flows

        public String getDetectorType() {
            return DetectorType;
        }

        public String getRate() {
            return Rate;
        }

        public double getAvgFlow() {
            return AvgFlow;
        }

        public double getAvgOcc() {
            return AvgOcc;
        }

        public double getTotLanes() {
            return TotLanes;
        }
    }

    public static class ThresholdAndRate{
        // Thresholds and Rates
        public ThresholdAndRate(String _Rate,double [] _Thresholds){
            this.Rate=_Rate;
            this.Thresholds=_Thresholds;
        }
        protected String Rate; // This is a string
        protected double [] Thresholds;
    }

    // ***********************Detector Types*******************************
    public static class DetectorTypeByMovement{
        // Category/types of detectors by each movement: left-turn, through, and right-turn
        public DetectorTypeByMovement(String [] _Left,String [] _Through,String [] _Right){
            this.Left=_Left;
            this.Through=_Through;
            this.Right=_Right;
        }
        protected String [] Left;
        protected String [] Through;
        protected String [] Right;
    }

    // ***********************Aggregated traffic flow values*******************************
    public static class AggregatedTrafficStates{
        // Profile for aggregated traffic states
        public AggregatedTrafficStates(double [] _AggregatedStatus,double [] _AvgOccupancy,double [] _AggregatedTotLanes,
                                       double [] _ThresholdLow, double [] _ThresholdHigh){
            this.AggregatedStatus=_AggregatedStatus;
            this.AvgOccupancy=_AvgOccupancy;
            this.AggregatedTotLanes=_AggregatedTotLanes;
            this.ThresholdLow=_ThresholdLow;
            this.ThresholdHigh=_ThresholdHigh;
        }
        protected double [] AggregatedStatus; // Status: rate
        protected double [] AvgOccupancy; // Avg Occupancy
        protected double [] AggregatedTotLanes; // Total lanes
        protected double [] ThresholdLow; // Aggregated low thresholds [left, through, right]
        protected double [] ThresholdHigh;// Aggregated high thresholds [left, through, right]
    }

    // ***********************Queue thresholds*******************************
    public static class QueueThresholdByMovement{
        // Queue thresholds for each movement
        public QueueThresholdByMovement(double _QueueToAdvance,double _QueueWithMaxGreen, double _QueueToEnd){
            this.QueueToAdvance=_QueueToAdvance;
            this.QueueWithMaxGreen=_QueueWithMaxGreen;
            this.QueueToEnd=_QueueToEnd;
        }
        protected double QueueToAdvance;
        protected double QueueWithMaxGreen;
        protected double QueueToEnd;

        public double getQueueToAdvance() {
            return QueueToAdvance;
        }

        public double getQueueToEnd() {
            return QueueToEnd;
        }

        public double getQueueWithMaxGreen() {
            return QueueWithMaxGreen;
        }
    }

    public static class QueueThreshold{
        // Queue thresholds for three movements
        public QueueThreshold(QueueThresholdByMovement _QueueThresholdLeft, QueueThresholdByMovement _QueueThresholdThrough
                ,QueueThresholdByMovement _QueueThresholdRight){
            this.QueueThresholdLeft=_QueueThresholdLeft;
            this.QueueThresholdThrough=_QueueThresholdThrough;
            this.QueueThresholdRight=_QueueThresholdRight;
        }
        protected QueueThresholdByMovement QueueThresholdLeft;
        protected QueueThresholdByMovement QueueThresholdThrough;
        protected QueueThresholdByMovement QueueThresholdRight;

        public QueueThresholdByMovement getQueueThresholdLeft() {
            return QueueThresholdLeft;
        }

        public QueueThresholdByMovement getQueueThresholdRight() {
            return QueueThresholdRight;
        }

        public QueueThresholdByMovement getQueueThresholdThrough() {
            return QueueThresholdThrough;
        }
    }

    public static class AssessmentStateAndQueue{
        // Assessment of state and queue
        public AssessmentStateAndQueue(String [] _StatusAssessment,int [] _QueueAssessment){
            this.StatusAssessment=_StatusAssessment;
            this.QueueAssessment=_QueueAssessment;
        }
        protected String[] StatusAssessment;
        protected int[] QueueAssessment;
    }

    public static class AssessmentStateAndQueueByMovement{
        // Assessment of state and queue by movement: left-turn, through, and right-turn
        public AssessmentStateAndQueueByMovement(String _StatusAssessment,int _QueueAssessment){
            this.StatusAssessment=_StatusAssessment;
            this.QueueAssessment=_QueueAssessment;
        }
        protected String StatusAssessment;
        protected int QueueAssessment;
    }

    // ********************************************************************
    // ***********************Main functions*******************************
    // ********************************************************************

    /**
     *
     * @param con Database connection
     * @param network AimsunNetworkByApproach class
     * @param queryMeasures QueryMeasures class
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @return List<TrafficState> class
     */
    public static List<TrafficState> arterialTrafficStateEstimation(Connection con,AimsunNetworkByApproach network
            ,QueryMeasures queryMeasures,List<AimsunControlPlanJunction> ActiveControlPlans){
        // This function is for arterial traffic state estimation

        // Run arterial estimation
        List<AimsunApproach> aimsunApproaches=network.getAimsunNetworkByApproach();
        List<TrafficState> trafficStateList=new ArrayList<TrafficState>();
        for(int i=0;i<aimsunApproaches.size();i++) {// Loop for each approach

            System.out.println("Junction ID="+aimsunApproaches.get(i).getJunctionID()+", Name="+
                    aimsunApproaches.get(i).getJunctionName()+", Section ID="+aimsunApproaches.get(i).getFirstSectionID());

            AimsunApproach aimsunApproach=aimsunApproaches.get(i);

            // Get default parameters
            Parameters parameters=getDefaultParameters();

            // *****************Update the parameter settings******************
            // Update turning proportions
            parameters.turningPrortions=UpdateVehicleProportions(parameters.turningPrortions,con,aimsunApproach, queryMeasures);
            parameters.turningPrortions=UpdateVehicleProportionsAccordingToLandIndicator(parameters.turningPrortions
                    ,aimsunApproach.getGeoDesign().getTurnIndicator());

            // Update saturation speeds
            parameters=UpdateSaturationSpeeds(parameters,aimsunApproach);

            // Update signal settings and the saturation speed (permitted phases)
            parameters=UpdateSignalSettings(parameters,ActiveControlPlans,aimsunApproach);

            // *****************Decision making******************************
            TrafficStateByApproach trafficStateByApproach=MakeDecisionTrafficState(con,aimsunApproach,queryMeasures,parameters);

            trafficStateByApproach=AssessmentStateAndQueue(aimsunApproach,parameters,trafficStateByApproach);

            TrafficState trafficState=new TrafficState(aimsunApproach,parameters,queryMeasures,trafficStateByApproach);
            trafficStateList.add(trafficState);
        }

        return trafficStateList;
    }

    // *****************************************************************************************************
    // ******************Assess the state and queue for different movements at a given approach*************
    // *****************************************************************************************************
    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters
     * @param trafficState TrafficStateByApproach class
     * @return TrafficStateByApproach class
     */
    public static TrafficStateByApproach AssessmentStateAndQueue(AimsunApproach aimsunApproach,Parameters parameters,TrafficStateByApproach trafficState){
        // This function is used to get the assessment of state and queue
        String [] StatusAssessment;
        int [] QueueAssessment;
        QueueThreshold queueThreshold;
        if(aimsunApproach.getDetectorProperty().getAdvanceDetectors().size()==0 && aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().size()==0
                && aimsunApproach.getDetectorProperty().getExclusiveRightTurn().size()==0
                && aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().size()==0){
            // If no detectors are available
            queueThreshold=null;
            StatusAssessment=new String[]{"No Detector","No Detector","No Detector"};
            QueueAssessment=new int[]{-1,-1,-1};// Set the queues to be negative
        }
        else{
            // For exclusive left turns
            DetectorTypeByMovement ExclusiveLeft=new DetectorTypeByMovement(new String [] {"Left Turn","Left Turn Queue"},
                    new String []{}, new String []{}); // Exclusive left turn
            AggregatedTrafficStates aggregatedTrafficStatesLeft=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.getDetectorProperty().getExclusiveLeftTurn(),ExclusiveLeft, trafficState.ExclusiveLeftTurnDetectors,
                    "Exclusive Left Turn",parameters);

            // For exclusive right turns
            DetectorTypeByMovement ExclusiveRight=new DetectorTypeByMovement(new String []{}, new String []{},
                    new String [] {"Right Turn","Right Turn Queue"}); // Exclusive right turn
            AggregatedTrafficStates aggregatedTrafficStatesRight=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.getDetectorProperty().getExclusiveRightTurn(),ExclusiveRight, trafficState.ExclusiveRightTurnDetectors,
                    "Exclusive Right Turn",parameters);

            // For general detectors
            DetectorTypeByMovement GeneralDetector=new DetectorTypeByMovement(
                    new String []{"All Movements","Left and Right","Left and Through"}, // Left turn
                    new String []{"All Movements","Through","Left and Through","Through and Right"}, //Through
                    new String []{"All Movements","Left and Right","Through and Right"}); // Right turn
            AggregatedTrafficStates aggregatedTrafficStatesGeneral=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors(),GeneralDetector, trafficState.GeneralStopbarDetectors,
                    "General Stopbar Detectors",parameters);

            // For Advance detectors
            DetectorTypeByMovement AdvanceDetector=new DetectorTypeByMovement(
                    new String []{"Advance","Advance Left Turn","Advance Left and Through", "Advance Left and Right"},// Left turn
                    new String []{"Advance","Advance Through","Advance Through and Right","Advance Left and Through"}, // Through
                    new String []{"Advance","Advance Right Turn","Advance Through and Right","Advance Left and Right"}); // Right turn
            AggregatedTrafficStates aggregatedTrafficStatesAdvance=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.getDetectorProperty().getAdvanceDetectors(),AdvanceDetector, trafficState.AdvanceDetectors,
                    "Advance Detectors",parameters);

            // Get the queue thresholds for different traffic movements
            queueThreshold=CalculateQueueThresholds(aimsunApproach,parameters);

            // Get the assessment of states and queues
            AssessmentStateAndQueue assessmentStateAndQueue=MakeADecision(aggregatedTrafficStatesAdvance,aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                    , aggregatedTrafficStatesRight,aimsunApproach.getGeoDesign().getTurnIndicator(),queueThreshold);
            StatusAssessment=assessmentStateAndQueue.StatusAssessment;
            QueueAssessment=assessmentStateAndQueue.QueueAssessment;
        }
        trafficState.queueThreshold=queueThreshold;
        trafficState.StateByMovement=StatusAssessment;
        trafficState.QueueByMovement=QueueAssessment;
        return trafficState;
    }

    /**
     *
     * @param detectorMovementProperties List<DetectorMovementProperty> class
     * @param detectorTypeByMovement DetectorTypeByMovement class
     * @param trafficStateByDetectorGroups List<TrafficStateByDetectorType> class
     * @param DetectorGroup String
     * @param parameters Parameters
     * @return AggregatedTrafficStates class
     */
    public static AggregatedTrafficStates CheckAggregatedStateForTrafficMovements(
            List<DetectorMovementProperty> detectorMovementProperties,DetectorTypeByMovement detectorTypeByMovement,
            List<TrafficStateByDetectorType> trafficStateByDetectorGroups,String DetectorGroup,Parameters parameters){
        // This is the function to check aggregated state for traffic movements

        // Initialization of aggregated states for three different movements:[Left turn, Through, Right turn]
        // Params: Rates[], Occs[], Lanes[], ThresholdLow[], ThresholdHigh[]
        AggregatedTrafficStates aggregatedTrafficStates=new AggregatedTrafficStates(new double[]{0,0,0},
                new double[]{0,0,0},new double[]{0,0,0},new double[]{0,0,0},new double[]{0,0,0});
        if(detectorMovementProperties.size()>0) {
            int NumType = detectorMovementProperties.size();
            // Check left-turn movement
            double [] RateOCCLanesLeft=CheckAggregatedStateIndividualMovement(NumType, detectorTypeByMovement.Left
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Left",DetectorGroup);
            // Check through movement
            double [] RateOCCLanesThrough=CheckAggregatedStateIndividualMovement(NumType, detectorTypeByMovement.Through
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Through",DetectorGroup);
            // Check right-turn movement
            double [] RateOCCLanesRight=CheckAggregatedStateIndividualMovement(NumType, detectorTypeByMovement.Right
                    ,detectorMovementProperties, trafficStateByDetectorGroups, parameters, "Right",DetectorGroup);

            double [] Rates=new double[]{RateOCCLanesLeft[0],RateOCCLanesThrough[0],RateOCCLanesRight[0]};
            double [] Occs=new double[]{RateOCCLanesLeft[1],RateOCCLanesThrough[1],RateOCCLanesRight[1]};
            double [] Lanes=new double[]{RateOCCLanesLeft[2],RateOCCLanesThrough[2],RateOCCLanesRight[2]};
            double [] ThresholdLow=new double[]{RateOCCLanesLeft[3],RateOCCLanesThrough[3],RateOCCLanesRight[3]};
            double [] ThresholdHigh=new double[]{RateOCCLanesLeft[4],RateOCCLanesThrough[4],RateOCCLanesRight[4]};
            aggregatedTrafficStates=new AggregatedTrafficStates(Rates,Occs,Lanes,ThresholdLow,ThresholdHigh);
        }
        return aggregatedTrafficStates;
    }

    /**
     *
     * @param NumType Number of detector types
     * @param PossibleMovement String []
     * @param detectorMovementProperties List<DetectorMovementProperty> class
     * @param trafficStateByDetectorGroups List<TrafficStateByDetectorType> class
     * @param parameters Parameters
     * @param Movement String
     * @param DetectorGroup String
     * @return double[]{Rate,Occ,TotLanes,ThresholdLow,ThresholdHigh}
     */
    public static double [] CheckAggregatedStateIndividualMovement(int NumType, String [] PossibleMovement
            ,List<DetectorMovementProperty> detectorMovementProperties, List<TrafficStateByDetectorType> trafficStateByDetectorGroups
            ,Parameters parameters, String Movement,String DetectorGroup){
        // This is the function to check aggregated state for individual traffic movement

        double Rate=0;
        double Occ=0;
        double TotLanes=0;
        double ThresholdLow=0;
        double ThresholdHigh=0;
        int NumMatchedDetectorMovement=0;
        for(int i=0;i<NumType;i++){ // Loop for each detector type
            for(int j=0;j<PossibleMovement.length;j++){ // Loop for each possible detector type
                if(PossibleMovement[j].equals(detectorMovementProperties.get(i).getMovement())){
                    // Find the corresponding movement
                    if(!trafficStateByDetectorGroups.get(i).Rate.equals("Unkonwn")){
                        // Get the proportions
                        double Proportion=FindTrafficProportion(detectorMovementProperties.get(i).getMovement(),
                                parameters,Movement);

                        if(DetectorGroup.equals("Exclusive Left Turn")||DetectorGroup.equals("Exclusive Right Turn")
                                ||DetectorGroup.equals("General Stopbar Detectors")) {
                            Rate = Rate + RateToNumberStopbar(trafficStateByDetectorGroups.get(i).Rate) *
                                    trafficStateByDetectorGroups.get(i).TotLanes * Proportion;
                        }else if(DetectorGroup.equals("Advance Detectors")){
                            Rate = Rate + RateToNumberAdvance(trafficStateByDetectorGroups.get(i).Rate) *
                                    trafficStateByDetectorGroups.get(i).TotLanes * Proportion;
                        }else{
                            System.out.println("Wrong input of detector group!");
                            System.exit(-1);
                        }

                        Occ=Occ+trafficStateByDetectorGroups.get(i).AvgOcc*trafficStateByDetectorGroups.get(i).TotLanes
                                *Proportion;
                        TotLanes=TotLanes+trafficStateByDetectorGroups.get(i).TotLanes*Proportion;
                        NumMatchedDetectorMovement=NumMatchedDetectorMovement+1;
                        ThresholdLow=ThresholdLow+trafficStateByDetectorGroups.get(i).Thresholds[0];
                        ThresholdHigh=ThresholdHigh+trafficStateByDetectorGroups.get(i).Thresholds[1];
                    }
                    break;
                }
            }
        }
        if(TotLanes>0){
            Rate=Rate/TotLanes;
            Occ=Occ/TotLanes;
            ThresholdLow=ThresholdLow/NumMatchedDetectorMovement;
            ThresholdHigh=ThresholdHigh/NumMatchedDetectorMovement;
        }
        return new double[]{Rate,Occ,TotLanes,ThresholdLow,ThresholdHigh};
    }

    /**
     *
     * @param Rate String
     * @return int: 0,1,2
     */
    public static int RateToNumberStopbar(String Rate){
        // This function is used to convert rate to number for stopbar detectors
        if(Rate.equals("Uncongested"))
            return 1;
        else if(Rate.equals("Congested/Queue Spillback"))
            return 2;
        else{
            System.out.println("Unknown Rate For Stopbar Detector!");
            return 0;
        }
    }

    /**
     *
     * @param Rate String
     * @return int: 0,1,2,3
     */
    public static int RateToNumberAdvance(String Rate){
        // This function is used to convert rate to number for advance detectors
        if(Rate.equals("Uncongested"))
            return 1;
        else if(Rate.equals("Congested"))
            return 2;
        else if(Rate.equals("Queue Spillback"))
            return 3;
        else{
            System.out.println("Unknown Rate For Advance Detector!");
            return 0;
        }
    }

    /**
     *
     * @param aimsunApproach AimsunApproach
     * @param parameters Parameters
     * @return QueueThreshold
     */
    public static QueueThreshold CalculateQueueThresholds(AimsunApproach aimsunApproach,Parameters parameters){
        // This function is used to calculate the queue thresholds
        // Note: this method is more detector-driven, which means it relies heavily on the detector layouts.
        //       With no detectors, we have no ideas on the queue thresholds. As a result, we have no way to
        //       assign the queues.
        // Note: The calculation of thresholds for downstream lanes can be done by using the physical geometry layout.
        //       But it is not clear how to calculate the thresholds for the upstream lanes.

        // Get number of exclusive left turn lanes and the length of the left-turn pocket
        double NumExclusiveLTLanes=aimsunApproach.getGeoDesign().getExclusiveLeftTurn().getNumLanes();
        double LTPocket=aimsunApproach.getGeoDesign().getExclusiveLeftTurn().getPocket();

        // Get number of exclusive right turn lanes and the length of the right-turn pocket
        double NumExclusiveRTLanes=aimsunApproach.getGeoDesign().getExclusiveRightTurn().getNumLanes();
        double RTPocket=aimsunApproach.getGeoDesign().getExclusiveRightTurn().getPocket();

        // Calculate the lane numbers for left-turn, through, and right-turn movements at general stopbar detectors
        double [] LanePortionByMovement=new double[]{0,0,0};
        double TotDownstreamLane=0;
        if(aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().size()!=0){// If stopbar detectors exist
            for(int i=0;i<aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().size();i++){// Loop for each detector type
                // There exist different types of detectors that belong to the category of general stopbar detectors
                String DetectorType=aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().get(i).getMovement();
                double ProportionLeft=FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=FindTrafficProportion(DetectorType, parameters,"Right");
                for(int j=0;j<aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().get(i).getNumberOfLanes().size();j++){
                    // Loop for each detector
                    double NumOfLane=aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors().get(i).getNumberOfLanes().get(j);
                    TotDownstreamLane=TotDownstreamLane+NumOfLane;
                    LanePortionByMovement[0]=LanePortionByMovement[0]+NumOfLane*ProportionLeft;
                    LanePortionByMovement[1]=LanePortionByMovement[1]+NumOfLane*ProportionThrough;
                    LanePortionByMovement[2]=LanePortionByMovement[2]+NumOfLane*ProportionRight;
                }
            }
        }
        // Rescale by the number of downstream lanes just in case with incomplete detector coverage (missing or broken)
        if(TotDownstreamLane>0){
            LanePortionByMovement[0]=LanePortionByMovement[0]*((double)aimsunApproach.getGeoDesign().getNumOfDownstreamLanes())/TotDownstreamLane;
            LanePortionByMovement[1]=LanePortionByMovement[1]*((double)aimsunApproach.getGeoDesign().getNumOfDownstreamLanes())/TotDownstreamLane;
            LanePortionByMovement[2]=LanePortionByMovement[2]*((double)aimsunApproach.getGeoDesign().getNumOfDownstreamLanes())/TotDownstreamLane;
        }

        // Calculate the lane numbers for left-turn, through, and right-turn movements at Advance detectors
        double [] LanePortionByMovementAdvance=new double[]{0,0,0};
        double TotUpstreamLane=0;
        double DistanceToAdvanceDetector=0;
        if(aimsunApproach.getDetectorProperty().getAdvanceDetectors().size()!=0){// If Advance detectors exist
            for(int i=0;i<aimsunApproach.getDetectorProperty().getAdvanceDetectors().size();i++){// Loop for each detector type
                String DetectorType=aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getMovement();
                double ProportionLeft=FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=FindTrafficProportion(DetectorType, parameters,"Right");
                double DistanceToStopbar=0;
                for(int j=0;j<aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getNumberOfLanes().size();j++){
                    // Loop for each detector
                    double NumOfLane=aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getNumberOfLanes().get(j);
                    TotUpstreamLane=TotUpstreamLane+NumOfLane;
                    LanePortionByMovementAdvance[0]=LanePortionByMovementAdvance[0]+NumOfLane*ProportionLeft;
                    LanePortionByMovementAdvance[1]=LanePortionByMovementAdvance[1]+NumOfLane*ProportionThrough;
                    LanePortionByMovementAdvance[2]=LanePortionByMovementAdvance[2]+NumOfLane*ProportionRight;

                    if(aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getDistancesToStopbar().get(j)>DistanceToStopbar)
                        DistanceToStopbar=aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getDistancesToStopbar().get(j);
                }
                // Get the maximum distance to the Advance detectors
                if(DistanceToAdvanceDetector<DistanceToStopbar){
                    DistanceToAdvanceDetector=DistanceToStopbar;
                }
            }
        }
        // Rescale by the number of upstream lanes just in case with incomplete detector coverage (missing or broken)
        if(TotDownstreamLane>0){
            LanePortionByMovementAdvance[0]=LanePortionByMovementAdvance[0]*
                    ((double)aimsunApproach.getGeoDesign().getNumOfUpstreamLanes())/TotUpstreamLane;
            LanePortionByMovementAdvance[1]=LanePortionByMovementAdvance[1]*
                    ((double)aimsunApproach.getGeoDesign().getNumOfUpstreamLanes())/TotUpstreamLane;
            LanePortionByMovementAdvance[2]=LanePortionByMovementAdvance[2]*
                    ((double)aimsunApproach.getGeoDesign().getNumOfUpstreamLanes())/TotUpstreamLane;
        }
        if(DistanceToAdvanceDetector==0) {// No Advance detectors, use the default value
            DistanceToAdvanceDetector = parameters.intersectionParams.DistanceAdvanceDetector;
        }

        // Determine the queue threshold for left-turn, through, and right-turn movements
        double NumJamVehPerLane=5280/parameters.vehicleParams.JamSpacing;
        QueueThresholdByMovement queueThresholdLeft=CalculateQueueThresholdsForMovement(NumExclusiveLTLanes,LanePortionByMovementAdvance
            , LanePortionByMovement, NumJamVehPerLane, LTPocket, DistanceToAdvanceDetector,aimsunApproach, parameters,"Left");
        QueueThresholdByMovement queueThresholdRight=CalculateQueueThresholdsForMovement(NumExclusiveRTLanes,LanePortionByMovementAdvance
                , LanePortionByMovement, NumJamVehPerLane, RTPocket, DistanceToAdvanceDetector,aimsunApproach, parameters, "Right");
        QueueThresholdByMovement queueThresholdThrough=CalculateQueueThresholdsForMovement(0,LanePortionByMovementAdvance
                , LanePortionByMovement, NumJamVehPerLane, Math.max(LTPocket,RTPocket), DistanceToAdvanceDetector,
                aimsunApproach, parameters,"Through");

        QueueThreshold queueThreshold=new QueueThreshold(queueThresholdLeft, queueThresholdThrough,queueThresholdRight);
        return queueThreshold;
    }

    /**
     *
     * @param NumExclusiveLane Number of exclusive lanes
     * @param LanePortionByMovementAdvance Lane portion by movement at advance detectors
     * @param LanePortionByMovementGeneral Lane portion by movement at general stopbar
     * @param NumJamVehPerLane Number of jam vehicles per lane
     * @param TurnPocket Turning pocket
     * @param DistanceToAdvanceDetector Distance to advance detectors
     * @param aimsunApproach AimsunApproach
     * @param parameters Parameters
     * @param Movement String
     * @return QueueThresholdByMovement
     */
    public static QueueThresholdByMovement CalculateQueueThresholdsForMovement(double NumExclusiveLane,double [] LanePortionByMovementAdvance
            , double [] LanePortionByMovementGeneral, double NumJamVehPerLane, double TurnPocket, double DistanceToAdvanceDetector
            ,AimsunApproach aimsunApproach, Parameters parameters,String Movement){
        // This function is used to calculate queue thresholds for an individual movement

        double LinkLength=aimsunApproach.getGeoDesign().getLinkLength();
        double NumUpstreamLanes=aimsunApproach.getGeoDesign().getNumOfUpstreamLanes();
        double NumDownstreamLanes=aimsunApproach.getGeoDesign().getNumOfDownstreamLanes();
        double ExclusiveLeftTurnLanes=0;
        if(!aimsunApproach.getGeoDesign().getExclusiveLeftTurn().equals(null)){
            ExclusiveLeftTurnLanes=aimsunApproach.getGeoDesign().getExclusiveLeftTurn().getNumLanes();
        }
        double ExclusiveRightTurnLanes=0;
        if(!aimsunApproach.getGeoDesign().getExclusiveRightTurn().equals(null)){
            ExclusiveRightTurnLanes=aimsunApproach.getGeoDesign().getExclusiveRightTurn().getNumLanes();
        }

        // Get the index of movements
        int Index=-1;
        double GreenTime=0;
        if(Movement.equals("Left")){
            Index=0;
            GreenTime=parameters.signalSettings.LeftTurnGreen;
        }else if(Movement.equals("Through")){
            Index=1;
            GreenTime=parameters.signalSettings.ThroughGreen;
        }else if(Movement.equals("Right")){
            Index=2;
            GreenTime=parameters.signalSettings.RightTurnGreen;
        }else{
            System.out.println("Wrong input of movement type!");
            System.exit(-1);
        }
        double VehicleHeadway=parameters.intersectionParams.SaturationHeadway;
        double VehiclePassageAtMaxGreen=GreenTime/VehicleHeadway;

        // **********Rescale the lane proportion for the movement************
        if(LanePortionByMovementAdvance[Index]==0){// Have no information at Advance detectors
            if(NumExclusiveLane>0 || LanePortionByMovementGeneral[Index]>0){ // But downstream has information
                // In this case, over-write the number of advance lanes
                // Try to use smaller values to avoid the case that the downstream has more lanes than the upstream within a link
                if(Index==2){ // For through movements
                    LanePortionByMovementAdvance[Index]=LanePortionByMovementGeneral[Index]*NumUpstreamLanes/
                            (ExclusiveLeftTurnLanes+ExclusiveRightTurnLanes+NumDownstreamLanes);
                }else{ // For left-turn and right-turn movements
                    LanePortionByMovementAdvance[Index]=(NumExclusiveLane+LanePortionByMovementGeneral[Index])*NumUpstreamLanes/
                            (ExclusiveLeftTurnLanes+ExclusiveRightTurnLanes+NumDownstreamLanes);
                }

            }
        }else{// Have information from advance detectors
            if(LanePortionByMovementGeneral[Index]==0 && NumExclusiveLane==0){// Downstream has no information
                // Rescale the proportions
                LanePortionByMovementGeneral[Index]=LanePortionByMovementAdvance[Index]*(ExclusiveLeftTurnLanes+
                        ExclusiveRightTurnLanes+NumDownstreamLanes)/NumUpstreamLanes;
            }
        }

        // **********Calculate different components of queues************
        double QueueExclusive;
        double QueueGeneral;
        double QueueAdvance;
        double QueueMaxGreen;
        if(LanePortionByMovementAdvance[Index]==0&& LanePortionByMovementGeneral[Index]==0 && NumExclusiveLane==0){
            QueueExclusive=0;
            QueueGeneral=0;
            QueueMaxGreen=0;
            QueueAdvance=0;
        }else{
            QueueExclusive=NumExclusiveLane*NumJamVehPerLane*TurnPocket/5280.0; // Vehicles in the turning pocket
            //Vehicles from stopbar to the location of advance detectors
            QueueGeneral=LanePortionByMovementGeneral[Index]*DistanceToAdvanceDetector*NumJamVehPerLane/5280.0;
            QueueMaxGreen=LanePortionByMovementAdvance[Index]*VehiclePassageAtMaxGreen;
            // Vehicles from the location of advance detectors to the end of the link
            QueueAdvance=LanePortionByMovementAdvance[Index]*NumJamVehPerLane*Math.max(0,LinkLength-DistanceToAdvanceDetector)/5280.0;
        }

        double QueueToAdvance=QueueExclusive+QueueGeneral;
        double QueueToEnd=QueueToAdvance+QueueAdvance;
        // Even though green time is fully used, there exists a queue reaching the location of advance detectors
        double QueueWithMaxGreen=QueueToAdvance+QueueMaxGreen;
        QueueWithMaxGreen=Math.min(QueueWithMaxGreen,QueueToEnd);
        QueueThresholdByMovement queueThresholdByMovement=new QueueThresholdByMovement(QueueToAdvance,QueueWithMaxGreen,QueueToEnd);
        return queueThresholdByMovement;
    }

    /**
     *
     * @param aggregatedTrafficStatesAdvance AggregatedTrafficStates at advance detectors
     * @param aggregatedTrafficStatesGeneral AggregatedTrafficStates at general stopbar detectors
     * @param aggregatedTrafficStatesLeft AggregatedTrafficStates at exclusive left turn detectors
     * @param aggregatedTrafficStatesRight AggregatedTrafficStates at exclusive right turn detectors
     * @param TurnIndicator int []
     * @param queueThreshold QueueThreshold
     * @return AssessmentStateAndQueue class
     */
    public static AssessmentStateAndQueue MakeADecision(AggregatedTrafficStates aggregatedTrafficStatesAdvance
            , AggregatedTrafficStates aggregatedTrafficStatesGeneral, AggregatedTrafficStates aggregatedTrafficStatesLeft
            , AggregatedTrafficStates aggregatedTrafficStatesRight, int [] TurnIndicator, QueueThreshold queueThreshold ){
        // This function is used to make a final decision for left-turn, through, and right-turn movements at the approach level

        String[] StatusAssessment;
        int [] QueueAssessment;

        // Get the states for left-turn, through, and right-turn movements
        double [] AdvanceRate=aggregatedTrafficStatesAdvance.AggregatedStatus;
        double [] GeneralRate=aggregatedTrafficStatesGeneral.AggregatedStatus;
        double [] ExclusiveLeftRate=aggregatedTrafficStatesLeft.AggregatedStatus;
        double [] ExclusiveRightRate=aggregatedTrafficStatesRight.AggregatedStatus;

        double DownstreamStatusLeft=MeanWithoutZero(new double[]{ExclusiveLeftRate[0],GeneralRate[0]});
        double AdvanceStatusLeft=AdvanceRate[0];
        double [] DownstreamOccAndThresholdLeft=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                , aggregatedTrafficStatesRight,"Left");
        double [] UpstreamOccAndThresholdLeft;
        if(aggregatedTrafficStatesAdvance.AggregatedStatus[0]>0){
            UpstreamOccAndThresholdLeft=new double[]{aggregatedTrafficStatesAdvance.AvgOccupancy[0],aggregatedTrafficStatesAdvance.ThresholdLow[0],
                    aggregatedTrafficStatesAdvance.ThresholdHigh[0]};
        }else{
            UpstreamOccAndThresholdLeft=new double[]{0,0,0};
        }

        double DownstreamStatusThrough=GeneralRate[1];
        double AdvanceStatusThrough=AdvanceRate[1];
        double [] DownstreamOccAndThresholdThrough=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                , aggregatedTrafficStatesRight,"Through");
        double [] UpstreamOccAndThresholdThrough;
        if(aggregatedTrafficStatesAdvance.AggregatedStatus[1]>0){
            UpstreamOccAndThresholdThrough=new double[]{aggregatedTrafficStatesAdvance.AvgOccupancy[1],aggregatedTrafficStatesAdvance.ThresholdLow[1],
                    aggregatedTrafficStatesAdvance.ThresholdHigh[1]};
        }else{
            UpstreamOccAndThresholdThrough=new double[]{0,0,0};
        }

        double DownstreamstatusRight=MeanWithoutZero(new double[]{ExclusiveRightRate[2],GeneralRate[2]});
        double AdvanceStatusRight=AdvanceRate[2];
        double [] DownstreamOccAndThresholdRight=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                , aggregatedTrafficStatesRight,"Right");
        double [] UpstreamOccAndThresholdRight;
        if(aggregatedTrafficStatesAdvance.AggregatedStatus[2]>0){
            UpstreamOccAndThresholdRight=new double[]{aggregatedTrafficStatesAdvance.AvgOccupancy[2],aggregatedTrafficStatesAdvance.ThresholdLow[2],
                    aggregatedTrafficStatesAdvance.ThresholdHigh[2]};
        }else{
            UpstreamOccAndThresholdRight=new double[]{0,0,0};
        }

        // Check whether there exist lane blockages
        double BlockageThresholdDownstream=1.5;
        double BlockageThresholdUpstream=2.5;
        int [] Blockage=new int[]{0,0,0};
        // Check left turn movement
        if(DownstreamStatusLeft>=BlockageThresholdDownstream && AdvanceStatusLeft>=BlockageThresholdUpstream){
            if(DownstreamStatusThrough<BlockageThresholdDownstream && AdvanceStatusThrough>=BlockageThresholdUpstream ){
                Blockage[0]=1;
            }
        }

        // Check right turn movement
        if(DownstreamstatusRight>=BlockageThresholdDownstream && AdvanceStatusRight>=BlockageThresholdUpstream){
            if(DownstreamStatusThrough<BlockageThresholdDownstream && AdvanceStatusThrough>=BlockageThresholdUpstream){
                Blockage[2]=1;
            }
        }
        // Check through movement
        if(DownstreamStatusThrough>=BlockageThresholdDownstream && AdvanceStatusThrough>=BlockageThresholdUpstream){
            if((DownstreamStatusLeft<BlockageThresholdDownstream &&AdvanceStatusLeft>=BlockageThresholdUpstream) ||
                    (DownstreamstatusRight <BlockageThresholdDownstream && AdvanceStatusRight>=BlockageThresholdUpstream)){
                Blockage[1]=1;
            }
        }
        //Left turn
        AssessmentStateAndQueueByMovement assessmentStateAndQueueLeft=DecideStatusQueueForMovement(Blockage, queueThreshold, DownstreamStatusLeft
                , AdvanceStatusLeft,DownstreamOccAndThresholdLeft, UpstreamOccAndThresholdLeft, "Left");
        //Through
        AssessmentStateAndQueueByMovement assessmentStateAndQueueThrough=DecideStatusQueueForMovement(Blockage, queueThreshold, DownstreamStatusThrough
                , AdvanceStatusThrough,DownstreamOccAndThresholdThrough, UpstreamOccAndThresholdThrough, "Through");
        //Right turn
        AssessmentStateAndQueueByMovement assessmentStateAndQueueRight=DecideStatusQueueForMovement(Blockage, queueThreshold, DownstreamstatusRight
                , AdvanceStatusRight,DownstreamOccAndThresholdRight, UpstreamOccAndThresholdRight, "Right");

        StatusAssessment=new String[]{assessmentStateAndQueueLeft.StatusAssessment,assessmentStateAndQueueThrough.StatusAssessment,
                assessmentStateAndQueueRight.StatusAssessment};
        QueueAssessment=new int[]{assessmentStateAndQueueLeft.QueueAssessment,assessmentStateAndQueueThrough.QueueAssessment,
                assessmentStateAndQueueRight.QueueAssessment};

        // Update when no such a movement
        for (int i=0;i<TurnIndicator.length;i++){
            if(TurnIndicator[i]==0){
                StatusAssessment[i]="No Movement";
                QueueAssessment[i]=-2; // -2 stands for "No Movement"
            }
        }
        return new AssessmentStateAndQueue(StatusAssessment,QueueAssessment);
    }

    /**
     *
     * @param Blockage int [] Blockage indicators
     * @param queueThreshold QueueThreshold
     * @param DownstreamStatus double
     * @param UpstreamStatus double
     * @param DownstreamOccAndThreshold double []
     * @param UpstreamOccAndThreshold double []
     * @param Movement String
     * @return AssessmentStateAndQueueByMovement class
     */
    public static AssessmentStateAndQueueByMovement DecideStatusQueueForMovement(int[] Blockage, QueueThreshold queueThreshold, double DownstreamStatus, double UpstreamStatus
            , double[] DownstreamOccAndThreshold, double[] UpstreamOccAndThreshold, String Movement){
        // This function is used to decide the status and queue for each movement

        if(UpstreamStatus<0 ||DownstreamStatus<0){
            System.out.println("Wrong upstream/downstream traffic status!");
            System.exit(-1);
        }

        double QueueToEnd=0;
        double QueueWithMaxGreen=0;
        double QueueToAdvance=0;
        if(Movement.equals("Left")){
            Blockage[0]=0; // Set it zero, only check other movements
            QueueToAdvance=queueThreshold.QueueThresholdLeft.QueueToAdvance;
            QueueWithMaxGreen=queueThreshold.QueueThresholdLeft.QueueWithMaxGreen;
            QueueToEnd=queueThreshold.QueueThresholdLeft.QueueToEnd;
        }else if(Movement.equals("Through")){
            Blockage[1]=0; // Set it zero, only check other movements
            QueueToAdvance=queueThreshold.QueueThresholdThrough.QueueToAdvance;
            QueueWithMaxGreen=queueThreshold.QueueThresholdThrough.QueueWithMaxGreen;
            QueueToEnd=queueThreshold.QueueThresholdThrough.QueueToEnd;
        }else if(Movement.equals("Right")){
            Blockage[2]=0; // Set it zero, only check other movements
            QueueToAdvance=queueThreshold.QueueThresholdRight.QueueToAdvance;
            QueueWithMaxGreen=queueThreshold.QueueThresholdRight.QueueWithMaxGreen;
            QueueToEnd=queueThreshold.QueueThresholdRight.QueueToEnd;
        }else{
            System.out.println("Wrong input of movements!");
            System.exit(-1);
        }

        double UpstreamThresholdHigh=2.5;
        double UpstreamThresholdLow=1.5;
        double DownstreamThreshold=1.5;

        String Status;
        double Queue;
        if(UpstreamStatus>UpstreamThresholdHigh) // Upstream is spillback
        {
            if(DownstreamStatus==0){// No downstream detectors
                if(Blockage[0]+Blockage[1]+Blockage[2]>0){ // Blockage exists
                    Status="Lane Blockage By Other Movements";
                }else{ // Spillback
                    Status="Queue Spillback In Upstream";
                }
            }else if(DownstreamStatus>DownstreamThreshold){// Downstream is congested/Spillback
                Status="Spillback Caused By Downstream Traffic";
            }else{ // Downstream is un-congested
                Status="Lane Blockage By Other Movements";
            }
            Queue=QueueWithMaxGreen+(QueueToEnd-QueueWithMaxGreen)*
                    Math.max(0,(UpstreamOccAndThreshold[0]-UpstreamOccAndThreshold[2])/(100-UpstreamOccAndThreshold[2]));
            if(Status.equals("Lane Blockage By Other Movements")){
                Queue=Queue-QueueToAdvance;
            }
        }else if(UpstreamStatus<=UpstreamThresholdHigh && UpstreamStatus>UpstreamThresholdLow){ // Upstream is congested
            if(DownstreamStatus==0) {// No downstream detectors
                Status="Congestion In Upstream";
            }else if(DownstreamStatus>DownstreamThreshold) {// Downstream is congested/Spillback
                Status="Heavy Congestion Caused By Downstream Traffic";
            }else{  // Downstream is un-congested
                Status="Congestion With Downstream Free";
            }
            Queue=QueueToAdvance+(QueueWithMaxGreen-QueueToAdvance)*
                    Math.max(0,(UpstreamOccAndThreshold[0]-UpstreamOccAndThreshold[1])/(UpstreamOccAndThreshold[2]-UpstreamOccAndThreshold[1]));
        }else if(UpstreamStatus<=UpstreamThresholdLow){ // Upstream is uncongested
            if(DownstreamStatus==0) {// No downstream detectors
                Status="No Congestion In Upstream";
            }else if(DownstreamStatus>DownstreamThreshold) {// Downstream is congested/Spillback
                Status="Light Congestion Caused By Downstream Traffic";
            }else{  // Downstream is un-congested
                Status="No Congestion";
            }
            Queue=QueueToAdvance*Math.max(0,UpstreamOccAndThreshold[0]/UpstreamOccAndThreshold[1]);
        }else{ //No upstream detectors
            // Note the occupancy values from downstream detectors contain some errors
            if(DownstreamStatus==0){
                Status="Unknown Traffic State";
                Queue=-1;
            }else if(DownstreamStatus>DownstreamThreshold){
                Status="Downstream Congestion/Spillback";
                Queue=(QueueWithMaxGreen-QueueToAdvance)+(QueueToEnd-QueueWithMaxGreen+QueueToAdvance)*
                        Math.max(0,(DownstreamOccAndThreshold[0]-DownstreamOccAndThreshold[2])/(100-DownstreamOccAndThreshold[2]));
            }else{
                Status="No Congestion In Downstream";
                Queue=(QueueWithMaxGreen-QueueToAdvance)* Math.max(0,DownstreamOccAndThreshold[0]/DownstreamOccAndThreshold[2]);
            }
        }

        AssessmentStateAndQueueByMovement assessmentStateAndQueueByMovement=new AssessmentStateAndQueueByMovement(Status,(int)Math.ceil(Queue));
        return assessmentStateAndQueueByMovement;
    }

    /**
     *
     * @param aggregatedTrafficStatesGeneral AggregatedTrafficStates class
     * @param aggregatedTrafficStatesLeft AggregatedTrafficStates class
     * @param aggregatedTrafficStatesRight AggregatedTrafficStates class
     * @param Movement String
     * @return DownstreamAvgOccAndThreshold: double[]{AvgOcc,ThresholdLow,ThresholdHigh};
     */
    public static double [] GetDownstreamAvgOccAndThresholdByMovement(AggregatedTrafficStates aggregatedTrafficStatesGeneral, AggregatedTrafficStates
            aggregatedTrafficStatesLeft, AggregatedTrafficStates aggregatedTrafficStatesRight, String Movement){
        // This function is used to get the downstream average occupancy and thresholds by movement

        int Index=-1;
        if(Movement.equals("Left")){
            Index=0;
        }else if(Movement.equals("Through")){
            Index=1;
        }else if(Movement.equals("Right")){
            Index=2;
        }else{
            System.out.println("Wrong input of traffic movement!");
            System.exit(-1);
        }

        double AvgOcc=0;
        double TotLanes=0;
        double ThresholdLow=0;
        double ThresholdHigh=0;
        if(aggregatedTrafficStatesLeft.AggregatedStatus[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesLeft.AvgOccupancy[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesLeft.AggregatedTotLanes[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesLeft.ThresholdLow[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesLeft.ThresholdHigh[Index];
        }

        if(aggregatedTrafficStatesGeneral.AggregatedStatus[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesGeneral.AvgOccupancy[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesGeneral.AggregatedTotLanes[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesGeneral.ThresholdLow[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesGeneral.ThresholdHigh[Index];
        }

        if(aggregatedTrafficStatesRight.AggregatedStatus[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesRight.AvgOccupancy[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesRight.AggregatedTotLanes[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesRight.ThresholdLow[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesRight.ThresholdHigh[Index];
        }

        double [] DownstreamAvgOccAndThreshold;
        if(TotLanes>0){
            AvgOcc=AvgOcc/TotLanes;
            ThresholdLow=ThresholdLow/TotLanes;
            ThresholdHigh=ThresholdHigh/TotLanes;
            DownstreamAvgOccAndThreshold=new double[]{AvgOcc,ThresholdLow,ThresholdHigh};
        }else{
            DownstreamAvgOccAndThreshold=new double[]{0,0,0};
        }

        return DownstreamAvgOccAndThreshold;
    }

    /**
     *
     * @param Input double[]
     * @return double
     */
    public static double MeanWithoutZero(double [] Input){
        // This function returns the mean for non-zero values

        double Output=0;
        double NumNonZero=0;
        for(int i=0;i<Input.length;i++){
            if(Input[i]!=0){
                NumNonZero=NumNonZero+1;
                Output=Output+Input[i];
            }
        }
        if(NumNonZero>0){
            Output=Output/NumNonZero;
        }
        return Output;
    }


    // *****************************************************************************************************
    // ******************Make a decision of traffic states at different types of detectors******************
    // *****************************************************************************************************

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @param parameters Parameters class
     * @return TrafficStateByApproach class
     */
    public static TrafficStateByApproach MakeDecisionTrafficState(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures,Parameters parameters){
        // This function is used to make decision for traffic states at different types of detectors (By "Detector", not By "Movement")

        // Get the end time of the query measures
        int Time=queryMeasures.TimeOfDay[1];
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
    public static TrafficStateByDetectorType CheckDetectorGroupStatus(Connection con,DetectorMovementProperty detectorMovementProperty,String
            DetectorGroup,Parameters parameters,QueryMeasures queryMeasures){
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
                thresholdAndRate = GetRateAtStopbarDetector(AvgOcc, AvgLength, parameters, DetectorType);
            }else if(DetectorGroup.equals("Advance Detectors")) {
                thresholdAndRate=GetRateAtAdvanceDetector(AvgOcc,AvgLength, parameters, DetectorType);
            }else{
                System.out.println("Wrong Detector Group!");
                System.exit(-1);
            }
        }

        // Return the traffic states
        TrafficStateByDetectorType trafficStateByDetectorGroup=new TrafficStateByDetectorType(DetectorType,thresholdAndRate.Rate,
                thresholdAndRate.Thresholds,AvgOcc, AvgFlow,TotalLanes,Occupancies,Flows);
        return trafficStateByDetectorGroup;
    }

    /**
     *
     * @param AvgOcc Average occupancy
     * @param DetLength Detector length
     * @param parameters Parameters
     * @param DetectorType Detector type
     * @return ThresholdAndRate
     */
    public static ThresholdAndRate GetRateAtAdvanceDetector(double AvgOcc,double DetLength, Parameters parameters, String DetectorType){
        // This function is used to get the rate at Advance detectors

        // Get the maximum green ratio given the detector group
        double GreenTime=Math.max(parameters.signalSettings.LeftTurnGreen,
                Math.max(parameters.signalSettings.ThroughGreen,parameters.signalSettings.RightTurnGreen));

        // Get the start-up lost time, and saturation headway
        double StartUpLostTime=parameters.vehicleParams.StartupLostTime;
        double SaturationHeadway=parameters.intersectionParams.SaturationHeadway;

        // Get the saturation speeds for different movements: Use the speed for through movements
        double SaturationSpeedThrough=parameters.intersectionParams.SaturationSpeedThrough;
        double SaturationSpeedLeft=SaturationSpeedThrough;
        double SaturationSpeedRight=SaturationSpeedThrough;

        // Get the vehicle length
        double VehLength= parameters.vehicleParams.VehicleLength;

        // Time for a vehicle passing a detector for different movements (in seconds)
        double PassTimeThrough=(VehLength+DetLength)*3600/SaturationSpeedThrough/5280;
        double PassTimeLeft=(VehLength+DetLength)*3600/SaturationSpeedLeft/5280;
        double PassTimeRight=(VehLength+DetLength)*3600/SaturationSpeedRight/5280;

        // Get the proportions of vehicles for left-turn, through and right-turn
        double ProportionLeft= FindTrafficProportion(DetectorType, parameters,"Left");
        double ProportionThrough= FindTrafficProportion(DetectorType, parameters,"Through");
        double ProportionRight= FindTrafficProportion(DetectorType, parameters,"Right");

        // Get the number of vehicles within one cycle: considering the green time is fully used
        double NumVeh=GreenTime/SaturationHeadway;
        // Get the number of vehicles for each movement
        double NumVehLeft=NumVeh*ProportionLeft;
        double NumVehThrough=NumVeh*ProportionThrough;
        double NumVehRight=NumVeh*ProportionRight;

        // Get the discharging time given the current number of vehicles
        double DischargingTime=StartUpLostTime+PassTimeLeft*NumVehLeft+PassTimeThrough*NumVehThrough
                +PassTimeRight*NumVehRight;

        // Get the occupancy thresholds: red time & discharging time
        double OccThresholdLow=100*Math.min(1.0,DischargingTime/parameters.signalSettings.CycleLength);
        double OccThresholdHigh=100*Math.min(1.0, DischargingTime/parameters.signalSettings.CycleLength+
                (1-GreenTime/parameters.signalSettings.CycleLength));

        // We have three categories for Advance detectors
        // Using occupancy is good because it can respresent either a lane or a road network
        String Rate;
        if(AvgOcc<OccThresholdLow){
            Rate="Uncongested";
        }
        else if(AvgOcc>=OccThresholdLow && AvgOcc <OccThresholdHigh){
            Rate="Congested";
        }else{
            Rate="Queue Spillback";
        }
        ThresholdAndRate thresholdAndRate=new ThresholdAndRate(Rate,new double[]{OccThresholdLow,OccThresholdHigh});
        return thresholdAndRate;
    }

    /**
     *
     * @param AvgOcc Average occupancy
     * @param DetLength Detector length
     * @param parameters Parameters
     * @param DetectorType Detector type
     * @return ThresholdAndRate
     */
    public static ThresholdAndRate GetRateAtStopbarDetector(double AvgOcc,double DetLength, Parameters parameters, String DetectorType){
        // This function is used to get the rate at stopbar detectors by different movements
        // The flow count at stopbar detectors are not reliable, and therefore, we only use the occupancy data

        // Get the green ratio given the detector group
        double GreenTime=0;
        if(DetectorType.equals("Left Turn") || DetectorType.equals("Left Turn Queue")) {
            GreenTime = parameters.signalSettings.LeftTurnGreen;
        }
        else if(DetectorType.equals("Right Turn")|| DetectorType.equals("Right Turn Queue")){
            GreenTime=parameters.signalSettings.RightTurnGreen;
        }
        else
        {
            GreenTime=parameters.signalSettings.ThroughGreen;
        }

        // Get the start-up lost time, and saturation headway
        double StartUpLostTime=parameters.vehicleParams.StartupLostTime;
        double SaturationHeadway=parameters.intersectionParams.SaturationHeadway;

        // Get the saturation speeds for different movements
        double SaturationSpeedLeft=parameters.intersectionParams.SaturationSpeedLeft;
        double SaturationSpeedRight=parameters.intersectionParams.SaturationSpeedRight;
        double SaturationSpeedThrough=parameters.intersectionParams.SaturationSpeedThrough;

        // Get the vehicle length
        double VehLength= parameters.vehicleParams.VehicleLength;

        // Time for a vehicle passing a detector for different movements (in seconds)
        double PassTimeLeft=(VehLength+DetLength)*3600/SaturationSpeedLeft/5280;
        double PassTimeRight=(VehLength+DetLength)*3600/SaturationSpeedRight/5280;
        double PassTimeThrough=(VehLength+DetLength)*3600/SaturationSpeedThrough/5280;

        // Get the proportions of vehicles for left-turn, through and right-turn
        double ProportionLeft= FindTrafficProportion(DetectorType, parameters,"Left");
        double ProportionThrough= FindTrafficProportion(DetectorType, parameters,"Through");
        double ProportionRight= FindTrafficProportion(DetectorType, parameters,"Right");

        // Get the number of vehicles within one cycle: considering the green time is fully used
        double NumVeh=GreenTime/SaturationHeadway;
        // Get the number of vehicles for each movement
        double NumVehLeft=NumVeh*ProportionLeft;
        double NumVehThrough=NumVeh*ProportionThrough;
        double NumVehRight=NumVeh*ProportionRight;

        // Get the discharging time given the current number of vehicles
        double DischargingTime=StartUpLostTime+PassTimeLeft*NumVehLeft+PassTimeThrough*NumVehThrough+PassTimeRight*NumVehRight;

        // Get the occupancy threshold: red time + discharging time
        double OccThresholdLow=100*Math.min(1.0,DischargingTime/parameters.signalSettings.CycleLength);
        double OccThresholdHigh=100*Math.min(1.0, DischargingTime/parameters.signalSettings.CycleLength+
                (1-GreenTime/parameters.signalSettings.CycleLength));

        // We only have two categories for stopbar detectors: Queue spillback or no queue spillback
        String Rate;
        if(AvgOcc<OccThresholdHigh){
            Rate="Uncongested";
        }
        else{
            Rate="Congested/Queue Spillback";
        }
        ThresholdAndRate thresholdAndRate=new ThresholdAndRate(Rate,new double[]{OccThresholdLow,OccThresholdHigh});
        return  thresholdAndRate;
    }


    // **********************************************************************************
    // ******************Functions to get/update params and proportions******************
    // **********************************************************************************

    /**
     *
     * @param DetectorType Detector type
     * @param parameters Parameters
     * @param Movement Movement (String)
     * @return Proportion (double)
     */
    public static double FindTrafficProportion(String DetectorType, Parameters parameters,String Movement){
        // This function is used to find traffic proportions

        int Index=0;
        if(Movement.equals("Left")) {
            Index=0;
        }
        else if(Movement.equals("Through")){
            Index=1;
        }
        else if(Movement.equals("Right")) {
            Index=2;
        }
        else{
            System.out.println("Wrong input of traffic movements!");
            System.exit(-1);
        }
        // Find the corresponding proportions
        double Proportion=0;
        if(DetectorType.equals("Left Turn")){
            Proportion=parameters.turningPrortions.LeftTurn[Index];
        }else if(DetectorType.equals("Left Turn Queue")){
            Proportion=parameters.turningPrortions.LeftTurnQueue[Index];
        }
        else if(DetectorType.equals("Right Turn")){
            Proportion=parameters.turningPrortions.RightTurn[Index];
        }
        else if(DetectorType.equals("Right Turn Queue")){
            Proportion=parameters.turningPrortions.RightTurnQueue[Index];
        }
        else if(DetectorType.equals("Advance")){
            Proportion=parameters.turningPrortions.Advance[Index];
        }
        else if(DetectorType.equals("Advance Left Turn")){
            Proportion=parameters.turningPrortions.AdvanceLeftTurn[Index];
        }
        else if(DetectorType.equals("Advance Right Turn")){
            Proportion=parameters.turningPrortions.AdvanceRightTurn[Index];
        }
        else if(DetectorType.equals("Advance Through")){
            Proportion=parameters.turningPrortions.AdvanceThrough[Index];
        }
        else if(DetectorType.equals("Advance Through and Right")){
            Proportion=parameters.turningPrortions.AdvanceThroughAndRight[Index];
        }
        else if(DetectorType.equals("Advance Left and Through")){
            Proportion=parameters.turningPrortions.AdvanceLeftAndThrough[Index];
        }
        else if(DetectorType.equals("Advance Left and Right")){
            Proportion=parameters.turningPrortions.AdvanceLeftAndRight[Index];
        }
        else if(DetectorType.equals("All Movements")){
            Proportion=parameters.turningPrortions.AllMovements[Index];
        }
        else if(DetectorType.equals("Through")){
            Proportion=parameters.turningPrortions.Through[Index];
        }
        else if(DetectorType.equals("Left and Right")){
            Proportion=parameters.turningPrortions.LeftAndRight[Index];
        }
        else if(DetectorType.equals("Left and Through")){
            Proportion=parameters.turningPrortions.LeftAndThrough[Index];
        }
        else if(DetectorType.equals("Through and Right")){
            Proportion=parameters.turningPrortions.ThroughAndRight[Index];
        }else
        {
            System.out.println("Wrong input of detector movement!");
            System.exit(-1);
        }
        return Proportion;
    }

    /**
     *
     * @param parameters Parameters
     * @param aimsunApproach AimsunApproach
     * @return Parameters
     */
    public static Parameters UpdateSaturationSpeeds(Parameters parameters,AimsunApproach aimsunApproach){
        // This function is used to update the saturation speeds

        Double LeftTurnSpeed=GetTurningSpeedFromApproach(aimsunApproach,"Left Turn");
        if(LeftTurnSpeed>0){
            parameters.intersectionParams.SaturationSpeedLeft=LeftTurnSpeed;
        }
        Double ThroughSpeed=GetTurningSpeedFromApproach(aimsunApproach,"Through");
        if(ThroughSpeed>0){
            parameters.intersectionParams.SaturationSpeedThrough=ThroughSpeed;
        }
        Double RightTurnSpeed=GetTurningSpeedFromApproach(aimsunApproach,"Right Turn");
        if(RightTurnSpeed>0){
            parameters.intersectionParams.SaturationSpeedRight=RightTurnSpeed;
        }
        return parameters;
    }

    /**
     *
     * @param aimsunApproach AimsunApproach
     * @param Turn String
     * @return MaxSpeed(double)
     */
    public static Double GetTurningSpeedFromApproach(AimsunApproach aimsunApproach,String Turn){
        // This function is used to get the (maximum) turning speed from a given approach

        double MaxSpeed=-1;
        for(int i=0;i<aimsunApproach.getTurningBelongToApproach().getTurningProperty().size();i++){ // Loop for all turns
            if(aimsunApproach.getTurningBelongToApproach().getTurningProperty().get(i).getMovement().equals(Turn)){
                if(MaxSpeed<aimsunApproach.getTurningBelongToApproach().getTurningProperty().get(i).getTurningSpeed()){
                    // Get the corresponding maximum speed
                    MaxSpeed=aimsunApproach.getTurningBelongToApproach().getTurningProperty().get(i).getTurningSpeed();
                }
            }
        }
        return MaxSpeed;
    }

    /**
     *
     * @return Parameters
     */
    public static Parameters getDefaultParameters(){
        // This function is used to get the default parameters

        // Get vehicle parameters
        VehicleParams vehicleParams=new VehicleParams(MainFunction.cBlock.VehicleLength,
                MainFunction.cBlock.StartupLostTime,MainFunction.cBlock.JamSpacing);
        // Get intersection parameters
        IntersectionParams intersectionParams=new IntersectionParams(MainFunction.cBlock.SaturationHeadway,
                MainFunction.cBlock.SaturationSpeedLeft,MainFunction.cBlock.SaturationSpeedRight,
                MainFunction.cBlock.SaturationSpeedThrough,MainFunction.cBlock.DistanceAdvanceDetector,
                MainFunction.cBlock.LeftTurnPocket,MainFunction.cBlock.RightTurnPocket,
                MainFunction.cBlock.DistanceToEnd);
        // Get estimation parameters
        EstimationParams estimationParams=new EstimationParams(MainFunction.cBlock.FFSpeedForAdvDet,
                MainFunction.cBlock.OccThresholdForAdvDet);
        // Get turning proportions
        TurningPrortions turningPrortions=new TurningPrortions(
                MainFunction.cBlock.LeftTurn,MainFunction.cBlock.LeftTurnQueue,MainFunction.cBlock.AdvanceLeftTurn,
                MainFunction.cBlock.RightTurn,MainFunction.cBlock.RightTurnQueue,MainFunction.cBlock.AdvanceRightTurn,
                MainFunction.cBlock.Advance,MainFunction.cBlock.AllMovements,MainFunction.cBlock.AdvanceThrough,
                MainFunction.cBlock.Through,MainFunction.cBlock.AdvanceLeftAndThrough,MainFunction.cBlock.LeftAndThrough,
                MainFunction.cBlock.AdvanceLeftAndRight,MainFunction.cBlock.LeftAndRight,MainFunction.cBlock.AdvanceThroughAndRight,
                MainFunction.cBlock.ThroughAndRight);
        // Get signal settings
        SignalSettings signalSettings=new SignalSettings(MainFunction.cBlock.CycleLength,
                MainFunction.cBlock.LeftTurnGreen,MainFunction.cBlock.ThroughGreen,
                MainFunction.cBlock.RightTurnGreen,MainFunction.cBlock.LeftTurnSetting);

        Parameters parameters=new Parameters(vehicleParams,intersectionParams,signalSettings,turningPrortions,estimationParams);
        return parameters;
    }

    /**
     *
     * @param turningPrortions TurningPrortions class
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return TurningPrortions class
     */
    public static TurningPrortions UpdateVehicleProportions(TurningPrortions turningPrortions,Connection con,
                                                            AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to update vehicle's proportions given the queryMeasures

        // Update these values with field data
        double ProportionLeft=0;
        double ProportionThrough=0;
        double ProportionRight=0;

        // Get the counts and lanes [count, lane]
        double [] LeftTurnCountsAndLanes=getExclusiveLeftTurnCounts(con,aimsunApproach,queryMeasures); //[total count, total lane]
        double [] RightTurnCountsAndLanes=getExclusiveRightTurnCounts(con,aimsunApproach,queryMeasures);
        double [] AdvanceCountsAndLanes=getAdvanceCounts(con,aimsunApproach,queryMeasures);

        if((LeftTurnCountsAndLanes[0]>0 || RightTurnCountsAndLanes[0]>0) && AdvanceCountsAndLanes[0]>0){
            if(LeftTurnCountsAndLanes[0]+RightTurnCountsAndLanes[0]>AdvanceCountsAndLanes[0]){
                // We haven't check the flow balance at each intersection approach.
                // Therefore, it is possible ot have very low advance traffic counts (bad ones) but we still think it is "good".
                // In such a case, do not use the data; keep the default values.
                System.out.println("Incorrect measurements at Junction "+aimsunApproach.getJunctionID()+" and Section "+aimsunApproach.getFirstSectionID()+
                        ": Downstream flow greater than the upstream one!");
                return turningPrortions;
            }
        }

        // Calculate the proportions based on the observations
        if(AdvanceCountsAndLanes[0]>0 && AdvanceCountsAndLanes[1]>0){
            // Have values from Advance detectors
            if(LeftTurnCountsAndLanes[1]>0){
                // Have left turn values
                ProportionLeft=LeftTurnCountsAndLanes[0]/AdvanceCountsAndLanes[0];
                if(RightTurnCountsAndLanes[1]>0){
                    //Have both left turn and right-turn values
                    ProportionRight=RightTurnCountsAndLanes[0]/AdvanceCountsAndLanes[0];
                    ProportionThrough=Math.max(0,1-ProportionLeft-ProportionRight); // Just make sure no negative values

                }else{
                    // Only have left-turn values
                    ProportionThrough=(1-ProportionLeft)*
                            turningPrortions.Advance[1]/(turningPrortions.Advance[1]+turningPrortions.Advance[2]);
                    ProportionRight=1-ProportionLeft-ProportionThrough;
                }
            }else{
                // No left-turn values
                if(RightTurnCountsAndLanes[1]>0){
                    //Have only right-turn values
                    ProportionRight=RightTurnCountsAndLanes[0]/AdvanceCountsAndLanes[0];
                    ProportionThrough=(1-ProportionRight)*
                            turningPrortions.Advance[1]/(turningPrortions.Advance[0]+turningPrortions.Advance[1]);
                    ProportionLeft=1-ProportionRight-ProportionThrough;
                }
            }

            // Update the proportions at each type of detectors
            if(ProportionLeft+ProportionThrough+ProportionRight>0) {
                // Update for all movements
                turningPrortions.Advance = new double[]{ProportionLeft, ProportionThrough, ProportionRight};
                turningPrortions.AllMovements = turningPrortions.Advance;
                // Update for through and right-turns
                turningPrortions.AdvanceThroughAndRight = new double[]{0,
                        ProportionThrough / (ProportionThrough + ProportionRight), ProportionRight / (ProportionThrough + ProportionRight)};
                turningPrortions.ThroughAndRight = turningPrortions.AdvanceThroughAndRight;
                // Update for left-turn and through
                turningPrortions.AdvanceLeftAndThrough = new double[]{ProportionLeft / (ProportionThrough + ProportionLeft),
                        ProportionThrough / (ProportionThrough + ProportionLeft), 0};
                turningPrortions.LeftAndThrough = turningPrortions.AdvanceLeftAndThrough;
                // Update for left-turn and right-turn
                turningPrortions.AdvanceLeftAndRight = new double[]{ProportionLeft / (ProportionLeft + ProportionRight), 0,
                        ProportionRight / (ProportionLeft + ProportionRight)};
                turningPrortions.LeftAndRight = turningPrortions.AdvanceLeftAndRight;
            }
        }
        return turningPrortions;
    }

    /**
     *
     * @param turningPrortions TurningPrortions class
     * @param LaneIndicator int []
     * @return TurningPrortions class
     */
    public static TurningPrortions UpdateVehicleProportionsAccordingToLandIndicator(TurningPrortions turningPrortions,int [] LaneIndicator){
        // This function is used to update vehicle proportions according to lane
        // indicators: only for lanes/detectors with all movements

        double TPLeft=turningPrortions.Advance[0];
        double TPThrough=turningPrortions.Advance[1];
        double TPRight=turningPrortions.Advance[2];
        if(LaneIndicator[0]==0 && LaneIndicator[1]==1 && LaneIndicator[2]==1 && (TPThrough+TPRight)>0){// No left turn
            turningPrortions.Advance=new double[]{0,TPThrough+TPLeft*TPThrough/(TPThrough+TPRight)
                    ,TPRight+TPLeft*TPRight/(TPThrough+TPRight)};
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==0 &&LaneIndicator[2]==1 && (TPLeft+TPRight)>0){// No through
            turningPrortions.Advance=new double[]{TPLeft+TPThrough*TPLeft/(TPLeft+TPRight),0,
                    TPRight+TPThrough*TPRight/(TPLeft+TPRight)};
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==1 &&LaneIndicator[2]==0 &&(TPLeft+TPThrough)>0){// No right turn
            turningPrortions.Advance=new double[]{TPLeft+TPRight*TPLeft/(TPLeft+TPThrough),
                    TPThrough+TPRight*TPThrough/(TPLeft+TPThrough),0};
        }

        if(LaneIndicator[0]==0 && LaneIndicator[1]==0 &&LaneIndicator[2]==1){ // No left turn and through
            turningPrortions.Advance=new double[]{0,0,1};
        }
        if(LaneIndicator[0]==0 && LaneIndicator[1]==1 &&LaneIndicator[2]==0){ // No left turn and right turn
            turningPrortions.Advance=new double[]{0,1,0};
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==0 &&LaneIndicator[2]==0){ // No through and right turn
            turningPrortions.Advance=new double[]{1,0,0};
        }

        turningPrortions.AllMovements=turningPrortions.Advance;
        return turningPrortions;
    }

    /**
     *
     * @param parameters Parameters class
     * @param aimsunControlPlanJunctionList List<AimsunControlPlanJunction> class
     * @param aimsunApproach AimsunApproach
     * @return Parameters class
     */
    public static Parameters UpdateSignalSettings(Parameters parameters,List<AimsunControlPlanJunction>
            aimsunControlPlanJunctionList,AimsunApproach aimsunApproach){
        // This function is used to update signal settings with field data
        // Also try to update the saturation speed for left turn movements

        SignalSettings tmpSignalSettings=getSignalData.GetGreenTimesForApproachFromSignalPlansInAimsun(aimsunControlPlanJunctionList,
                aimsunApproach);
        if(tmpSignalSettings!=null){
            parameters.signalSettings=tmpSignalSettings;
            // Reduce the saturation speed for permitted left turns
            if(tmpSignalSettings.LeftTurnSetting.equals("Permitted"))
                // Reduced to 1/3
                parameters.intersectionParams.SaturationSpeedLeft=parameters.intersectionParams.SaturationSpeedLeft*0.35;
            else if(tmpSignalSettings.LeftTurnSetting.equals("Protected-Permitted"))
                // Reduced to 2/3
                parameters.intersectionParams.SaturationSpeedLeft=parameters.intersectionParams.SaturationSpeedLeft*0.7;
        }else {
            parameters.signalSettings.LeftTurnSetting="N/A";
        }
        return parameters;
    }



    // ********************************************************************
    // ******************Functions to get data*********************
    // ********************************************************************

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach
     * @param queryMeasures QueryMeasures
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getAdvanceCounts(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to get flow counts from Advance detectors; rescaled flow counts if no enough coverage

        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.getDetectorProperty().getAdvanceDetectors()!=null){ // If Advance detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getNumOfUpstreamLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getAdvanceDetectors().size();i++){
                    List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getDetectorIDs();
                    List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getNumberOfLanes();
                    if (DetectorIDs != null) {// If detector IDs exist
                        for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                            SelectedDetectorData selectedDetectorData =getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                            if(selectedDetectorData.getDataAll()!=null){
                                TotLanes=TotLanes+DetectorLanes.get(j);
                                if(queryMeasures.Median==true) {//Use median?
                                    TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                }else{// Use average?
                                    TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                }
                            }
                        }
                        if(TotLanes!=0){// Rescale the flow
                            // Consider bad detectors or incomplete detector coverage
                            TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                            TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getExclusiveRightTurnCounts(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to get right-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.getDetectorProperty().getExclusiveRightTurn()!=null){ // If exclusive right-turn detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getExclusiveRightTurn().getNumLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getExclusiveRightTurn().size();i++){
                    if(aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getMovement().equals("Right Turn")) {
                        // Only check right-turn detectors, not right-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getDetectorIDs();
                        List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getNumberOfLanes();
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                SelectedDetectorData selectedDetectorData =getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.getDataAll()!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.Median==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                    }
                                }
                            }
                            if(TotLanes!=0){ // Other detectors have data
                                TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                                TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                            }
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getExclusiveLeftTurnCounts(Connection con,AimsunApproach aimsunApproach,QueryMeasures queryMeasures){
        // This function is used to get left-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive left turn counts
            if(aimsunApproach.getDetectorProperty().getExclusiveLeftTurn()!=null){ // If exclusive left-turn detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getExclusiveLeftTurn().getNumLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().size();i++){
                    if(aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getMovement().equals("Left Turn")) {
                        // Only check left-turn detectors, not left-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getDetectorIDs();
                        List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getNumberOfLanes();
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                getDetectorData.SelectedDetectorData selectedDetectorData =
                                        getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.getDataAll()!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.Median==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                    }
                                }
                            }
                            if(TotLanes!=0){ // Other detectors have data
                                TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                                TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                            }
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }

}
