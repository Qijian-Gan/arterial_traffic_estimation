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
import dataProvider.getSignalData;
import settings.programSettings;
import networkInput.readFromAimsun;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork;
import networkInput.reconstructNetwork.*;
import connectors.extractNetworkFile;
import estimation.trafficStateEstimation;
import estimation.trafficStateEstimation.*;
import estimation.trafficInitialization.*;
import estimation.trafficInitialization;
import dataProvider.loadSimulationData;
import dataProvider.loadSimulationData.*;
import dataProvider.getSimulationData;
import dataProvider.getSimulationData.*;
import dataProvider.loadEstimationResults;


public class MainFunction{

    // ***********Global settings************
    // Database
    public static String host="jdbc:mysql://localhost:3306/arcadia_tcs_server_data"; // For TCS server
    public static String hostIENArcadia="jdbc:mysql://localhost:3306/arcadia_ien_server_data"; // For IEN-Arcadia
    public static String hostIENLACO="jdbc:mysql://localhost:3306/laco_ien_server_data";  // For IEN-LACO
    public static String hostAimsunSimulation="jdbc:mysql://localhost:3306/traffic_estimation_prediction";  // For Aimsun simulation;
    public static String hostAimsunSimulationSqlite;
    // Users
    public static String userName="ganqijian";
    public static String password="!PATH2017ganqijian";
    // VPN connection
    public static String vpnServerName="Arcadia";
    public static String vpnUser="path";
    public static String vpnPassword="arcadia";
    // Variables
    public static Connection conTCSServer;
    public static Connection conArcadia;
    public static Connection conLACO;
    public static Connection conActualFieldSignal;
    public static Connection conAimsunSimulation;
    public static Connection conAimsunSimulationSqlite;

    public static String AimsunExeOld="aimsun.exe"; // Need to have no space in between
    public static String AimsunExeNew="AimsunNext.exe"; // Need to have no space in between

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
            extractNetworkFile.extractFromAimsun(AimsunExeNew);

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
            System.out.print("4:  Load Aimsun simulation data to database\n"); // Load aimsun simulation data into database
            try {
                conAimsunSimulation= DriverManager.getConnection(hostAimsunSimulation, userName, password);
            }catch(SQLException e){
                e.printStackTrace();
            }

            if(cBlock.sigInfFolder==null){
                System.out.println("Empty simulation signal information!");
                System.exit(-1);
            }else{
                // Load Aimsun signal information
                loadSimulationData.LoadSignalInformationToDatabase(conAimsunSimulation, cBlock.replicationName, cBlock.sigInfFolder);
            }

            if(cBlock.vehInfFolder==null){
                System.out.println("Empty simulation vehicle information!");
                System.exit(-1);
            }else{
                // Load Aimsun vehicle information
                loadSimulationData.LoadVehicleInformationToDatabase(conAimsunSimulation, cBlock.replicationName, cBlock.vehInfFolder);
            }

        }
        else if(taksID==5){
            System.out.print("5:  Arterial traffic estimation\n"); // Arterial traffic estimation
            if(cBlock.AimsunFolder ==null){
                System.out.println("Missing the folder name!");
                System.exit(-1);
            }
            int EndTime=(int)7.5*3600;
            int Interval=300;
            int StartTime=Math.max(0,EndTime-Interval*3);

            // Reconstruct the network by approach
            reconstructNetwork.AimsunNetworkByApproach aimsunNetworkByApproach =reconstructNetwork.reconstructAimsunNetwork();

            // Set query measures
            QueryMeasures queryMeasures=new QueryMeasures(-1,-1,-1,
                    8,true, new int []{StartTime,EndTime},Interval);

            // Get the simulation statistics
            hostAimsunSimulationSqlite="jdbc:sqlite:"+cBlock.sqliteFileLocation+"\\"+cBlock.sqliteFileName;
            try {
                conAimsunSimulationSqlite=DriverManager.getConnection(hostAimsunSimulationSqlite);
                System.out.println("Succeeded to connect to the sqlite database!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conAimsunSimulation= DriverManager.getConnection(hostAimsunSimulation, userName, password);
                System.out.println("Succeeded to connect to the MySQL database!");
            }catch(SQLException e){
                e.printStackTrace();
            }
            SimulationStatistics simulationStatistics=getSimulationData.GetSimulationStatistics(conAimsunSimulationSqlite,conAimsunSimulation, EndTime,true
                    ,true,true,true,true,true,true);

            // Get the active control plans first
            conActualFieldSignal=null;
            List<AimsunControlPlanJunction> activeControlPlans=getSignalData.GetActiveControlPlansForGivenDayAndTime(aimsunNetworkByApproach
                    , queryMeasures, conActualFieldSignal);

            // Next, run traffic state estimation
            List<TrafficState> trafficStateList=trafficStateEstimation.arterialTrafficStateEstimation(conTCSServer,aimsunNetworkByApproach,queryMeasures
                    ,activeControlPlans);
            // Then, run traffic initialization: vehicle generation and active phase determination
            List<TrafficState> trafficStateListNew=new ArrayList<TrafficState>();
            for(int i=0;i<trafficStateList.size();i++){
                if(trafficStateList.get(i).getTrafficStateByApproach().getQueueThreshold()!=null){
                    trafficStateListNew.add(trafficStateList.get(i));
                }
            }
            loadEstimationResults.LoadEstimationResultsToDatabase(conAimsunSimulation, "estimation_results", trafficStateList);

            //List<SimVehicle> simVehicleList=trafficInitialization.VehicleGeneration(trafficStateList,activeControlPlans,simulationStatistics);
        }
        else{
            System.out.println("Unknown task!");
            System.exit(-1);
        }
    }

}
