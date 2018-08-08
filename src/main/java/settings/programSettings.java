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
    public double FFSpeedForAdvDet; // Free flow speed for advance detectors
    public double OccThresholdForAdvDet; // Occ threshold for advance detectors
    public double SaturationHeadway; // Saturation settings
    public double SaturationSpeedLeft;
    public double SaturationSpeedRight;
    public double SaturationSpeedThrough;
    public double StartupLostTime;
    public double JamSpacing;
    public double DistanceAdvanceDetector;
    public double LeftTurnPocket; // Turn pockets
    public double RightTurnPocket;
    public double DistanceToEnd;

    public double [] LeftTurn; // Exclusive left turn
    public double [] LeftTurnQueue;
    public double [] AdvanceLeftTurn;
    public double [] RightTurn; // Exclusive right turn
    public double [] RightTurnQueue;
    public double [] AdvanceRightTurn;
    public double [] Advance;   // Mixed through, left turn, and right turn
    public double [] AllMovements;
    public double [] AdvanceThrough; // Exclusive through
    public double [] Through;
    public double [] AdvanceLeftAndThrough; // Left turn and through only
    public double [] LeftAndThrough;
    public double [] AdvanceLeftAndRight;  // Left turn and right turn only
    public double [] LeftAndRight;
    public double [] AdvanceThroughAndRight; // Through and right turn only
    public double [] ThroughAndRight;

    public String replicationName;// Replication name
    public String sqliteFileLocation; // sqlite file location
    public String sqliteFileName;  // sqlite file name
    public String vehInfFolder;  // simulation vehicle information
    public String sigInfFolder;  // simulation signal information

}
