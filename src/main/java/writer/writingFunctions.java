package writer;

import estimation.trafficInitialization.*;
import java.util.*;
import java.io.*;

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
}
