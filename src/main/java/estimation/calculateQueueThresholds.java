package estimation;

import commonClass.forEstimation.QueueThreshold;
import commonClass.forEstimation.QueueThresholdByMovement;
import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.parameters.Parameters;

public class calculateQueueThresholds {

    /**
     *
     * @param aimsunApproach AimsunApproach
     * @param parameters Parameters
     * @return QueueThreshold
     */
    public static QueueThreshold ForApproach(AimsunApproach aimsunApproach, Parameters parameters){
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
                double ProportionLeft=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Right");
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
                double ProportionLeft=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Left");
                double ProportionThrough=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Through");
                double ProportionRight=parameterOperations.FindTrafficProportion(DetectorType, parameters,"Right");
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
            DistanceToAdvanceDetector = parameters.getIntersectionParams().getDistanceAdvanceDetector();
        }

        // Determine the queue threshold for left-turn, through, and right-turn movements
        double NumJamVehPerLane=5280/parameters.getVehicleParams().getJamSpacing();
        QueueThresholdByMovement queueThresholdLeft=ForMovement(NumExclusiveLTLanes,LanePortionByMovementAdvance
                , LanePortionByMovement, NumJamVehPerLane, LTPocket, DistanceToAdvanceDetector,aimsunApproach, parameters,"Left");
        QueueThresholdByMovement queueThresholdRight=ForMovement(NumExclusiveRTLanes,LanePortionByMovementAdvance
                , LanePortionByMovement, NumJamVehPerLane, RTPocket, DistanceToAdvanceDetector,aimsunApproach, parameters, "Right");
        QueueThresholdByMovement queueThresholdThrough=ForMovement(0,LanePortionByMovementAdvance
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
    public static QueueThresholdByMovement ForMovement(double NumExclusiveLane, double [] LanePortionByMovementAdvance
            , double [] LanePortionByMovementGeneral, double NumJamVehPerLane, double TurnPocket, double DistanceToAdvanceDetector
            , AimsunApproach aimsunApproach, Parameters parameters, String Movement){
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
            GreenTime=parameters.getSignalSettings().getLeftTurnGreen();
        }else if(Movement.equals("Through")){
            Index=1;
            GreenTime=parameters.getSignalSettings().getThroughGreen();
        }else if(Movement.equals("Right")){
            Index=2;
            GreenTime=parameters.getSignalSettings().getRightTurnGreen();
        }else{
            System.out.println("Wrong input of movement type!");
            System.exit(-1);
        }
        double VehicleHeadway=parameters.getIntersectionParams().getSaturationHeadway();
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

}
