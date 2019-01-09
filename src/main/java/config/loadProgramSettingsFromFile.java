package config;

import java.io.BufferedReader;
import java.io.FileReader;

import settings.programSettings;

public class loadProgramSettingsFromFile {

    public static programSettings loadProgramSettings(String programSettingFile) {
        // This function is used to load program settings

        programSettings cBlock=new programSettings(); // Create a new configuration block
        String confLine;

        System.out.println("Reading in the config file : " + programSettingFile);
        try {
            BufferedReader confFile = new BufferedReader(new FileReader(programSettingFile));

            while ((confLine = confFile.readLine()) != null) {// Start to read the file

                confLine = confLine.trim();
                // Ignore blank lines and lines that start with a pound symbol (#)
                if ((confLine.length() > 0) && (!confLine.trim().startsWith("#", 0))) {
                    String[] tokens = confLine.split("=");

                    String [] tmp;
                    switch (tokens[0]){
                    // User name & password
                        case "userName":
                            cBlock.userName=tokens[1].trim();
                            break;
                        case "password":
                            cBlock.password=tokens[1].trim();
                            break;

                    // Aimsun settings
                        case "AimsunFolder":
                            cBlock.AimsunFolder=tokens[1].trim();
                            break;
                        case "AimsunFileName":
                            cBlock.AimsunFileName=tokens[1].trim();
                            break;
                        case "AimsunNetExtractionScriptName":
                            cBlock.AimsunNetExtractionScriptName=tokens[1].trim();
                            break;
                        case "JunctionYes":
                            cBlock.JunctionYes=tokens[1].trim();
                            break;
                        case "SectionYes":
                            cBlock.SectionYes=tokens[1].trim();
                            break;
                        case "DetectorYes":
                            cBlock.DetectorYes=tokens[1].trim();
                            break;
                        case "SignalYes":
                            cBlock.SignalYes=tokens[1].trim();
                            break;

                    // Default signal settings
                        case "CycleLength":
                            cBlock.CycleLength=Integer.parseInt(tokens[1].trim());
                            break;
                        case "LeftTurnGreen":
                            cBlock.LeftTurnGreen=Integer.parseInt(tokens[1].trim());
                            break;
                        case "ThroughGreen":
                            cBlock.ThroughGreen=Integer.parseInt(tokens[1].trim());
                            break;
                        case "RightTurnGreen":
                            cBlock.RightTurnGreen=Integer.parseInt(tokens[1].trim());
                            break;
                        case "LeftTurnSetting":
                            cBlock.LeftTurnSetting=tokens[1].trim();
                            break;

                    // Default parameters
                        case "VehicleLength":
                            cBlock.VehicleLength=Double.parseDouble(tokens[1].trim());
                            break;
                        case "FreeFlowSpeedForAdvanceDetector":
                            cBlock.FFSpeedForAdvDet=Double.parseDouble(tokens[1].trim());
                            break;
                        case "OccupancyThresholdForAdvanceDetector":
                            cBlock.OccThresholdForAdvDet=Double.parseDouble(tokens[1].trim());
                            break;
                        case "SaturationHeadway":
                            cBlock.SaturationHeadway=Double.parseDouble(tokens[1].trim());
                            break;
                        case "SaturationSpeedLeft":
                            cBlock.SaturationSpeedLeft=Double.parseDouble(tokens[1].trim());
                            break;
                        case "SaturationSpeedRight":
                            cBlock.SaturationSpeedRight=Double.parseDouble(tokens[1].trim());
                            break;
                        case "SaturationSpeedThrough":
                            cBlock.SaturationSpeedThrough=Double.parseDouble(tokens[1].trim());
                            break;
                        case "StartupLostTime":
                            cBlock.StartupLostTime=Double.parseDouble(tokens[1].trim());
                            break;
                        case "JamSpacing":
                            cBlock.JamSpacing=Double.parseDouble(tokens[1].trim());
                            break;
                        case "DistanceAdvanceDetector":
                            cBlock.DistanceAdvanceDetector=Double.parseDouble(tokens[1].trim());
                            break;
                        case "LeftTurnPocket":
                            cBlock.LeftTurnPocket=Double.parseDouble(tokens[1].trim());
                            break;
                        case "RightTurnPocket":
                            cBlock.RightTurnPocket=Double.parseDouble(tokens[1].trim());
                            break;
                        case "DistanceToEnd":
                            cBlock.DistanceToEnd=Double.parseDouble(tokens[1].trim());
                            break;

                        // Default parameters
                        case "LeftTurn":
                            tmp=tokens[1].trim().split(",");
                            cBlock.LeftTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "LeftTurnQueue":
                            tmp=tokens[1].trim().split(",");
                            cBlock.LeftTurnQueue=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "RightTurn":
                            tmp=tokens[1].trim().split(",");
                            cBlock.RightTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "RightTurnQueue":
                            tmp=tokens[1].trim().split(",");
                            cBlock.RightTurnQueue=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "Advance":
                            tmp=tokens[1].trim().split(",");
                            cBlock.Advance=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AllMovements":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AllMovements=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceLeftTurn":
                            tmp=tokens[1].trim().split(",");
                        cBlock.AdvanceLeftTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceRightTurn":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AdvanceRightTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceThrough":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AdvanceThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "Through":
                            tmp=tokens[1].trim().split(",");
                            cBlock.Through=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceLeftAndThrough":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AdvanceLeftAndThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "LeftAndThrough":
                            tmp=tokens[1].trim().split(",");
                            cBlock.LeftAndThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceLeftAndRight":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AdvanceLeftAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "LeftAndRight":
                            tmp=tokens[1].trim().split(",");
                            cBlock.LeftAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "AdvanceThroughAndRight":
                            tmp=tokens[1].trim().split(",");
                            cBlock.AdvanceThroughAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;
                        case "ThroughAndRight":
                            tmp=tokens[1].trim().split(",");
                            cBlock.ThroughAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                    Double.parseDouble(tmp[2].trim())};
                            break;

                        // Aimsun simulation
                        case "replicationName":
                            cBlock.replicationName=tokens[1].trim();
                            break;
                        case "sqliteFileLocation":
                            cBlock.sqliteFileLocation=tokens[1].trim();
                            break;
                        case "sqliteFileName":
                            cBlock.sqliteFileName=tokens[1].trim();
                            break;
                        case "vehInfFolder":
                            cBlock.vehInfFolder=tokens[1].trim();
                            break;
                        case "sigInfFolder":
                            cBlock.sigInfFolder=tokens[1].trim();
                            break;

                        // Bluetooth data
                        case "bluetoothFolder":
                            cBlock.bluetoothFolder=tokens[1].trim();
                        case "bluetoothOutputFolder":
                            cBlock.bluetoothOutputFolder=tokens[1].trim();
                        default:
                            System.out.println("Unknown input:"+tokens[0]);
                    }
                } // If confLine.length() > 0
            } // while read config file

            confFile.close();
            return cBlock ;
        } catch (Exception e) {
            System.out.println("Exceptions have occurred reading the config file!  Exiting!");
            System.exit(-1);
            return null;
        }
    }
}
