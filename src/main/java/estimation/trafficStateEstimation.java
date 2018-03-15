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

    // ***********************Parameters*******************************
    public static class Parameters{
        public VehicleParams vehicleParams;
        public IntersectionParams intersectionParams;
        public SignalSettings signalSettings;
        public TurningPrortions turningPrortions;
        public EstimationParams estimationParams;
    }

    public static class VehicleParams{
        public VehicleParams(double _VehicleLength,double _StartupLostTime,double _JamSpacing){
            this.VehicleLength=_VehicleLength;
            this.StartupLostTime=_StartupLostTime;
            this.JamSpacing=_JamSpacing;
        }
        public double VehicleLength;
        public double StartupLostTime;
        public double JamSpacing;
    }

    public static class IntersectionParams{
        // This is the profile for intersection parameters
        public IntersectionParams(double _SaturationHeadway,double _SaturationSpeedLeft,
                                  double _SaturationSpeedRight,double _SaturationSpeedThrough,
                                  double _DistanceAdvandedDetector,double _LeftTurnPocket,
                                  double _RightTurnPocket,double _DistanceToEnd){
            this.SaturationHeadway=_SaturationHeadway;
            this.SaturationSpeedLeft=_SaturationSpeedLeft;
            this.SaturationSpeedThrough=_SaturationSpeedThrough;
            this.SaturationSpeedRight=_SaturationSpeedRight;
            this.DistanceAdvandedDetector=_DistanceAdvandedDetector;
            this.LeftTurnPocket=_LeftTurnPocket;
            this.RightTurnPocket=_RightTurnPocket;
            this.DistanceToEnd=_DistanceToEnd;
        }
        public double SaturationHeadway;
        public double SaturationSpeedLeft;
        public double SaturationSpeedRight;
        public double SaturationSpeedThrough;
        public double DistanceAdvandedDetector;
        public double LeftTurnPocket;
        public double RightTurnPocket;
        public double DistanceToEnd;
    }

    public static class EstimationParams{
        // This is the profile for estimation parameters
        public EstimationParams(double _FFSpeedForAdvDet,double _OccThresholdForAdvDet){
            this.FFSpeedForAdvDet=_FFSpeedForAdvDet;
            this.OccThresholdForAdvDet=_OccThresholdForAdvDet;
        }
        public double FFSpeedForAdvDet;
        public double OccThresholdForAdvDet;
    }

    public static class SignalSettings{
        // This is the profile of signal settings
        public SignalSettings(int _CycleLength,int _LeftTurnGreen,int _ThroughGreen,int _RightTurnGreen,String _LeftTurnSetting){
            this.CycleLength=_CycleLength;
            this.LeftTurnGreen=_LeftTurnGreen;
            this.ThroughGreen=_ThroughGreen;
            this.RightTurnGreen=_RightTurnGreen;
            this.LeftTurnSetting=_LeftTurnSetting;
        }
        public int CycleLength;
        public int LeftTurnGreen;
        public int ThroughGreen;
        public int RightTurnGreen;
        public String LeftTurnSetting;
    }

    public static class TurningPrortions{
        //This is the profile for turning proportions
        public TurningPrortions(double [] _LeftTurn,double [] _LeftTurnQueue,double [] _AdvancedLeftTurn,
                                double [] _RightTurn,double [] _RightTurnQueue,double [] _AdvancedRightTurn,
                                double [] _Advanced,double [] _AllMovements,double [] _AdvancedThrough,
                                double [] _Through,double [] _AdvancedLeftAndThrough,double [] _LeftAndThrough,
                                double [] _AdvancedLeftAndRight,double [] _LeftAndRight,double [] _AdvancedThroughAndRight,
                                double [] _ThroughAndRight){
            this.LeftTurn=_LeftTurn;
            this.LeftTurnQueue=_LeftTurnQueue;
            this.AdvancedLeftTurn=_AdvancedLeftTurn;
            this.RightTurn=_RightTurn;
            this.RightTurnQueue=_RightTurnQueue;
            this.AdvancedRightTurn=_AdvancedRightTurn;
            this.Advanced=_Advanced;
            this.AllMovements=_AllMovements;
            this.AdvancedThrough=_AdvancedThrough;
            this.Through=_Through;
            this.AdvancedLeftAndThrough=_AdvancedLeftAndThrough;
            this.LeftAndThrough=_LeftAndThrough;
            this.AdvancedLeftAndRight=_AdvancedLeftAndRight;
            this.LeftAndRight=_LeftAndRight;
            this.AdvancedThroughAndRight=_AdvancedThroughAndRight;
            this.ThroughAndRight=_ThroughAndRight;
        }
        public double [] LeftTurn;
        public double [] LeftTurnQueue;
        public double [] AdvancedLeftTurn;
        public double [] RightTurn; // Exclusive right turn
        public double [] RightTurnQueue;
        public double [] AdvancedRightTurn;
        public double [] Advanced;   // Mixed through, left turn, and right turn
        public double [] AllMovements;
        public double [] AdvancedThrough; // Exclusive through
        public double [] Through;
        public double [] AdvancedLeftAndThrough; // Left turn and through only
        public double [] LeftAndThrough;
        public double [] AdvancedLeftAndRight;  // Left turn and right turn only
        public double [] LeftAndRight;
        public double [] AdvancedThroughAndRight; // Through and right turn only
        public double [] ThroughAndRight;
    }

    // ***********************Query Measures*******************************
    public static class QueryMeasures{
        // This is the settings of data query
        public QueryMeasures(int _Year,int _Month,int _Day,int _DayOfWeek,boolean _Median,int [] _TimeOfDay,int _Interval){
            this.Year=_Year;
            this.Month=_Month;
            this.Day=_Day;
            this.DayOfWeek=_DayOfWeek;
            this.Median=_Median;
            this.TimeOfDay=_TimeOfDay;
            this.Interval=_Interval;
        }
        public int Year;
        public int Month;
        public int Day;
        public int DayOfWeek;
        public boolean Median;
        public int [] TimeOfDay=null;
        public int Interval;
    }

    // ***********************Traffic States*******************************
    public static class TrafficState{
        // This is the profile for traffic state
        public TrafficState(int _Time,List<TrafficStateByDetectorGroup> _AdvancedDetectors,List<TrafficStateByDetectorGroup> _ExclusiveLeftTurnDetectors,
                            List<TrafficStateByDetectorGroup> _ExclusiveRightTurnDetectors,List<TrafficStateByDetectorGroup> _GeneralStopbarDetectors,
                            String[] _StateByMovement,double[] _QueueByMovement){
            this.Time=_Time;
            this.AdvancedDetectors=_AdvancedDetectors;
            this.ExclusiveLeftTurnDetectors=_ExclusiveLeftTurnDetectors;
            this.ExclusiveRightTurnDetectors=_ExclusiveRightTurnDetectors;
            this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
            this.StateByMovement=_StateByMovement;
            this.QueueByMovement=_QueueByMovement;
        }
        public int Time;
        public List<TrafficStateByDetectorGroup> AdvancedDetectors;
        public List<TrafficStateByDetectorGroup> ExclusiveLeftTurnDetectors;
        public List<TrafficStateByDetectorGroup> ExclusiveRightTurnDetectors;
        public List<TrafficStateByDetectorGroup> GeneralStopbarDetectors;
        public String[] StateByMovement;
        public double[] QueueByMovement;
    }

    public static class TrafficStateByDetectorGroup{
        // This is the property of traffic states by Movement type
        // e.g., exclusive left turn/right turn, general stopbar detectors, and advanced detectors
        public TrafficStateByDetectorGroup(String _DetectorType,String _Rate, double[] _Thresholds,double _AvgOcc,
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
        public String DetectorType;
        public String Rate;
        public double [] Thresholds;
        public double AvgOcc;
        public double AvgFlow;
        public double TotLanes;
        public List<Double> Occupancies;
        public List<Double> Flows;
    }

    public static class ThresholdAndRate{
        public ThresholdAndRate(String _Rate,double [] _Thresholds){
            this.Rate=_Rate;
            this.Thresholds=_Thresholds;
        }
        public String Rate;
        public double [] Thresholds;
    }

    // ***********************Detector Types*******************************
    public static class DetectorTypeByMovement{
        public DetectorTypeByMovement(String [] _Left,String [] _Through,String [] _Right){
            this.Left=_Left;
            this.Through=_Through;
            this.Right=_Right;
        }
        public String [] Left;
        public String [] Through;
        public String [] Right;
    }

    // ***********************Aggregated traffic flow values*******************************
    public static class AggregatedTrafficStates{
        // This is the profile for aggregated traffic states
        public AggregatedTrafficStates(double [] _AggregatedStatus,double [] _AvgOccupancy,double [] _AggregatedTotLanes){
            this.AggregatedStatus=_AggregatedStatus;
            this.AvgOccupancy=_AvgOccupancy;
            this.AggregatedTotLanes=_AggregatedTotLanes;
        }
        public double [] AggregatedStatus;
        public double [] AvgOccupancy;
        public double [] AggregatedTotLanes;
    }

    // ********************************************************************
    // ***********************Main functions*******************************
    // ********************************************************************
    public static void arterialTrafficStateEstimation(Connection con,AimsunNetworkByApproach aimsunNetworkByApproach,
                                                      QueryMeasures queryMeasures){
        // This function is for arterial traffic state estimation

        // First get active control plans given the query measures
        // Currently using the control plan information from the field
        // But can be overwritten by the input from the field in the future
        List<AimsunControlPlanJunction> ActiveControlPlans=getSignalData.GetActiveControlPlansForGivenDayAndTimeFromAimsun
        (aimsunNetworkByApproach.aimsunNetwork.aimsunMasterControlPlanList,
                aimsunNetworkByApproach.aimsunNetwork.aimsunControlPlanJunctionList, queryMeasures.DayOfWeek, queryMeasures.TimeOfDay[0]);

        // Run arterial estimation
        for(int i=0;i<aimsunNetworkByApproach.aimsunNetworkByApproach.size();i++) {// Loop for each approach
            // Get default parameters
            Parameters parameters=getDefaultParameters();

            System.out.println("Junction ID="+aimsunNetworkByApproach.aimsunNetworkByApproach.get(i).JunctionID+", Name="+
                    aimsunNetworkByApproach.aimsunNetworkByApproach.get(i).JunctionName+", Section ID="+
                    aimsunNetworkByApproach.aimsunNetworkByApproach.get(i).FirstSectionID);

            // *****************Update the parameter settings******************
            AimsunApproach aimsunApproach=aimsunNetworkByApproach.aimsunNetworkByApproach.get(i);
            // Update turning proportions
            parameters.turningPrortions=UpdateVehicleProportions(parameters.turningPrortions,con,aimsunApproach, queryMeasures);
            //System.out.println("Proportion Left="+parameters.turningPrortions.Advanced[0]+",Proportion Through="+
            //        parameters.turningPrortions.Advanced[1]+", Proportion Right="+parameters.turningPrortions.Advanced[2]);

            // Update signal settings and the saturation speed
            parameters=UpdateSignalSettings(parameters,ActiveControlPlans,aimsunApproach);
            //System.out.println("Cycle Length="+parameters.signalSettings.CycleLength+", Left Green Time="+
            //        parameters.signalSettings.LeftTurnGreen+", Through Green Time="+parameters.signalSettings.ThroughGreen+
            //        ", Right Green Time="+parameters.signalSettings.RightTurnGreen+", Left Turn Setting="+parameters.signalSettings.LeftTurnSetting);

            // Update saturation speeds
            parameters=UpdateSaturationSpeeds(parameters,aimsunApproach);

            // *****************Make a decision of traffic states******************
            TrafficState trafficState=MakeDecisionTrafficState(con,aimsunApproach,queryMeasures,parameters);

            trafficState=AssessmentStateAndQueue(aimsunApproach,parameters,trafficState);

            }
    }

    public static TrafficState AssessmentStateAndQueue(AimsunApproach aimsunApproach,Parameters parameters,TrafficState trafficState){

        String [] StatusAssessment=null;
        double [] QueueAssessment=null;
        if(aimsunApproach.detectorProperty.AdvancedDetectors.size()==0 && aimsunApproach.detectorProperty.ExclusiveLeftTurn.size()==0
                && aimsunApproach.detectorProperty.ExclusiveRightTurn.size()==0
                && aimsunApproach.detectorProperty.GeneralStopbarDetectors.size()==0){
            // If no detectors are available
            StatusAssessment=new String[]{"No Detector","No Detector","No Detector"};
            QueueAssessment=new double[]{-1,-1,-1};// Set the queues to be negative
        }
        else{
            // For exclusive left turns
            DetectorTypeByMovement ExclusiveLeft=new DetectorTypeByMovement(new String [] {"Left Turn","Left Turn Queue"},
                    new String []{}, new String []{});
            AggregatedTrafficStates aggregatedTrafficStatesLeft=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.detectorProperty.ExclusiveLeftTurn,ExclusiveLeft, trafficState.ExclusiveLeftTurnDetectors,
                    "Exclusive Left Turn",parameters);

            // For exclusive right turns
            DetectorTypeByMovement ExclusiveRight=new DetectorTypeByMovement(new String []{}, new String []{},
                    new String [] {"Right Turn","Right Turn Queue"});
            AggregatedTrafficStates aggregatedTrafficStatesRight=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.detectorProperty.ExclusiveRightTurn,ExclusiveRight, trafficState.ExclusiveRightTurnDetectors,
                    "Exclusive Right Turn",parameters);

            // For general detectors
            DetectorTypeByMovement GeneralDetector=new DetectorTypeByMovement(
                    new String []{"All Movements","Left and Right","Left and Through"},
                    new String []{"All Movements","Through","Left and Through","Through and Right"},
                    new String [] {"All Movements","Left and Right","Through and Right"});
            AggregatedTrafficStates aggregatedTrafficStatesGeneral=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.detectorProperty.GeneralStopbarDetectors,GeneralDetector, trafficState.GeneralStopbarDetectors,
                    "General Stopbar Detectors",parameters);

            // For advanced detectors
            DetectorTypeByMovement AdvancedDetector=new DetectorTypeByMovement(
                    new String []{"Advanced","Advanced Left Turn","Advanced Left and Through", "Advanced Left and Right"},
                    new String []{"Advanced","Advanced Through","Advanced Through and Right","Advanced Left and Through"},
                    new String []{"Advanced","Advanced Right Turn","Advanced Through and Right","Advanced Left and Right"});
            AggregatedTrafficStates aggregatedTrafficStatesAdvanced=CheckAggregatedStateForTrafficMovements(
                    aimsunApproach.detectorProperty.AdvancedDetectors,AdvancedDetector, trafficState.AdvancedDetectors,
                    "Advanced Detectors",parameters);
        }

        trafficState.StateByMovement=StatusAssessment;
        trafficState.QueueByMovement=QueueAssessment;
        return trafficState;
    }

    public static void CalculateQueueThresholds(AimsunApproach aimsunApproach,Parameters parameters){
        // This function is used to calculate the queue thresholds
        // Note: this method is more detector-driven, which means it relies heavily on the detector layouts
        //       With no detectors, we have no ideas on the queue thresholds. As a result, we have no way to
        //       assign the queues.
        // Note: The calculation of thresholds for downstream lanes can be done by using the physical geometry layout.
        //       But it is not clear how to calculate the thresholds for the upstream lanes.

        // Get number of exclusive left turn lanes and the length of the left-turn pocket
        double NumExclusiveLTLanes=aimsunApproach.geoDesign.ExclusiveLeftTurn.NumLanes;
        double LTPocket=aimsunApproach.geoDesign.ExclusiveLeftTurn.Pocket;

        // Get number of exclusive right turn lanes and the length of the right-turn pocket
        double NumExclusiveRTLanes=aimsunApproach.geoDesign.ExclusiveRightTurn.NumLanes;
        double RTPocket=aimsunApproach.geoDesign.ExclusiveRightTurn.Pocket;

        // Calculate the lane numbers for left-turn, through, and right-turn movements at general stopbar detectors
        double [] LanePortionByMovement=new double[]{0,0,0};
        double TotDownstreamLane=0;
        if(aimsunApproach.detectorProperty.GeneralStopbarDetectors.size()!=0){// If stopbar detectors exist
            for(int i=0;i<aimsunApproach.detectorProperty.GeneralStopbarDetectors.size();i++){// Loop for each detector type
                // There exist different types of detectors that belong to the category of general stopbar detectors
                String DetectorType=aimsunApproach.detectorProperty.GeneralStopbarDetectors.get(i).Movement;
                double ProportionLeft=FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=FindTrafficProportion(DetectorType, parameters,"Right");
                for(int j=0;j<aimsunApproach.detectorProperty.GeneralStopbarDetectors.get(i).NumberOfLanes.size();j++){
                    // Loop for each detector
                    double NumOfLane=aimsunApproach.detectorProperty.GeneralStopbarDetectors.get(i).NumberOfLanes.get(j);
                    TotDownstreamLane=TotDownstreamLane+NumOfLane;
                    LanePortionByMovement[0]=LanePortionByMovement[0]+NumOfLane*ProportionLeft;
                    LanePortionByMovement[1]=LanePortionByMovement[1]+NumOfLane*ProportionThrough;
                    LanePortionByMovement[2]=LanePortionByMovement[2]+NumOfLane*ProportionRight;
                }
            }
        }
        // Rescale by the number of downstream lanes just in case with incomplete detector coverage (missing or broken)
        if(TotDownstreamLane>0){
            LanePortionByMovement[0]=LanePortionByMovement[0]*((double)aimsunApproach.geoDesign.NumOfDownstreamLanes)/TotDownstreamLane;
            LanePortionByMovement[1]=LanePortionByMovement[1]*((double)aimsunApproach.geoDesign.NumOfDownstreamLanes)/TotDownstreamLane;
            LanePortionByMovement[2]=LanePortionByMovement[2]*((double)aimsunApproach.geoDesign.NumOfDownstreamLanes)/TotDownstreamLane;
        }

        // Calculate the lane numbers for left-turn, through, and right-turn movements at advanced detectors
        double [] LanePortionByMovementAdvanced=new double[]{0,0,0};
        double TotUpstreamLane=0;
        double DistanceToAdvancedDetector=0;
        if(aimsunApproach.detectorProperty.AdvancedDetectors.size()!=0){// If advanced detectors exist
            for(int i=0;i<aimsunApproach.detectorProperty.AdvancedDetectors.size();i++){// Loop for each detector type
                String DetectorType=aimsunApproach.detectorProperty.AdvancedDetectors.get(i).Movement;
                double ProportionLeft=FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=FindTrafficProportion(DetectorType, parameters,"Right");
                double DistanceToStopbar=0;
                for(int j=0;j<aimsunApproach.detectorProperty.AdvancedDetectors.get(i).NumberOfLanes.size();j++){
                    // Loop for each detector
                    double NumOfLane=aimsunApproach.detectorProperty.AdvancedDetectors.get(i).NumberOfLanes.get(j);
                    TotUpstreamLane=TotUpstreamLane+NumOfLane;
                    LanePortionByMovementAdvanced[0]=LanePortionByMovementAdvanced[0]+NumOfLane*ProportionLeft;
                    LanePortionByMovementAdvanced[1]=LanePortionByMovementAdvanced[1]+NumOfLane*ProportionThrough;
                    LanePortionByMovementAdvanced[2]=LanePortionByMovementAdvanced[2]+NumOfLane*ProportionRight;

                    if(aimsunApproach.detectorProperty.AdvancedDetectors.get(i).DistancesToStopbar.get(j)>DistanceToStopbar)
                        DistanceToStopbar=aimsunApproach.detectorProperty.AdvancedDetectors.get(i).DistancesToStopbar.get(j);
                }
                // Get the maximum distance to the advanced detectors
                if(DistanceToAdvancedDetector<DistanceToStopbar){
                    DistanceToAdvancedDetector=DistanceToStopbar;
                }
            }
        }
        // Rescale by the number of upstream lanes just in case with incomplete detector coverage (missing or broken)
        if(TotDownstreamLane>0){
            LanePortionByMovementAdvanced[0]=LanePortionByMovementAdvanced[0]*
                    ((double)aimsunApproach.geoDesign.NumOfUpstreamLanes)/TotUpstreamLane;
            LanePortionByMovementAdvanced[1]=LanePortionByMovementAdvanced[1]*
                    ((double)aimsunApproach.geoDesign.NumOfUpstreamLanes)/TotUpstreamLane;
            LanePortionByMovementAdvanced[2]=LanePortionByMovementAdvanced[2]*
                    ((double)aimsunApproach.geoDesign.NumOfUpstreamLanes)/TotUpstreamLane;
        }
        if(DistanceToAdvancedDetector==0) // No advanced detectors, use the default value
            DistanceToAdvancedDetector=parameters.intersectionParams.DistanceAdvandedDetector;

        double NumJamVehPerLane=5280/parameters.vehicleParams.JamSpacing;

        // Determine the queue threshold for left-turn, through, and right-turn movements


    }

    public static void CalculateQueueThresholdsForMovement(double NumExclusiveLane,double [] LanePortionByMovementAdvanced
            , double [] LanePortionByMovementGeneral, double NumJamVehPerLane, double TurnPocket, double DistanceToAdvancedDetector
            ,AimsunApproach aimsunApproach, String Movement){
        // This function is used to calculate queue thresholds for an individual movement

        double LinkLength=aimsunApproach.geoDesign.LinkLength;
        double NumUpstreamLanes=aimsunApproach.geoDesign.NumOfUpstreamLanes;
        double NumDownstreamLanes=aimsunApproach.geoDesign.NumOfDownstreamLanes;

        // Get the index of movements
        int Index=-1;
        if(Movement.equals("Left")){
            Index=0;
        }else if(Movement.equals("Through")){
            Index=1;
        }else if(Movement.equals("Right")){
            Index=2;
        }

        if(LanePortionByMovementAdvanced[Index]==0){
            // Have no information at advanced detectors
            if(NumExclusiveLane>0 && LanePortionByMovementGeneral[Index]==0){

            }
        }


    }

    public static AggregatedTrafficStates CheckAggregatedStateForTrafficMovements(
            List<DetectorMovementProperty> detectorMovementProperties,DetectorTypeByMovement detectorTypeByMovement,
            List<TrafficStateByDetectorGroup> trafficStateByDetectorGroups,String DetectorGroup,Parameters parameters){
        // This is the function to check aggregated state for stopbar detectors

        // Initialization of aggregated states for three different movements
        // [Left turn, Through, Right turn]
        AggregatedTrafficStates aggregatedTrafficStates=new AggregatedTrafficStates(new double[]{0,0,0},
                new double[]{0,0,0},new double[]{0,0,0});
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
            aggregatedTrafficStates=new AggregatedTrafficStates(Rates,Occs,Lanes);
        }
        return aggregatedTrafficStates;
    }

    public static double [] CheckAggregatedStateIndividualMovement(int NumType, String [] PossibleMovement
            ,List<DetectorMovementProperty> detectorMovementProperties, List<TrafficStateByDetectorGroup> trafficStateByDetectorGroups
            ,Parameters parameters, String Movement,String DetectorGroup){

        double Rate=0;
        double Occ=0;
        double TotLanes=0;
        for(int i=0;i<NumType;i++){ // Loop for each detector type
            for(int j=0;j<PossibleMovement.length;j++){ // Loop for each possible detector type
                if(PossibleMovement[j].equals(detectorMovementProperties.get(i).Movement)){
                    // Find the corresponding movement
                    if(!trafficStateByDetectorGroups.get(i).Rate.equals("Unkonwn")){
                        // Get the proportions
                        double Proportion=FindTrafficProportion(detectorMovementProperties.get(i).Movement,
                                parameters,Movement);
                        if(DetectorGroup.equals("Exclusive Left Turn")||DetectorGroup.equals("Exclusive Right Turn")
                                ||DetectorGroup.equals("General Stopbar Detectors")) {
                            Rate = Rate + RateToNumberStopbar(trafficStateByDetectorGroups.get(i).Rate) *
                                    trafficStateByDetectorGroups.get(i).TotLanes * Proportion;
                        }else if(DetectorGroup.equals("Advanced Detectors")){
                            Rate = Rate + RateToNumberAdvanced(trafficStateByDetectorGroups.get(i).Rate) *
                                    trafficStateByDetectorGroups.get(i).TotLanes * Proportion;
                        }else{
                            System.out.println("Wrong input of detector group!");
                            System.exit(-1);
                        }
                        Occ=Occ+trafficStateByDetectorGroups.get(i).AvgOcc*trafficStateByDetectorGroups.get(i).TotLanes
                                *Proportion;
                        TotLanes=TotLanes+trafficStateByDetectorGroups.get(i).TotLanes*Proportion;
                    }
                    break;
                }
            }
        }
        if(TotLanes>0){
            Rate=Rate/TotLanes;
            Occ=Occ/TotLanes;
        }
        return new double[]{Rate,Occ,TotLanes};
    }

    public static TrafficState MakeDecisionTrafficState(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures,Parameters parameters){
        // This function is used to make decision for traffic states

        // Get the end time of the query measures
        int Time=queryMeasures.TimeOfDay[1];
        // Get traffic states
        List<TrafficStateByDetectorGroup> ExclusiveLeftTurnDetectors=GetTrafficState(con,
                aimsunApproach.detectorProperty.ExclusiveLeftTurn, queryMeasures,parameters, "Exclusive Left Turn");
        List<TrafficStateByDetectorGroup> ExclusiveRightTurnDetectors=GetTrafficState(con,
                aimsunApproach.detectorProperty.ExclusiveRightTurn, queryMeasures,parameters, "Exclusive Right Turn");
        List<TrafficStateByDetectorGroup> GeneralStopbarDetectors=GetTrafficState(con,
                aimsunApproach.detectorProperty.GeneralStopbarDetectors, queryMeasures,parameters, "General Stopbar Detectors");
        List<TrafficStateByDetectorGroup> AdvancedDetectors=GetTrafficState(con,
                aimsunApproach.detectorProperty.AdvancedDetectors, queryMeasures,parameters, "Advanced Detectors");

        TrafficState trafficState=new TrafficState(Time,AdvancedDetectors,ExclusiveLeftTurnDetectors,
                ExclusiveRightTurnDetectors,GeneralStopbarDetectors,null,null);
        return trafficState;
    }

    public static List<TrafficStateByDetectorGroup> GetTrafficState(Connection con,List<DetectorMovementProperty>
            Detectors, QueryMeasures queryMeasures,Parameters parameters, String DetectorGroup){
        // This function is used to get traffic states
        List<TrafficStateByDetectorGroup> trafficStateByDetectorGroups=new ArrayList<TrafficStateByDetectorGroup>();
        if(Detectors.size()>0){
            for (int i=0;i<Detectors.size();i++){
                TrafficStateByDetectorGroup tmpDetectors=CheckDetectorGroupStatus(con,
                        Detectors.get(i),DetectorGroup,parameters,queryMeasures);
                trafficStateByDetectorGroups.add(tmpDetectors);
            }
        }
        return trafficStateByDetectorGroups;
    }

    public static TrafficStateByDetectorGroup CheckDetectorGroupStatus(Connection con,DetectorMovementProperty detectorMovementProperty,String
            DetectorGroup,Parameters parameters,QueryMeasures queryMeasures){
        // This function is used to check the detector group status

        // Get the detector type
        String DetectorType=detectorMovementProperty.Movement;

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
                OccUnit=3600.0/100.0;
            else
                OccUnit=1.0;

            Statement ps= con.createStatement();
            for (int i=0;i<detectorMovementProperty.DetectorIDs.size();i++){ // Loop for each detector
                // Get the data
                SelectedDetectorData selectedDetectorData=getDetectorData.getDataForGivenDetector(ps,
                        detectorMovementProperty.DetectorIDs.get(i), queryMeasures);
                if(selectedDetectorData.DataAll!=null){
                    if(selectedDetectorData.Health.equals("Good")){
                        // Only use good data
                        // Get the median flow and occupancy
                        Flows.add(selectedDetectorData.FlowOccMid[0]);
                        Occupancies.add(selectedDetectorData.FlowOccMid[1]/OccUnit);
                        // Do the calculation
                        TotalLanes=TotalLanes+detectorMovementProperty.NumberOfLanes.get(i);
                        AvgFlow=AvgFlow+selectedDetectorData.FlowOccMid[0]*detectorMovementProperty.NumberOfLanes.get(i);
                        AvgOcc=AvgOcc+selectedDetectorData.FlowOccMid[1]*detectorMovementProperty.NumberOfLanes.get(i)/OccUnit;
                        AvgLength=AvgLength+detectorMovementProperty.DetectorLengths.get(i)*detectorMovementProperty.NumberOfLanes.get(i);
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

        // Get the traffic state assessment
        ThresholdAndRate thresholdAndRate=new ThresholdAndRate("Unknown",new double[]{100,100});
        if(Flows.size()>0) {
            if(DetectorGroup.equals("Exclusive Left Turn")|| DetectorGroup.equals("Exclusive Right Turn") ||
                    DetectorGroup.equals("General Stopbar Detectors")){
                thresholdAndRate = GetRateAtStopbarDetector(AvgOcc, AvgLength, parameters, DetectorType);
            }else if(DetectorGroup.equals("Advanced Detectors")) {
                thresholdAndRate=GetRateAtAdvancedDetector(AvgOcc,AvgLength, parameters, DetectorType);
            }else{
                System.out.println("Wrong Detector Group!");
                System.exit(-1);
            }
        }

        // Return the traffic states
        TrafficStateByDetectorGroup trafficStateByDetectorGroup=new TrafficStateByDetectorGroup(DetectorType,thresholdAndRate.Rate,
                thresholdAndRate.Thresholds,AvgOcc, AvgFlow,TotalLanes,Occupancies,Flows);
        return trafficStateByDetectorGroup;
    }

    public static ThresholdAndRate GetRateAtAdvancedDetector(double AvgOcc,double DetLength, Parameters parameters, String DetectorType){
        // This function is used to get the rate at advanced detectors

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

        // We have three categories for advanced detectors
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
            Rate="No Queue Spillback";
        }
        else{
            Rate="Queue Spillback";
        }
        ThresholdAndRate thresholdAndRate=new ThresholdAndRate(Rate,new double[]{OccThresholdLow,OccThresholdHigh});
        return  thresholdAndRate;
    }


    // ******************Functions to get/update params and proportions******************
    public static int RateToNumberStopbar(String Rate){
        // This function is used to convert rate to number for stopbar detectors
        if(Rate.equals("No Queue Spillback"))
            return 1;
        else if(Rate.equals("Queue Spillback"))
            return 2;
        else{
            System.out.println("Unrecognized Rate For Stopbar Detector!");
            return 0;
        }
    }

    public static int RateToNumberAdvanced(String Rate){
        // This function is used to convert rate to number for advanced detectgors
        if(Rate.equals("Uncongested"))
            return 1;
        else if(Rate.equals("Congested"))
            return 2;
        else if(Rate.equals("Queue Spillback"))
            return 3;
        else{
            System.out.println("Unrecognized Rate For Stopbar Detector!");
            return 0;
        }
    }

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

    public static Double GetTurningSpeedFromApproach(AimsunApproach aimsunApproach,String Turn){
        // This function is used to get the turning speed
        double MaxSpeed=-1;
        for(int i=0;i<aimsunApproach.turningBelongToApproach.TurningProperty.size();i++){ // Loop for all turns
            if(aimsunApproach.turningBelongToApproach.TurningProperty.get(i).Movement.equals(Turn)){
                if(MaxSpeed<aimsunApproach.turningBelongToApproach.TurningProperty.get(i).TurningSpeed){
                    // Get the corrsponding maximum speed
                    MaxSpeed=aimsunApproach.turningBelongToApproach.TurningProperty.get(i).TurningSpeed;
                }
            }
        }
        return MaxSpeed;
    }

    public static Parameters getDefaultParameters(){
        // This function is used to get the default parameters

        Parameters parameters=new Parameters();
        // Get vehicle parameters
        parameters.vehicleParams=new VehicleParams(MainFunction.cBlock.VehicleLength,
                MainFunction.cBlock.StartupLostTime,MainFunction.cBlock.JamSpacing);
        // Get intersection parameters
        parameters.intersectionParams=new IntersectionParams(MainFunction.cBlock.SaturationHeadway,
                MainFunction.cBlock.SaturationSpeedLeft,MainFunction.cBlock.SaturationSpeedRight,
                MainFunction.cBlock.SaturationSpeedThrough,MainFunction.cBlock.DistanceAdvandedDetector,
                MainFunction.cBlock.LeftTurnPocket,MainFunction.cBlock.RightTurnPocket,
                MainFunction.cBlock.DistanceToEnd);
        // Get estimation parameters
        parameters.estimationParams=new EstimationParams(MainFunction.cBlock.FFSpeedForAdvDet,
                MainFunction.cBlock.OccThresholdForAdvDet);
        // Get turning proportions
        parameters.turningPrortions=new TurningPrortions(
                MainFunction.cBlock.LeftTurn,MainFunction.cBlock.LeftTurnQueue,MainFunction.cBlock.AdvancedLeftTurn,
                MainFunction.cBlock.RightTurn,MainFunction.cBlock.RightTurnQueue,MainFunction.cBlock.AdvancedRightTurn,
                MainFunction.cBlock.Advanced,MainFunction.cBlock.AllMovements,MainFunction.cBlock.AdvancedThrough,
                MainFunction.cBlock.Through,MainFunction.cBlock.AdvancedLeftAndThrough,MainFunction.cBlock.LeftAndThrough,
                MainFunction.cBlock.AdvancedLeftAndRight,MainFunction.cBlock.LeftAndRight,MainFunction.cBlock.AdvancedThroughAndRight,
                MainFunction.cBlock.ThroughAndRight);
        // Get signal settings
        parameters.signalSettings=new SignalSettings(MainFunction.cBlock.CycleLength,
                MainFunction.cBlock.LeftTurnGreen,MainFunction.cBlock.ThroughGreen,
                MainFunction.cBlock.RightTurnGreen,MainFunction.cBlock.LeftTurnSetting);
        return parameters;
    }

    public static TurningPrortions UpdateVehicleProportions(TurningPrortions turningPrortions,Connection con,
                                                            AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to update vehicle's proportions given the queryMeasures
        // Update these values with field data
        double ProportionLeft=0;
        double ProportionThrough=0;
        double ProportionRight=0;

        double [] LeftTurnCountsAndLanes=getExclusiveLeftTurnCounts(con,aimsunApproach,queryMeasures);
        double [] RightTurnCountsAndLanes=getExclusiveRightTurnCounts(con,aimsunApproach,queryMeasures);
        double [] AdvancedCountsAndLanes=getAdvancedCounts(con,aimsunApproach,queryMeasures);

        if(AdvancedCountsAndLanes[0]>0 && AdvancedCountsAndLanes[1]>0){
            // Have values from advanced detectors
            if(LeftTurnCountsAndLanes[1]>0){
                // Have left turn values
                ProportionLeft=LeftTurnCountsAndLanes[0]/AdvancedCountsAndLanes[0];
                if(RightTurnCountsAndLanes[1]>0){
                    //Have both left turn and right-turn values
                    ProportionRight=RightTurnCountsAndLanes[0]/AdvancedCountsAndLanes[0];
                    ProportionThrough=Math.max(0,1-ProportionLeft-ProportionRight); // Just make sure no negative values

                    // Update for all movements
                    turningPrortions.Advanced=new double[]{ProportionLeft,ProportionThrough,ProportionRight};
                    turningPrortions.AllMovements=turningPrortions.Advanced;
                    // Update for through and right-turns
                    turningPrortions.AdvancedThroughAndRight=new double [] {0,
                            ProportionThrough/(ProportionThrough+ProportionRight),ProportionRight/(ProportionThrough+ProportionRight)};
                    turningPrortions.ThroughAndRight=turningPrortions.AdvancedThroughAndRight;
                    // Update for left-turn and through
                    turningPrortions.AdvancedLeftAndThrough=new double []{ProportionLeft/(ProportionThrough+ProportionLeft),
                            ProportionThrough/(ProportionThrough+ProportionLeft),0};
                    turningPrortions.LeftAndThrough=turningPrortions.AdvancedLeftAndThrough;
                    // Update for left-turn and right-turn
                    turningPrortions.AdvancedLeftAndRight=new double []{ProportionLeft/(ProportionLeft+ProportionRight),0,
                            ProportionRight/(ProportionLeft+ProportionRight)};
                    turningPrortions.LeftAndRight=turningPrortions.AdvancedLeftAndRight;
                }else{
                    // Only have left-turn values
                    // Can only update for all movements with limited information: rescale
                    ProportionThrough=(1-ProportionLeft)*
                            turningPrortions.Advanced[1]/(turningPrortions.Advanced[1]+turningPrortions.Advanced[2]);
                    ProportionRight=1-ProportionLeft-ProportionThrough;
                    turningPrortions.Advanced=new double[]{ProportionLeft,ProportionThrough,ProportionRight};
                    turningPrortions.AllMovements=turningPrortions.Advanced;
                }
            }else{
                // No left-turn values
                ProportionRight=RightTurnCountsAndLanes[0]/AdvancedCountsAndLanes[0];
                if(RightTurnCountsAndLanes[1]>0){
                    //Have only right-turn values
                    // Can only update for all movements with limited information: rescale
                    ProportionThrough=(1-ProportionRight)*
                            turningPrortions.Advanced[1]/(turningPrortions.Advanced[0]+turningPrortions.Advanced[1]);
                    ProportionLeft=1-ProportionRight-ProportionThrough;
                    turningPrortions.Advanced=new double[]{ProportionLeft,ProportionThrough,ProportionRight};
                    turningPrortions.AllMovements=turningPrortions.Advanced;
                }
            }



        }
        return turningPrortions;
    }

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
                parameters.intersectionParams.SaturationSpeedLeft=parameters.intersectionParams.SaturationSpeedLeft*0.35;
            else if(tmpSignalSettings.LeftTurnSetting.equals("Protected-Permitted"))
                parameters.intersectionParams.SaturationSpeedLeft=parameters.intersectionParams.SaturationSpeedLeft*0.7;
        }else {
            parameters.signalSettings.LeftTurnSetting="N/A";
        }
        return parameters;
    }

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
        else if(DetectorType.equals("Advanced")){
            Proportion=parameters.turningPrortions.Advanced[Index];
        }
        else if(DetectorType.equals("Advanced Left Turn")){
            Proportion=parameters.turningPrortions.AdvancedLeftTurn[Index];
        }
        else if(DetectorType.equals("Advanced Right Turn")){
            Proportion=parameters.turningPrortions.AdvancedRightTurn[Index];
        }
        else if(DetectorType.equals("Advanced Through")){
            Proportion=parameters.turningPrortions.AdvancedThrough[Index];
        }
        else if(DetectorType.equals("Advanced Through and Right")){
            Proportion=parameters.turningPrortions.AdvancedThroughAndRight[Index];
        }
        else if(DetectorType.equals("Advanced Left and Through")){
            Proportion=parameters.turningPrortions.AdvancedLeftAndThrough[Index];
        }
        else if(DetectorType.equals("Advanced Left and Right")){
            Proportion=parameters.turningPrortions.AdvancedLeftAndRight[Index];
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

    // ******************Functions to get data*********************
    public static double [] getAdvancedCounts(Connection con,AimsunApproach aimsunApproach,
                                              QueryMeasures queryMeasures){
        // This function is used to get flow counts from advanced detectors
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.detectorProperty.AdvancedDetectors!=null){ // If advanced detectors exit
                for(int i=0;i<aimsunApproach.detectorProperty.AdvancedDetectors.size();i++){
                    List<Integer> DetectorIDs = aimsunApproach.detectorProperty.AdvancedDetectors.get(i).DetectorIDs;
                    List<Integer> DetectorLanes=aimsunApproach.detectorProperty.AdvancedDetectors.get(i).NumberOfLanes;
                    if (DetectorIDs != null) {// If detector IDs exist
                        for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                            getDetectorData.SelectedDetectorData selectedDetectorData =
                                    getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                            if(selectedDetectorData.DataAll!=null){
                                TotLanes=TotLanes+DetectorLanes.get(j);
                                if(queryMeasures.Median==true) {//Use median?
                                    TotCounts = TotCounts + selectedDetectorData.FlowOccMid[0];
                                }else{// Use average?
                                    TotCounts = TotCounts + selectedDetectorData.FlowOCCAvg[0];
                                }
                            }else{ // If is empty
                                if(TotLanes!=0){ // Other detectors have data
                                    TotCounts=TotCounts* // Rescale the flow
                                            ((double) (TotLanes+DetectorLanes.get(j))/TotLanes);
                                    TotLanes=TotLanes+DetectorLanes.get(j); // Add more lanes
                                }
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

    public static double [] getExclusiveRightTurnCounts(Connection con,AimsunApproach aimsunApproach,
                                                        QueryMeasures queryMeasures){
        // This function is used to get right-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.detectorProperty.ExclusiveRightTurn!=null){ // If exclusive right-turn detectors exit
                for(int i=0;i<aimsunApproach.detectorProperty.ExclusiveRightTurn.size();i++){
                    if(aimsunApproach.detectorProperty.ExclusiveRightTurn.get(i).Movement.equals("Right Turn")) {
                        // Only check right-turn detectors, not right-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.detectorProperty.ExclusiveRightTurn.get(i).DetectorIDs;
                        List<Integer> DetectorLanes=aimsunApproach.detectorProperty.ExclusiveRightTurn.get(i).NumberOfLanes;
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                getDetectorData.SelectedDetectorData selectedDetectorData =
                                        getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.DataAll!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.Median==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.FlowOccMid[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.FlowOCCAvg[0];
                                    }
                                }else{ // If is empty
                                    if(TotLanes!=0){ // Other detectors have data
                                        TotCounts=TotCounts* // Rescale the flow
                                                ((double) (TotLanes+DetectorLanes.get(j))/TotLanes);
                                        TotLanes=TotLanes+DetectorLanes.get(j); // Add more lanes
                                    }
                                }
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

    public static double [] getExclusiveLeftTurnCounts(Connection con,AimsunApproach aimsunApproach,
                                                       QueryMeasures queryMeasures){
        // This function is used to get left-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive left turn counts
            if(aimsunApproach.detectorProperty.ExclusiveLeftTurn!=null){ // If exclusive left-turn detectors exit
                for(int i=0;i<aimsunApproach.detectorProperty.ExclusiveLeftTurn.size();i++){
                    if(aimsunApproach.detectorProperty.ExclusiveLeftTurn.get(i).Movement.equals("Left Turn")) {
                        // Only check left-turn detectors, not left-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.detectorProperty.ExclusiveLeftTurn.get(i).DetectorIDs;
                        List<Integer> DetectorLanes=aimsunApproach.detectorProperty.ExclusiveLeftTurn.get(i).NumberOfLanes;
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                getDetectorData.SelectedDetectorData selectedDetectorData =
                                        getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.DataAll!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.Median==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.FlowOccMid[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.FlowOCCAvg[0];
                                    }
                                }else{ // If is empty
                                    if(TotLanes!=0){ // Other detectors have data
                                        TotCounts=TotCounts* // Rescale the flow
                                                ((double) (TotLanes+DetectorLanes.get(j))/TotLanes);
                                        TotLanes=TotLanes+DetectorLanes.get(j); // Add more lanes
                                    }
                                }
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
