package settings;

/**
 * Created by Qijian-Gan on 10/5/2017.
 */
public class programSettings {

    // Users
    public  String userName=null;
    public  String password=null;

    //*********Aimsun Settings**********
    //Network extraction
    public String AimsunFolder=null;
    public String AimsunFileName=null;
    public String AimsunNetExtractionScriptName=null;
    public String JunctionYes=null;
    public String SectionYes=null;
    public String DetectorYes=null;
    public String SignalYes=null;

    //Default signal settings
    public int CycleLength=120;
    public int LeftTurnGreen=15;
    public int ThroughGreen=30;
    public int RightTurnGreen=30;
    public String LeftTurnSetting="Protected";


    public double VehicleLength;
    public double FFSpeedForAdvDet;
    public double OccThresholdForAdvDet;
    public double SaturationHeadway;
    public double SaturationSpeedLeft;
    public double SaturationSpeedRight;
    public double SaturationSpeedThrough;
    public double StartupLostTime;
    public double JamSpacing;
    public double DistanceAdvandedDetector;
    public double LeftTurnPocket;
    public double RightTurnPocket;
    public double DistanceToEnd;

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
