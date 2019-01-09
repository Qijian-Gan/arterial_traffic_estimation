package writer;

import java.util.*;
import java.io.*;

import commonClass.bluetoothData.TravelTimeStatistics;
import commonClass.forInitialization.*;
import commonClass.query.QueryMeasures;

public class writingFunctions {

    // This is the class for writing functions

    public static void WriteSimVehicleToCSVForAimsun(List<SimVehicle> simVehicleList, String OutputFolder){
        // This function is to write sim vehicles to the csv format for Aimsun initialization

        File fileDir = new File(OutputFolder);
        File OutputFile=new File(fileDir,"VehicleInfInitialization.csv");

        try {
            FileWriter fw=new FileWriter(OutputFile);
            BufferedWriter bw= new BufferedWriter(fw);
            String str;
            for(int i=0;i<simVehicleList.size();i++){
                str=simVehicleList.get(i).getSectionID()+","+simVehicleList.get(i).getLaneID()+","+simVehicleList.get(i).getVehicleTypeInAimsun()
                        +","+simVehicleList.get(i).getOriginCentroid()+","+simVehicleList.get(i).getDestinationCentroid()
                        +","+simVehicleList.get(i).getInitialPosition()+","+simVehicleList.get(i).getInitialSpeed()+"\n";
                bw.write(str);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void WriteBluetoothTravelTimeToCSV(TravelTimeStatistics travelTimeStatistics, QueryMeasures queryMeasures, String FileName, String OutputFolder){
        // This function is to write sim vehicles to the csv format for Aimsun initialization

        File fileDir = new File(OutputFolder);
        File OutputFile=new File(fileDir,FileName+".csv");

        try {
            FileWriter fw=new FileWriter(OutputFile);
            BufferedWriter bw= new BufferedWriter(fw);
            String str;
            int StartTime=queryMeasures.getTimeOfDay()[0];
            int EndTime=queryMeasures.getTimeOfDay()[1];
            int Interval=queryMeasures.getInterval();
            boolean IsMedianOrNot=queryMeasures.isMedian();

            Map<Integer,Integer> TravelTimeList;
            if(IsMedianOrNot)
                TravelTimeList=travelTimeStatistics.getMedianTravelTime();
            else
                TravelTimeList=travelTimeStatistics.getAverageTravelTime();


            for(int time=StartTime;time<EndTime;time=time+Interval){
                int TravelTime;
                if(TravelTimeList.containsKey(time)){
                    TravelTime=TravelTimeList.get(time);
                    str=time+","+time/3600.0+","+TravelTime+"\n";
                    bw.write(str);
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
