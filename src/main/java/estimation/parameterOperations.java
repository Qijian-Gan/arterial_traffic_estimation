package estimation;

import commonClass.forAimsunNetwork.signalControl.AimsunControlPlanJunction;
import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.parameters.*;
import commonClass.query.QueryMeasures;
import dataProvider.getSignalData;
import main.MainFunction;

import java.sql.Connection;
import java.util.List;

public class parameterOperations {

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
        TurningProportion turningPrortions=new TurningProportion(
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
     * @param DetectorType Detector type
     * @param parameters Parameters
     * @param Movement Movement (String)
     * @return Proportion (double)
     */
    public static double FindTrafficProportion(String DetectorType, Parameters parameters, String Movement){
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
            Proportion=parameters.getTurningProportion().getLeftTurn()[Index];
        }else if(DetectorType.equals("Left Turn Queue")){
            Proportion=parameters.getTurningProportion().getLeftTurnQueue()[Index];
        }
        else if(DetectorType.equals("Right Turn")){
            Proportion=parameters.getTurningProportion().getRightTurn()[Index];
        }
        else if(DetectorType.equals("Right Turn Queue")){
            Proportion=parameters.getTurningProportion().getRightTurnQueue()[Index];
        }
        else if(DetectorType.equals("Advance")){
            Proportion=parameters.getTurningProportion().getAdvance()[Index];
        }
        else if(DetectorType.equals("Advance Left Turn")){
            Proportion=parameters.getTurningProportion().getAdvanceLeftTurn()[Index];
        }
        else if(DetectorType.equals("Advance Right Turn")){
            Proportion=parameters.getTurningProportion().getAdvanceRightTurn()[Index];
        }
        else if(DetectorType.equals("Advance Through")){
            Proportion=parameters.getTurningProportion().getAdvanceThrough()[Index];
        }
        else if(DetectorType.equals("Advance Through and Right")){
            Proportion=parameters.getTurningProportion().getAdvanceThroughAndRight()[Index];
        }
        else if(DetectorType.equals("Advance Left and Through")){
            Proportion=parameters.getTurningProportion().getAdvanceLeftAndThrough()[Index];
        }
        else if(DetectorType.equals("Advance Left and Right")){
            Proportion=parameters.getTurningProportion().getAdvanceLeftAndRight()[Index];
        }
        else if(DetectorType.equals("All Movements")){
            Proportion=parameters.getTurningProportion().getAllMovements()[Index];
        }
        else if(DetectorType.equals("Through")){
            Proportion=parameters.getTurningProportion().getThrough()[Index];
        }
        else if(DetectorType.equals("Left and Right")){
            Proportion=parameters.getTurningProportion().getLeftAndRight()[Index];
        }
        else if(DetectorType.equals("Left and Through")){
            Proportion=parameters.getTurningProportion().getLeftAndThrough()[Index];
        }
        else if(DetectorType.equals("Through and Right")){
            Proportion=parameters.getTurningProportion().getThroughAndRight()[Index];
        }else
        {
            System.out.println("Wrong input of detector movement!");
            System.exit(-1);
        }
        return Proportion;
    }

    /**
     *
     * @param turningPrortions TurningPrortions class
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return TurningPrortions class
     */
    public static TurningProportion UpdateVehicleProportions(TurningProportion turningPrortions,Connection con,
                                                             AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to update vehicle's proportions given the queryMeasures

        // Update these values with field data
        double ProportionLeft=0;
        double ProportionThrough=0;
        double ProportionRight=0;

        // Get the counts and lanes [count, lane]
        double [] LeftTurnCountsAndLanes=getData.getExclusiveLeftTurnCounts(con,aimsunApproach,queryMeasures); //[total count, total lane]
        double [] RightTurnCountsAndLanes=getData.getExclusiveRightTurnCounts(con,aimsunApproach,queryMeasures);
        double [] AdvanceCountsAndLanes=getData.getAdvanceCounts(con,aimsunApproach,queryMeasures);

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
                            turningPrortions.getAdvance()[1]/(turningPrortions.getAdvance()[1]+turningPrortions.getAdvance()[2]);
                    ProportionRight=1-ProportionLeft-ProportionThrough;
                }
            }else{
                // No left-turn values
                if(RightTurnCountsAndLanes[1]>0){
                    //Have only right-turn values
                    ProportionRight=RightTurnCountsAndLanes[0]/AdvanceCountsAndLanes[0];
                    ProportionThrough=(1-ProportionRight)*
                            turningPrortions.getAdvance()[1]/(turningPrortions.getAdvance()[0]+turningPrortions.getAdvance()[1]);
                    ProportionLeft=1-ProportionRight-ProportionThrough;
                }
            }

            // Update the proportions at each type of detectors
            if(ProportionLeft+ProportionThrough+ProportionRight>0) {
                // Update for all movements
                turningPrortions.setAdvance(new double[]{ProportionLeft, ProportionThrough, ProportionRight});
                turningPrortions.setAllMovements(turningPrortions.getAdvance());
                // Update for through and right-turns
                turningPrortions.setAdvanceThroughAndRight(new double[]{0,
                        ProportionThrough / (ProportionThrough + ProportionRight), ProportionRight / (ProportionThrough + ProportionRight)});
                turningPrortions.setThroughAndRight(turningPrortions.getAdvanceThroughAndRight());
                // Update for left-turn and through
                turningPrortions.setAdvanceLeftAndThrough(new double[]{ProportionLeft / (ProportionThrough + ProportionLeft),
                        ProportionThrough / (ProportionThrough + ProportionLeft), 0});
                turningPrortions.setLeftAndThrough(turningPrortions.getAdvanceLeftAndThrough());
                // Update for left-turn and right-turn
                turningPrortions.setAdvanceLeftAndRight(new double[]{ProportionLeft / (ProportionLeft + ProportionRight), 0,
                        ProportionRight / (ProportionLeft + ProportionRight)});
                turningPrortions.setLeftAndRight(turningPrortions.getAdvanceLeftAndRight());
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
    public static TurningProportion UpdateVehicleProportionsAccordingToLandIndicator(TurningProportion turningPrortions,int [] LaneIndicator){
        // This function is used to update vehicle proportions according to lane
        // indicators: only for lanes/detectors with all movements

        double TPLeft=turningPrortions.getAdvance()[0];
        double TPThrough=turningPrortions.getAdvance()[1];
        double TPRight=turningPrortions.getAdvance()[2];
        if(LaneIndicator[0]==0 && LaneIndicator[1]==1 && LaneIndicator[2]==1 && (TPThrough+TPRight)>0){// No left turn
            turningPrortions.setAdvance(new double[]{0,TPThrough+TPLeft*TPThrough/(TPThrough+TPRight)
                    ,TPRight+TPLeft*TPRight/(TPThrough+TPRight)});
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==0 &&LaneIndicator[2]==1 && (TPLeft+TPRight)>0){// No through
            turningPrortions.setAdvance(new double[]{TPLeft+TPThrough*TPLeft/(TPLeft+TPRight),0,
                    TPRight+TPThrough*TPRight/(TPLeft+TPRight)});
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==1 &&LaneIndicator[2]==0 &&(TPLeft+TPThrough)>0){// No right turn
            turningPrortions.setAdvance(new double[]{TPLeft+TPRight*TPLeft/(TPLeft+TPThrough),
                    TPThrough+TPRight*TPThrough/(TPLeft+TPThrough),0});
        }

        if(LaneIndicator[0]==0 && LaneIndicator[1]==0 &&LaneIndicator[2]==1){ // No left turn and through
            turningPrortions.setAdvance(new double[]{0,0,1});
        }
        if(LaneIndicator[0]==0 && LaneIndicator[1]==1 &&LaneIndicator[2]==0){ // No left turn and right turn
            turningPrortions.setAdvance(new double[]{0,1,0});
        }
        if(LaneIndicator[0]==1 && LaneIndicator[1]==0 &&LaneIndicator[2]==0){ // No through and right turn
            turningPrortions.setAdvance(new double[]{1,0,0});
        }

        turningPrortions.setAllMovements(turningPrortions.getAdvance());
        return turningPrortions;
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
            parameters.getIntersectionParams().setSaturationSpeedLeft(LeftTurnSpeed);
        }
        Double ThroughSpeed=GetTurningSpeedFromApproach(aimsunApproach,"Through");
        if(ThroughSpeed>0){
            parameters.getIntersectionParams().setSaturationSpeedThrough(ThroughSpeed);
        }
        Double RightTurnSpeed=GetTurningSpeedFromApproach(aimsunApproach,"Right Turn");
        if(RightTurnSpeed>0){
            parameters.getIntersectionParams().setSaturationSpeedRight(RightTurnSpeed);
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
            parameters.setSignalSettings(tmpSignalSettings);
            // Reduce the saturation speed for permitted left turns
            if(tmpSignalSettings.getLeftTurnSetting().equals("Permitted"))
                // Reduced to 1/3
                parameters.getIntersectionParams().setSaturationSpeedLeft(parameters.getIntersectionParams().getSaturationSpeedLeft()*0.35);
            else if(tmpSignalSettings.getLeftTurnSetting().equals("Protected-Permitted"))
                // Reduced to 2/3
                parameters.getIntersectionParams().setSaturationSpeedLeft(parameters.getIntersectionParams().getSaturationSpeedLeft()*0.7);
        }else {
            parameters.getSignalSettings().setLeftTurnSetting("N/A");
        }
        return parameters;
    }
}
