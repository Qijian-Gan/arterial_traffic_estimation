package dataProvider;

import commonClass.bluetoothData.*;
import util.util;
import java.util.List;
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


public class loadBluetoothData {

    /**
     *
     * @param con Database connection
     * @param bluetoothFolder Folder that stores the bluetooth data
     */
    public static void LoadBluetoothTravelTimeToDatabase(Connection con, String bluetoothFolder){
        // This function is used to load vehicle information to database

        // Get the list of files
        File fileDir = new File(bluetoothFolder);
        File[] listOfFiles = fileDir.listFiles();
        for(int i=0;i<listOfFiles.length;i++){
            System.out.println("Reading: " +listOfFiles[i].getAbsoluteFile());
            List<BluetoothTravelTime> bluetoothTravelTimeList=ReadBluetoothTravelTimeFromFile(listOfFiles[i].getAbsoluteFile());
            System.out.println("Saving: "+listOfFiles[i].getAbsoluteFile());
            SaveBluetoothTravelTimes(con, bluetoothTravelTimeList);
        }
    }

    /**
     *
     * @param BluetoothFolder Folder that stores bluetooth data
     * @return  List<BluetoothTravelTime>: List of BluetoothTravelTime class
     */
    public static List<BluetoothTravelTime> ReadBluetoothTravelTimeFromFile(File BluetoothFolder){
        // Read the Bluetooth travel time from files

        List<BluetoothTravelTime> bluetoothTravelTimeList=new ArrayList<BluetoothTravelTime>();

        BufferedReader br = null ;
        try {
            br = new BufferedReader(new FileReader(BluetoothFolder));
        } catch (FileNotFoundException e) {
            System.out.println("The Bluetooth travel time file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            line = br.readLine(); // Get rid of the header
            while ((line = br.readLine()) != null) {
                String [] tmpLine=line.split(",");
                String BluetoothID=tmpLine[0].trim();
                String LocationA=tmpLine[1].trim();
                String LocationB=tmpLine[2].trim();
                //Date and Time at A
                String[] DateTimeAtAStr=(tmpLine[3].trim()).split(" ");
                String DateStr=DateTimeAtAStr[0];
                String TimeStr=DateTimeAtAStr[1];
                String AMOrPM=DateTimeAtAStr[2];
                int DateAtA=util.convertDateStringToIntegerWithSlash(DateStr);
                int TimeAtA=util.convertTimeStringToSecondsWithColon(TimeStr+" "+AMOrPM);
                // Date and Time at B
                DateTimeAtAStr=(tmpLine[4].trim()).split(" ");
                DateStr=DateTimeAtAStr[0];
                TimeStr=DateTimeAtAStr[1];
                AMOrPM=DateTimeAtAStr[2];
                int DateAtB=util.convertDateStringToIntegerWithSlash(DateStr);
                int TimeAtB=util.convertTimeStringToSecondsWithColon(TimeStr+" "+AMOrPM);;

                int TravelTime=Integer.parseInt(tmpLine[5].trim());
                int UnknownC1=Integer.parseInt(tmpLine[6].trim());
                String ValidOrNot=tmpLine[7].trim();
                int UnknownC2=Integer.parseInt(tmpLine[8].trim());

                BluetoothTravelTime bluetoothTravelTime=new BluetoothTravelTime(BluetoothID,LocationA,LocationB,DateAtA,TimeAtA,DateAtB,
                        TimeAtB,TravelTime,UnknownC1,ValidOrNot,UnknownC2);
                bluetoothTravelTimeList.add(bluetoothTravelTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bluetoothTravelTimeList;
    }

    /**
     *
     * @param con Database connection
     * @param bluetoothTravelTimeList: List of BluetoothTravelTime class
     */
    public static void SaveBluetoothTravelTimes(Connection con, List<BluetoothTravelTime> bluetoothTravelTimeList){
        // This function is used to save signal information to database

        String TableName="bluetooth_travel_time";
        String Header="insert into "+TableName;
        List<String> sqlStatements=new ArrayList<String>();
        HashSet<String> stringHashSet= new HashSet<String>();; // Create the hash set
        for(int i=0;i<bluetoothTravelTimeList.size();i++){
            BluetoothTravelTime bluetoothTravelTime=bluetoothTravelTimeList.get(i);
            String sql=Header+" values (\""+bluetoothTravelTime.getBluetoothID()+"\",\""+bluetoothTravelTime.getLocationA()+"\",\""+
                    bluetoothTravelTime.getLocationB()+"\",\""+bluetoothTravelTime.getDateAtA()+"\",\""+bluetoothTravelTime.getTimeAtA()+
                    "\",\""+bluetoothTravelTime.getDateAtB()+"\",\""+bluetoothTravelTime.getTimeAtB()+
                    "\",\""+bluetoothTravelTime.getTravelTime()+"\",\""+bluetoothTravelTime.getUnknownC1()+
                    "\",\""+bluetoothTravelTime.getValidOrNot()+"\",\""+bluetoothTravelTime.getUnknownC2()+"\");";
            if(stringHashSet.add(bluetoothTravelTime.getBluetoothID()+"-"+bluetoothTravelTime.getLocationA()+"-"
                    +bluetoothTravelTime.getLocationB()+"-"+bluetoothTravelTime.getDateAtA()+"-"+bluetoothTravelTime.getTimeAtA())) {
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
