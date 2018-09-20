package test;

import commonClass.forInitialization.SimVehicle;
import java.util.*;

public class DebugCases {

    public static boolean CheckConsistencyOfSectionAssignment(List<SimVehicle> simVehicleList){
        // This function is used to check the consistency of section assignment

        // Check incorrect assignment of section ID
        boolean Status=true;
        if(simVehicleList.size()>2){
            for(int i=2;i<simVehicleList.size();i++){
                if(simVehicleList.get(i).getSectionID()==simVehicleList.get(i-2).getSectionID() &&
                        simVehicleList.get(i).getSectionID()!=simVehicleList.get(i-1).getSectionID()){
                    Status=false;
                }
            }
        }
        return Status;
    }
}
