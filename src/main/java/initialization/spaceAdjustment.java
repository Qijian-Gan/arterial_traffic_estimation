package initialization;

import commonClass.forInitialization.SimVehicle;
import commonClass.parameters.*;

import java.util.*;

public class spaceAdjustment {

    /**
     *
     * @param TypeForIndividualVehicle Vehicle type
     * @param SpacingAndSpeed double[spacing, speed]
     * @param parameters Parameters class
     * @param Threshold Threshold for assignment
     * @return double[spacing, speed]
     */
    public static double[] ForDifferentVehicleTypes(int TypeForIndividualVehicle, double[] SpacingAndSpeed
            ,Parameters parameters, double Threshold){
        // This function is used to adjust spacing for different vehicle types

        double pce;// Passenger car equivalent
        if(TypeForIndividualVehicle==1){ // Regular car
            pce=1;
        }else if(TypeForIndividualVehicle>1&&TypeForIndividualVehicle<4) { // Regular trucks?
            pce=1.5;
        }else{ // Heavy duty trucks?
            pce=2.5;
        }
        SpacingAndSpeed[0]=Math.max(SpacingAndSpeed[0],parameters.getVehicleParams().getJamSpacing()*pce);
        return SpacingAndSpeed;
    }

    public static int GetVehicleTypeAhead(List<SimVehicle> tmpSimVehicleAssign, int SectionID, int LaneID){
        // This function is used to get the type of the vehicle which is ahead of the vehicle being inserted into the given section and lane

        int VehicleType=1; // Regular vehicle
        double Distance=10000;
        for(int i=0;i<tmpSimVehicleAssign.size();i++){
            if(tmpSimVehicleAssign.get(i).getSectionID()==SectionID && tmpSimVehicleAssign.get(i).getLaneID()==LaneID){
                if(tmpSimVehicleAssign.get(i).getInitialPosition()<Distance){// Try to get the leading vehicle on a given section and lane
                    Distance=tmpSimVehicleAssign.get(i).getInitialPosition();
                    VehicleType=tmpSimVehicleAssign.get(i).getVehicleTypeInAimsun();
                }
            }
        }
        return VehicleType;
    }


}
