package main;

import java.io.File;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import commonClass.bluetoothData.TravelTimeStatistics;
import config.getTaskID;
import config.loadProgramSettingsFromFile;
import dataProvider.*;
import settings.programSettings;
import networkInput.readFromAimsun;
import networkInput.reconstructNetwork;
import connectors.extractNetworkFile;
import estimation.trafficStateEstimation;
import initialization.trafficInitialization;
import writer.writingFunctions;
import commonClass.forInitialization.*;
import commonClass.forAimsunNetwork.*;
import commonClass.forGeneralNetwork.*;
import commonClass.forAimsunNetwork.signalControl.*;
import commonClass.estimationData.*;
import commonClass.query.*;
import commonClass.forEstimation.*;
import commonClass.simulationData.SimulationStatistics;

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
        int taskID=getTaskID.getTaskIDFromScreen();

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

        // Check the selected task\
        int EndTime;
        int Interval;
        int StartTime;
        QueryMeasures queryMeasures;
        AimsunNetworkByApproach aimsunNetworkByApproach;
        List<AimsunControlPlanJunction> activeControlPlans;
        switch (taskID) {
            case 1:
                System.out.print("1:  Extract the Aimsun network\n"); // Extract the network files
                if (cBlock.AimsunFolder == null || cBlock.AimsunFileName == null ||
                        cBlock.AimsunNetExtractionScriptName == null || cBlock.JunctionYes == null ||
                        cBlock.SectionYes == null || cBlock.DetectorYes == null || cBlock.SignalYes == null) {
                    System.out.println("Not enough information to update the Aimsun network!");
                    System.exit(-1);
                }
                extractNetworkFile.extractFromAimsun(AimsunExeNew);
                break;

            case 2:
                System.out.print("2:  Read the Aimsun network files\n"); // Read the network files
                if (cBlock.AimsunFolder == null) {
                    System.out.println("Missing the folder name!");
                    System.exit(-1);
                }
                AimsunNetwork aimsunNetwork = readFromAimsun.readAimsunNetworkFiles();
                break;

            case 3:
                System.out.print("3:  Read and reconstruct the Aimsun network files\n"); // Read and reconstruct the network files
                if (cBlock.AimsunFolder == null) {
                    System.out.println("Missing the folder name!");
                    System.exit(-1);
                }
                reconstructNetwork.reconstructAimsunNetwork();
                break;

            case 4:
                System.out.print("4:  Load Aimsun simulation data to database\n"); // Load aimsun simulation data into database
                try {
                    conAimsunSimulation = DriverManager.getConnection(hostAimsunSimulation, userName, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (cBlock.sigInfFolder == null) {
                    System.out.println("Empty simulation signal information!");
                    System.exit(-1);
                } else {
                    // Load Aimsun signal information
                    loadSimulationData.LoadSignalInformationToDatabase(conAimsunSimulation, cBlock.replicationName, cBlock.sigInfFolder);
                }

                if (cBlock.vehInfFolder == null) {
                    System.out.println("Empty simulation vehicle information!");
                    System.exit(-1);
                } else {
                    // Load Aimsun vehicle information
                    loadSimulationData.LoadVehicleInformationToDatabase(conAimsunSimulation, cBlock.replicationName, cBlock.vehInfFolder);
                }
                break;

            case 5:
                System.out.print("5:  Arterial traffic estimation\n"); // Arterial traffic estimation
                if (cBlock.AimsunFolder == null) {
                    System.out.println("Missing the folder name!");
                    System.exit(-1);
                }
                try {
                    conAimsunSimulation = DriverManager.getConnection(hostAimsunSimulation, userName, password);
                    System.out.println("Succeeded to connect to the MySQL database!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Reconstruct the network by approach
                aimsunNetworkByApproach = reconstructNetwork.reconstructAimsunNetwork();

                Interval = 300;
                for (EndTime = Interval; EndTime <= 24 * 3600; EndTime = EndTime + Interval){
                    //EndTime=(int)(7.5*3600);
                    System.out.println("Time Step="+EndTime+" Seconds");

                    StartTime = Math.max(0, EndTime - Interval * 3);

                    // Set query measures
                    queryMeasures = new QueryMeasures(2016, -1, -1,
                            8, true, new int[]{StartTime, EndTime}, Interval);

                    // Get the active control plans first
                    conActualFieldSignal = null;
                    activeControlPlans = getSignalData.GetActiveControlPlansForGivenDayAndTime(aimsunNetworkByApproach
                            , queryMeasures, conActualFieldSignal);

                    // Next, run traffic state estimation
                    List<TrafficState> trafficStateList = trafficStateEstimation.arterialTrafficStateEstimation(conTCSServer, aimsunNetworkByApproach, queryMeasures
                            , activeControlPlans);
                    // Then, run traffic initialization: vehicle generation and active phase determination
                    List<TrafficState> trafficStateListNew = new ArrayList<TrafficState>();
                    for (int i = 0; i < trafficStateList.size(); i++) {
                        if (trafficStateList.get(i).getTrafficStateByApproach().getQueueThreshold() != null) {
                            trafficStateListNew.add(trafficStateList.get(i));
                        }
                    }

                    loadEstimationResults.LoadEstimationResultsToDatabase(conAimsunSimulation, "estimation_results", trafficStateList);
                }
                break;

            case 6:
                System.out.print("6:  Arterial traffic initialization\n"); // Arterial traffic initialization
                if(cBlock.AimsunFolder ==null){
                    System.out.println("Missing the folder name!");
                    System.exit(-1);
                }

                EndTime=(int)(8*3600);
                Interval=300;
                StartTime=Math.max(0,EndTime-Interval*3);

                // Set query measures
                queryMeasures=new QueryMeasures(2016,-1,-1,
                        8,true, new int []{StartTime,EndTime},Interval);

                // Reconstruct the network by approach
                aimsunNetworkByApproach =reconstructNetwork.reconstructAimsunNetwork();

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
                activeControlPlans=getSignalData.GetActiveControlPlansForGivenDayAndTime(aimsunNetworkByApproach
                        , queryMeasures, conActualFieldSignal);

                Map<Integer,EstimationResults> estimationResultsList=getEstimationResults.GetEstimationResultsFromDatabase(conAimsunSimulation,
                        "estimation_results",queryMeasures);

                List<SimVehicle> simVehicleList=trafficInitialization.VehicleGeneration(aimsunNetworkByApproach.getAimsunNetworkByApproach(),
                        estimationResultsList,activeControlPlans,simulationStatistics);

                writingFunctions.WriteSimVehicleToCSVForAimsun(simVehicleList, cBlock.AimsunFolder);
                break;

            case 7:
                System.out.print("7:  Load Bluetooth travel time data\n"); // Bluetooth travel time
                try {
                    conAimsunSimulation= DriverManager.getConnection(hostAimsunSimulation, userName, password);
                }catch(SQLException e){
                    e.printStackTrace();
                }
                loadBluetoothData.LoadBluetoothTravelTimeToDatabase(conAimsunSimulation, cBlock.bluetoothFolder);

            case 8:
                System.out.print("8:  Validate queue estimates with Bluetooth travel time\n"); // Validation
                try {
                    conAimsunSimulation= DriverManager.getConnection(hostAimsunSimulation, userName, password);
                }catch(SQLException e){
                    e.printStackTrace();
                }
                String LocationA="Huntington_SantaClara";
                String LocationB="Huntington_Gateway";
                EndTime=24*3600;
                Interval=300;
                StartTime=0;
                //EndTime=(int)(7.5*3600);
                //Interval=300;
                //StartTime=Math.max(0,EndTime-Interval*3);

                // Set query measures
                queryMeasures=new QueryMeasures(-1,-1,-1,
                        8,true, new int []{StartTime,EndTime},Interval);

                // Eastbound: From A to B
                TravelTimeStatistics travelTimeStatisticsAToB=getBluetoothData.getTravelTimeStatistics(conAimsunSimulation, LocationA, LocationB, queryMeasures);
                writingFunctions.WriteBluetoothTravelTimeToCSV(travelTimeStatisticsAToB, queryMeasures, LocationA+"_"+LocationB,cBlock.bluetoothOutputFolder);

                // Westbound: From B to A
                TravelTimeStatistics travelTimeStatisticsBToA=getBluetoothData.getTravelTimeStatistics(conAimsunSimulation, LocationB, LocationA, queryMeasures);
                writingFunctions.WriteBluetoothTravelTimeToCSV(travelTimeStatisticsBToA, queryMeasures, LocationB+"_"+LocationA,cBlock.bluetoothOutputFolder);

                break;

            default:
                System.out.println("Unknown task!");
                System.exit(-1);
        }
    }

}
