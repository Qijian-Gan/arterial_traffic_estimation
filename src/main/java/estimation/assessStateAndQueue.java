package estimation;

import commonClass.forEstimation.*;
import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.parameters.Parameters;
import util.util;

public class assessStateAndQueue {
    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters
     * @param trafficState TrafficStateByApproach class
     * @return TrafficStateByApproach class
     */
    public static TrafficStateByApproach Assessment(AimsunApproach aimsunApproach, Parameters parameters
            , TrafficStateByApproach trafficState){
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
            AggregatedTrafficStates aggregatedTrafficStatesLeft=aggregateTrafficStates.CheckAllMovements(
                    aimsunApproach.getDetectorProperty().getExclusiveLeftTurn(),ExclusiveLeft, trafficState.getExclusiveLeftTurnDetectors(),
                    "Exclusive Left Turn",parameters);

            // For exclusive right turns
            DetectorTypeByMovement ExclusiveRight=new DetectorTypeByMovement(new String []{}, new String []{},
                    new String [] {"Right Turn","Right Turn Queue"}); // Exclusive right turn
            AggregatedTrafficStates aggregatedTrafficStatesRight=aggregateTrafficStates.CheckAllMovements(
                    aimsunApproach.getDetectorProperty().getExclusiveRightTurn(),ExclusiveRight, trafficState.getExclusiveRightTurnDetectors(),
                    "Exclusive Right Turn",parameters);

            // For general detectors
            DetectorTypeByMovement GeneralDetector=new DetectorTypeByMovement(
                    new String []{"All Movements","Left and Right","Left and Through"}, // Left turn
                    new String []{"All Movements","Through","Left and Through","Through and Right"}, //Through
                    new String []{"All Movements","Left and Right","Through and Right"}); // Right turn
            AggregatedTrafficStates aggregatedTrafficStatesGeneral=aggregateTrafficStates.CheckAllMovements(
                    aimsunApproach.getDetectorProperty().getGeneralStopbarDetectors(),GeneralDetector, trafficState.getGeneralStopbarDetectors(),
                    "General Stopbar Detectors",parameters);

            // For Advance detectors
            DetectorTypeByMovement AdvanceDetector=new DetectorTypeByMovement(
                    new String []{"Advance","Advance Left Turn","Advance Left and Through", "Advance Left and Right"},// Left turn
                    new String []{"Advance","Advance Through","Advance Through and Right","Advance Left and Through"}, // Through
                    new String []{"Advance","Advance Right Turn","Advance Through and Right","Advance Left and Right"}); // Right turn
            AggregatedTrafficStates aggregatedTrafficStatesAdvance=aggregateTrafficStates.CheckAllMovements(
                    aimsunApproach.getDetectorProperty().getAdvanceDetectors(),AdvanceDetector, trafficState.getAdvanceDetectors(),
                    "Advance Detectors",parameters);

            // Get the queue thresholds for different traffic movements
            queueThreshold=calculateQueueThresholds.ForApproach(aimsunApproach,parameters);

            // Get the assessment of states and queues
            AssessmentStateAndQueue assessmentStateAndQueue=MakeADecision(aggregatedTrafficStatesAdvance,aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                    , aggregatedTrafficStatesRight,aimsunApproach.getGeoDesign().getTurnIndicator(),queueThreshold);
            StatusAssessment=assessmentStateAndQueue.getStatusAssessment();
            QueueAssessment=assessmentStateAndQueue.getQueueAssessment();
        }
        trafficState.setQueueThreshold(queueThreshold);
        trafficState.setStateByMovement(StatusAssessment);
        trafficState.setQueueByMovement(QueueAssessment);
        return trafficState;
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
        double [] AdvanceRate=aggregatedTrafficStatesAdvance.getAggregatedStatus();
        double [] GeneralRate=aggregatedTrafficStatesGeneral.getAggregatedStatus();
        double [] ExclusiveLeftRate=aggregatedTrafficStatesLeft.getAggregatedStatus();
        double [] ExclusiveRightRate=aggregatedTrafficStatesRight.getAggregatedStatus();

        double DownstreamStatusLeft=util.MeanWithoutZero(new double[]{ExclusiveLeftRate[0],GeneralRate[0]});
        double AdvanceStatusLeft=AdvanceRate[0];
        double [] DownstreamOccAndThresholdLeft=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral,aggregatedTrafficStatesLeft
                , aggregatedTrafficStatesRight,"Left");
        double [] UpstreamOccAndThresholdLeft;
        if(aggregatedTrafficStatesAdvance.getAggregatedStatus()[0]>0){
            UpstreamOccAndThresholdLeft=new double[]{aggregatedTrafficStatesAdvance.getAvgOccupancy()[0],
                    aggregatedTrafficStatesAdvance.getThresholdLow()[0], aggregatedTrafficStatesAdvance.getThresholdHigh()[0]};
        }else{
            UpstreamOccAndThresholdLeft=new double[]{0,0,0};
        }

        double DownstreamStatusThrough=GeneralRate[1];
        double AdvanceStatusThrough=AdvanceRate[1];
        double [] DownstreamOccAndThresholdThrough=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral
                ,aggregatedTrafficStatesLeft, aggregatedTrafficStatesRight,"Through");
        double [] UpstreamOccAndThresholdThrough;
        if(aggregatedTrafficStatesAdvance.getAggregatedStatus()[1]>0){
            UpstreamOccAndThresholdThrough=new double[]{aggregatedTrafficStatesAdvance.getAvgOccupancy()[1]
                    ,aggregatedTrafficStatesAdvance.getThresholdLow()[1],aggregatedTrafficStatesAdvance.getThresholdHigh()[1]};
        }else{
            UpstreamOccAndThresholdThrough=new double[]{0,0,0};
        }

        double DownstreamstatusRight=util.MeanWithoutZero(new double[]{ExclusiveRightRate[2],GeneralRate[2]});
        double AdvanceStatusRight=AdvanceRate[2];
        double [] DownstreamOccAndThresholdRight=GetDownstreamAvgOccAndThresholdByMovement(aggregatedTrafficStatesGeneral
                ,aggregatedTrafficStatesLeft, aggregatedTrafficStatesRight,"Right");
        double [] UpstreamOccAndThresholdRight;
        if(aggregatedTrafficStatesAdvance.getAggregatedStatus()[2]>0){
            UpstreamOccAndThresholdRight=new double[]{aggregatedTrafficStatesAdvance.getAvgOccupancy()[2],
                    aggregatedTrafficStatesAdvance.getThresholdLow()[2], aggregatedTrafficStatesAdvance.getThresholdHigh()[2]};
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

        StatusAssessment=new String[]{assessmentStateAndQueueLeft.getStatusAssessment(),assessmentStateAndQueueThrough.getStatusAssessment(),
                assessmentStateAndQueueRight.getStatusAssessment()};
        QueueAssessment=new int[]{assessmentStateAndQueueLeft.getQueueAssessment(),assessmentStateAndQueueThrough.getQueueAssessment(),
                assessmentStateAndQueueRight.getQueueAssessment()};

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
            QueueToAdvance=queueThreshold.getQueueThresholdLeft().getQueueToAdvance();
            QueueWithMaxGreen=queueThreshold.getQueueThresholdLeft().getQueueWithMaxGreen();
            QueueToEnd=queueThreshold.getQueueThresholdLeft().getQueueToEnd();
        }else if(Movement.equals("Through")){
            Blockage[1]=0; // Set it zero, only check other movements
            QueueToAdvance=queueThreshold.getQueueThresholdThrough().getQueueToAdvance();
            QueueWithMaxGreen=queueThreshold.getQueueThresholdThrough().getQueueWithMaxGreen();
            QueueToEnd=queueThreshold.getQueueThresholdThrough().getQueueToEnd();
        }else if(Movement.equals("Right")){
            Blockage[2]=0; // Set it zero, only check other movements
            QueueToAdvance=queueThreshold.getQueueThresholdRight().getQueueToAdvance();
            QueueWithMaxGreen=queueThreshold.getQueueThresholdRight().getQueueWithMaxGreen();
            QueueToEnd=queueThreshold.getQueueThresholdRight().getQueueToEnd();
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
        if(aggregatedTrafficStatesLeft.getAggregatedStatus()[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesLeft.getAvgOccupancy()[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesLeft.getAggregatedTotLanes()[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesLeft.getThresholdLow()[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesLeft.getThresholdHigh()[Index];
        }

        if(aggregatedTrafficStatesGeneral.getAggregatedStatus()[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesGeneral.getAvgOccupancy()[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesGeneral.getAggregatedTotLanes()[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesGeneral.getThresholdLow()[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesGeneral.getThresholdHigh()[Index];
        }

        if(aggregatedTrafficStatesRight.getAggregatedStatus()[Index]>0){
            AvgOcc=AvgOcc+aggregatedTrafficStatesRight.getAvgOccupancy()[Index];
            TotLanes=TotLanes+aggregatedTrafficStatesRight.getAggregatedTotLanes()[Index];
            ThresholdLow=ThresholdLow+aggregatedTrafficStatesRight.getThresholdLow()[Index];
            ThresholdHigh=ThresholdHigh+aggregatedTrafficStatesRight.getThresholdHigh()[Index];
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


}
