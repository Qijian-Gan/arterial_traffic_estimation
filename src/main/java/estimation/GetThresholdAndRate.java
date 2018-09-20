package estimation;

import commonClass.forEstimation.ThresholdAndRate;
import commonClass.parameters.Parameters;

public class getThresholdAndRate {
    /**
     *
     * @param AvgOcc Average occupancy
     * @param DetLength Detector length
     * @param parameters Parameters
     * @param DetectorType Detector type
     * @return ThresholdAndRate
     */
    public static ThresholdAndRate AtAdvanceDetector(double AvgOcc, double DetLength, Parameters parameters, String DetectorType){
        // This function is used to get the rate at Advance detectors

        // Get the maximum green ratio given the detector group
        double GreenTime=Math.max(parameters.getSignalSettings().getLeftTurnGreen(),
                Math.max(parameters.getSignalSettings().getThroughGreen(),parameters.getSignalSettings().getRightTurnGreen()));

        // Get the start-up lost time, and saturation headway
        double StartUpLostTime=parameters.getVehicleParams().getStartupLostTime();
        double SaturationHeadway=parameters.getIntersectionParams().getSaturationHeadway();

        // Get the saturation speeds for different movements: Use the speed for through movements
        double SaturationSpeedThrough=parameters.getIntersectionParams().getSaturationSpeedThrough();
        double SaturationSpeedLeft=SaturationSpeedThrough;
        double SaturationSpeedRight=SaturationSpeedThrough;

        // Get the vehicle length
        double VehLength= parameters.getVehicleParams().getVehicleLength();

        // Time for a vehicle passing a detector for different movements (in seconds)
        double PassTimeThrough=(VehLength+DetLength)*3600/SaturationSpeedThrough/5280;
        double PassTimeLeft=(VehLength+DetLength)*3600/SaturationSpeedLeft/5280;
        double PassTimeRight=(VehLength+DetLength)*3600/SaturationSpeedRight/5280;

        // Get the proportions of vehicles for left-turn, through and right-turn
        double ProportionLeft= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Left");
        double ProportionThrough= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Through");
        double ProportionRight= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Right");

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
        double OccThresholdLow=100*Math.min(1.0,DischargingTime/parameters.getSignalSettings().getCycleLength());
        double OccThresholdHigh=100*Math.min(1.0, DischargingTime/parameters.getSignalSettings().getCycleLength()+
                (1-GreenTime/parameters.getSignalSettings().getCycleLength()));

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
    public static ThresholdAndRate AtStopbarDetector(double AvgOcc,double DetLength, Parameters parameters, String DetectorType){
        // This function is used to get the rate at stopbar detectors by different movements
        // The flow count at stopbar detectors are not reliable, and therefore, we only use the occupancy data

        // Get the green ratio given the detector group
        double GreenTime=0;
        if(DetectorType.equals("Left Turn") || DetectorType.equals("Left Turn Queue")) {
            GreenTime = parameters.getSignalSettings().getLeftTurnGreen();
        }
        else if(DetectorType.equals("Right Turn")|| DetectorType.equals("Right Turn Queue")){
            GreenTime=parameters.getSignalSettings().getRightTurnGreen();
        }
        else
        {
            GreenTime=parameters.getSignalSettings().getThroughGreen();
        }

        // Get the start-up lost time, and saturation headway
        double StartUpLostTime=parameters.getVehicleParams().getStartupLostTime();
        double SaturationHeadway=parameters.getIntersectionParams().getSaturationHeadway();

        // Get the saturation speeds for different movements
        double SaturationSpeedLeft=parameters.getIntersectionParams().getSaturationSpeedLeft();
        double SaturationSpeedRight=parameters.getIntersectionParams().getSaturationSpeedRight();
        double SaturationSpeedThrough=parameters.getIntersectionParams().getSaturationSpeedThrough();

        // Get the vehicle length
        double VehLength= parameters.getVehicleParams().getVehicleLength();

        // Time for a vehicle passing a detector for different movements (in seconds)
        double PassTimeLeft=(VehLength+DetLength)*3600/SaturationSpeedLeft/5280;
        double PassTimeRight=(VehLength+DetLength)*3600/SaturationSpeedRight/5280;
        double PassTimeThrough=(VehLength+DetLength)*3600/SaturationSpeedThrough/5280;

        // Get the proportions of vehicles for left-turn, through and right-turn
        double ProportionLeft= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Left");
        double ProportionThrough= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Through");
        double ProportionRight= parameterOperations.FindTrafficProportion(DetectorType, parameters,"Right");

        // Get the number of vehicles within one cycle: considering the green time is fully used
        double NumVeh=GreenTime/SaturationHeadway;
        // Get the number of vehicles for each movement
        double NumVehLeft=NumVeh*ProportionLeft;
        double NumVehThrough=NumVeh*ProportionThrough;
        double NumVehRight=NumVeh*ProportionRight;

        // Get the discharging time given the current number of vehicles
        double DischargingTime=StartUpLostTime+PassTimeLeft*NumVehLeft+PassTimeThrough*NumVehThrough+PassTimeRight*NumVehRight;

        // Get the occupancy threshold: red time + discharging time
        double OccThresholdLow=100*Math.min(1.0,DischargingTime/parameters.getSignalSettings().getCycleLength());
        double OccThresholdHigh=100*Math.min(1.0, DischargingTime/parameters.getSignalSettings().getCycleLength()+
                (1-GreenTime/parameters.getSignalSettings().getCycleLength()));

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


}
