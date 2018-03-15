package main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import config.getTaskID;
import config.loadProgramSettingsFromFile;
import settings.programSettings;
import networkInput.readFromAimsun;
import networkInput.reconstructNetwork;
import connectors.extractNetworkFile;
import estimation.trafficStateEstimation;


public class MainFunction{

    // ***********Global settings************
    // Database
    public static String host="jdbc:mysql://localhost:3306/arcadia_tcs_server_data"; // For TCS server
    public static String hostIENArcadia="jdbc:mysql://localhost:3306/arcadia_ien_server_data"; // For IEN-Arcadia
    public static String hostIENLACO="jdbc:mysql://localhost:3306/laco_ien_server_data";  // For IEN-LACO
    // Users
    public static String userName="root";
    public static String password="!Ganqijian2017";
    // VPN connection
    public static String vpnServerName="Arcadia";
    public static String vpnUser="path";
    public static String vpnPassword="arcadia";
    // Variables
    public static Connection conTCSServer;
    public static Connection conArcadia;
    public static Connection conLACO;

    public static programSettings cBlock=new programSettings();
    // ***********Global settings************


    public static void main(String [] args) {

        // Selection of type of tasks
        int taksID=getTaskID.getTaskIDFromScreen();

        // Get the program settings
        File configFile = new File("");
        configFile=new File(configFile.getAbsolutePath(),"\\src\\main\\java\\trafficEstimation.conf");
        System.out.println("Current configuration file path : "+configFile.getAbsolutePath());
        cBlock=loadProgramSettingsFromFile.loadProgramSettings(configFile.getAbsolutePath());

        try {
            conTCSServer = DriverManager.getConnection(host, userName, password);
        }catch(SQLException e){
            e.printStackTrace();
        }

        // Check the selected task
        if(taksID==1){
            System.out.print("1:  Extract the Aimsun network\n"); // Extract the network files
            if(cBlock.AimsunFolder ==null || cBlock.AimsunFileName ==null ||
                    cBlock.AimsunNetExtractionScriptName ==null || cBlock.JunctionYes==null ||
                    cBlock.SectionYes==null || cBlock.DetectorYes==null||cBlock.SignalYes==null){
                System.out.println("Not enough information to update the Aimsun network!");
                System.exit(-1);
            }
            extractNetworkFile.extractFromAimsun();

        }else if(taksID==2){
            System.out.print("2:  Read the Aimsun network files\n"); // Read the network files
            if(cBlock.AimsunFolder ==null){
                System.out.println("Missing the folder name!");
                System.exit(-1);
            }
            networkInput.readFromAimsun.AimsunNetwork aimsunNetwork=readFromAimsun.readAimsunNetworkFiles();
        }else if(taksID==3){
            System.out.print("3:  Read and reconstruct the Aimsun network files\n"); // Read and reconstruct the network files
            if(cBlock.AimsunFolder ==null){
                System.out.println("Missing the folder name!");
                System.exit(-1);
            }
            reconstructNetwork.reconstructAimsunNetwork();

        }else if(taksID==4){
            System.out.print("4:  Arterial traffic estimation\n"); // Arterial traffic estimation
            if(cBlock.AimsunFolder ==null){
                System.out.println("Missing the folder name!");
                System.exit(-1);
            }
            int EndTime=(int)7.5*3600;
            int Interval=300;
            int StartTime=Math.max(0,EndTime-Interval*3);

            reconstructNetwork.AimsunNetworkByApproach aimsunNetworkByApproach
                    =reconstructNetwork.reconstructAimsunNetwork();
            trafficStateEstimation.QueryMeasures queryMeasures=new trafficStateEstimation.QueryMeasures(-1,-1,-1,
                    8,true, new int []{StartTime,EndTime},Interval);
            trafficStateEstimation.arterialTrafficStateEstimation(conTCSServer,aimsunNetworkByApproach,queryMeasures);

        }
        else{
            System.out.println("Unknown task!");
            System.exit(-1);
        }
    }

}
