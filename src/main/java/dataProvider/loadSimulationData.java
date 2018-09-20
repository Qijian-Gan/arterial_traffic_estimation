package dataProvider;

import commonClass.simulationData.SimVehicle.*;
import commonClass.simulationData.SimSignal.*;
import util.util;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;

/**
 * Created by Qijian-Gan on 6/1/2018.
 */
public class loadSimulationData {
    // This function is used to load simulation data from Aimsun to the MySQL database.

    //************************************************************************************
    // Vehicle information
    //************************************************************************************
    /**
     *
     * @param con Database connection
     * @param ReplicationName Replication name
     * @param VehInfFolder Vehicle information folder
     */
    public static void LoadVehicleInformationToDatabase(Connection con, String ReplicationName, String VehInfFolder){
        // This function is used to load vehicle information to database

        // Get the list of files
        File fileDir = new File(VehInfFolder);
        File[] listOfFiles = fileDir.listFiles();
        for(int i=0;i<listOfFiles.length;i++){
            System.out.println("Reading: " +listOfFiles[i].getAbsoluteFile());
            List<AimsunVehInf> aimsunSigInfList=ReadVehicleInformationFile(listOfFiles[i].getAbsoluteFile());
            System.out.println("Saving: "+listOfFiles[i].getAbsoluteFile());
            SaveVehInf(con, ReplicationName, aimsunSigInfList);
        }


    }

    /**
     *
     * @param VehInfFile Vehicle information folder
     * @return
     */
    public static List<AimsunVehInf> ReadVehicleInformationFile(File VehInfFile){
        // Read the vehicle information file (txt format)

        List<AimsunVehInf> aimsunVehInfList=new ArrayList<AimsunVehInf>();

        BufferedReader br = null ;
        try {
            br = new BufferedReader(new FileReader(VehInfFile));
        } catch (FileNotFoundException e) {
            System.out.println("The vehicle information file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            line = br.readLine(); // Get rid of the header
            while ((line = br.readLine()) != null) {
                String [] tmpLine=line.split(",");
                double Time=Double.parseDouble(tmpLine[0].trim());
                int VehicleID=Integer.parseInt(tmpLine[1].trim());
                int VehicleType=Integer.parseInt(tmpLine[2].trim());
                int SectionID=Integer.parseInt(tmpLine[3].trim());
                int LaneID=Integer.parseInt(tmpLine[4].trim());
                double CurrentPosition=Double.parseDouble(tmpLine[5].trim());
                double CurrentSpeed=Double.parseDouble(tmpLine[6].trim());
                int CentroidOrigin=Integer.parseInt(tmpLine[7].trim());
                int CentroidDestination=Integer.parseInt(tmpLine[8].trim());
                double DistanceToEnd=Double.parseDouble(tmpLine[9].trim());
                AimsunVehInf aimsunVehInf=new AimsunVehInf(Time,VehicleID,VehicleType,SectionID,LaneID,CurrentPosition
                        , CurrentSpeed, CentroidOrigin, CentroidDestination, DistanceToEnd);
                aimsunVehInfList.add(aimsunVehInf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return aimsunVehInfList;
    }

    /**
     *
     * @param con Database connection
     * @param ReplicationName Replication name
     * @param aimsunVehInfList List<AimsunVehInf> class
     */
    public static void SaveVehInf(Connection con, String ReplicationName, List<AimsunVehInf> aimsunVehInfList){
        // This function is used to save vehicle information to database

        String TableName="simulated_vehicle_"+ReplicationName;
        String Header="insert into "+TableName;
        List<String> sqlStatements=new ArrayList<String>();
        HashSet<String> stringHashSet= new HashSet<String>();; // Create the hash set
        for(int i=0;i<aimsunVehInfList.size();i++){
            AimsunVehInf aimsunVehInf=aimsunVehInfList.get(i);
            String sql=Header+" values (\""+aimsunVehInf.getTime()+"\",\""+aimsunVehInf.getVehicleID()+"\",\""+
                    aimsunVehInf.getVehicleType()+"\",\""+aimsunVehInf.getSectionID()+"\",\""+aimsunVehInf.getLaneID()+"\",\""+
                    aimsunVehInf.getCurrentPosition()+"\",\""+aimsunVehInf.getCurrentSpeed()+"\",\""+
                    aimsunVehInf.getCentroidOrigin()+"\",\""+aimsunVehInf.getCentroidDestination()+"\",\""+aimsunVehInf.getDistanceToEnd()+"\");";
            if(stringHashSet.add(aimsunVehInf.getTime()+"-"+aimsunVehInf.getVehicleID()+"-"+aimsunVehInf.getSectionID()+"-"+aimsunVehInf.getLaneID())) {
                sqlStatements.add(sql);
            }
        }

        try{
            Statement ps=con.createStatement();
            util.insertSQLBatch(ps, sqlStatements, 10000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    //************************************************************************************
    // Signal information
    //************************************************************************************
    /**
     *
     * @param con Database connection
     * @param ReplicationName Replication name
     * @param SigInfFolder Signal information folder
     */
    public static void LoadSignalInformationToDatabase(Connection con, String ReplicationName, String SigInfFolder){
        // This function is used to load signal information to database

        // Get the list of files
        File fileDir = new File(SigInfFolder);
        File[] listOfFiles = fileDir.listFiles();
        for(int i=0;i<listOfFiles.length;i++){
            System.out.println("Reading: " +listOfFiles[i].getAbsoluteFile());
            List<AimsunSigInf> aimsunSigInfList=ReadSignalInformationFile(listOfFiles[i].getAbsoluteFile());
            System.out.println("Saving: "+listOfFiles[i].getAbsoluteFile());
            SaveSigInf(con, ReplicationName, aimsunSigInfList);
        }
    }

    /**
     *
     * @param SigInfFile Signal information folder
     * @return
     */
    public static List<AimsunSigInf> ReadSignalInformationFile(File SigInfFile){
        // Read the signal information file (txt format)
        List<AimsunSigInf> aimsunSigInfList=new ArrayList<AimsunSigInf>();

        BufferedReader br = null ;
        try {
            br = new BufferedReader(new FileReader(SigInfFile));
        } catch (FileNotFoundException e) {
            System.out.println("The signal information file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            line = br.readLine(); // Get rid of the header
            while ((line = br.readLine()) != null) {
                String [] tmpLine=line.split(",");
                double TimeRelative=Double.parseDouble(tmpLine[0].trim());
                double Time=Double.parseDouble(tmpLine[1].trim());
                int JunctionID=Integer.parseInt(tmpLine[2].trim());
                int ControlType=Integer.parseInt(tmpLine[3].trim());
                int ControlPlanIndex=Integer.parseInt(tmpLine[4].trim());
                int NumOfRings=Integer.parseInt(tmpLine[5].trim());
                String PhaseAndStartTimeInRing="";
                for(int i= 6; i<tmpLine.length-1;i++){
                    PhaseAndStartTimeInRing=PhaseAndStartTimeInRing+tmpLine[i].trim()+",";
                }
                PhaseAndStartTimeInRing=PhaseAndStartTimeInRing+tmpLine[tmpLine.length-1].trim();

                AimsunSigInf aimsunSigInf=new AimsunSigInf(TimeRelative, Time, JunctionID, ControlType,ControlPlanIndex, NumOfRings, PhaseAndStartTimeInRing);
                aimsunSigInfList.add(aimsunSigInf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return aimsunSigInfList;
    }

    /**
     *
     * @param con Database connection
     * @param ReplicationName Replication name
     * @param aimsunSigInfList List<AimsunSigInf> class
     */
    public static void SaveSigInf(Connection con, String ReplicationName, List<AimsunSigInf> aimsunSigInfList){
        // This function is used to save signal information to database

        String TableName="simulated_signal_"+ReplicationName;
        String Header="insert into "+TableName;
        List<String> sqlStatements=new ArrayList<String>();
        HashSet<String> stringHashSet= new HashSet<String>();; // Create the hash set
        for(int i=0;i<aimsunSigInfList.size();i++){
            AimsunSigInf aimsunSigInf=aimsunSigInfList.get(i);
            String sql=Header+" values (\""+aimsunSigInf.getTimeRelative()+"\",\""+aimsunSigInf.getTime()+"\",\""+
                    aimsunSigInf.getJunctionID()+"\",\""+aimsunSigInf.getControlType()+"\",\""+aimsunSigInf.getControlPlanIndex()+"\",\""+
                    aimsunSigInf.getNumOfRings()+"\",\""+aimsunSigInf.getPhaseAndStartTimeInRing()+"\");";
            if(stringHashSet.add(aimsunSigInf.getTime()+"-"+aimsunSigInf.getJunctionID()+"-"+aimsunSigInf.getControlType()+"-"+aimsunSigInf.getControlPlanIndex())) {
                sqlStatements.add(sql);
            }
        }

        try{
            Statement ps=con.createStatement();
            util.insertSQLBatch(ps, sqlStatements, 1000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
