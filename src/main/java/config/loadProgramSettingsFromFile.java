package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
                // ignore blank lines and lines that start with a pound symbol (#)
                if ((confLine.length() > 0) && (!confLine.trim().startsWith("#", 0))) {
                    String[] tokens = confLine.split("=");

                    // User name & password
                    if(tokens[0].equals("userName"))
                        cBlock.userName=tokens[1].trim();
                    else if(tokens[0].equals("password")){
                        cBlock.password=tokens[1].trim();
                    }
                    // Aimsun settings
                    else if(tokens[0].equals("AimsunFolder")){
                        cBlock.AimsunFolder=tokens[1].trim();
                    }else if(tokens[0].equals("AimsunFileName")){
                        cBlock.AimsunFileName=tokens[1].trim();
                    }else if(tokens[0].equals("AimsunNetExtractionScriptName")){
                        cBlock.AimsunNetExtractionScriptName=tokens[1].trim();
                    }else if(tokens[0].equals("JunctionYes")){
                        cBlock.JunctionYes=tokens[1].trim();
                    }else if(tokens[0].equals("SectionYes")){
                        cBlock.SectionYes=tokens[1].trim();
                    }else if(tokens[0].equals("DetectorYes")){
                        cBlock.DetectorYes=tokens[1].trim();
                    }else if(tokens[0].equals("SignalYes")){
                        cBlock.SignalYes=tokens[1].trim();
                    }
                    // Default signal settings
                    else if(tokens[0].equals("CycleLength")){
                        cBlock.CycleLength=Integer.parseInt(tokens[1].trim());
                    }else if(tokens[0].equals("LeftTurnGreen")){
                        cBlock.LeftTurnGreen=Integer.parseInt(tokens[1].trim());
                    }else if(tokens[0].equals("ThroughGreen")){
                        cBlock.ThroughGreen=Integer.parseInt(tokens[1].trim());
                    }else if(tokens[0].equals("RightTurnGreen")){
                        cBlock.RightTurnGreen=Integer.parseInt(tokens[1].trim());
                    }else if(tokens[0].equals("LeftTurnSetting")){
                        cBlock.LeftTurnSetting=tokens[1].trim();
                    }

                    // Default parameters
                    else if(tokens[0].equals("VehicleLength")){
                        cBlock.VehicleLength=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("FreeFlowSpeedForAdvancedDetector")){
                        cBlock.FFSpeedForAdvDet=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("OccupancyThresholdForAdvancedDetector")){
                        cBlock.OccThresholdForAdvDet=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("SaturationHeadway")){
                        cBlock.SaturationHeadway=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("SaturationSpeedLeft")){
                        cBlock.SaturationSpeedLeft=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("SaturationSpeedRight")){
                        cBlock.SaturationSpeedRight=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("SaturationSpeedThrough")){
                        cBlock.SaturationSpeedThrough=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("StartupLostTime")){
                        cBlock.StartupLostTime=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("JamSpacing")){
                        cBlock.JamSpacing=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("DistanceAdvandedDetector")){
                        cBlock.DistanceAdvandedDetector=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("LeftTurnPocket")){
                        cBlock.LeftTurnPocket=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("RightTurnPocket")){
                        cBlock.RightTurnPocket=Double.parseDouble(tokens[1].trim());
                    }else if(tokens[0].equals("DistanceToEnd")){
                        cBlock.DistanceToEnd=Double.parseDouble(tokens[1].trim());
                    }

                    // Default parameters
                    else if(tokens[0].equals("LeftTurn")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.LeftTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("LeftTurnQueue")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.LeftTurnQueue=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("RightTurn")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.RightTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("RightTurnQueue")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.RightTurnQueue=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("Advanced")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.Advanced=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AllMovements")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AllMovements=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedLeftTurn")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedLeftTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedRightTurn")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedRightTurn=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedThrough")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("Through")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.Through=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedLeftAndThrough")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedLeftAndThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("LeftAndThrough")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.LeftAndThrough=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedLeftAndRight")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedLeftAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("LeftAndRight")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.LeftAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("AdvancedThroughAndRight")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.AdvancedThroughAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }else if(tokens[0].equals("ThroughAndRight")){
                        String [] tmp=tokens[1].trim().split(",");
                        cBlock.ThroughAndRight=new double []{Double.parseDouble(tmp[0].trim()),Double.parseDouble(tmp[1].trim()),
                                Double.parseDouble(tmp[2].trim())};
                    }

                    else{
                        System.out.println("Unkown input:"+tokens[0]);
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
