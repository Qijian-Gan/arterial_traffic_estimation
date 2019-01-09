package estimation;

import commonClass.forGeneralNetwork.*;
import commonClass.forAimsunNetwork.signalControl.*;
import commonClass.forGeneralNetwork.approach.*;
import commonClass.parameters.*;
import commonClass.query.*;
import commonClass.forEstimation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qijian-Gan on 11/29/2017.
 */
public class trafficStateEstimation {

    /**
     *
     * @param con Database connection
     * @param network AimsunNetworkByApproach class
     * @param queryMeasures QueryMeasures class
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @return List<TrafficState> class
     */
    public static List<TrafficState> arterialTrafficStateEstimation(Connection con,AimsunNetworkByApproach network
            ,QueryMeasures queryMeasures,List<AimsunControlPlanJunction> ActiveControlPlans){
        // This function is for arterial traffic state estimation

        // Run arterial estimation
        List<AimsunApproach> aimsunApproaches=network.getAimsunNetworkByApproach();
        List<TrafficState> trafficStateList=new ArrayList<TrafficState>();
        for(int i=0;i<aimsunApproaches.size();i++) {// Loop for each approach

            //if(!(aimsunApproaches.get(i).getJunctionID()==3329 ||aimsunApproaches.get(i).getJunctionID()==3344 ||aimsunApproaches.get(i).getJunctionID()==3370
            //        ||aimsunApproaches.get(i).getJunctionID()==3341 || aimsunApproaches.get(i).getJunctionID()==3369))
            //    continue;

            System.out.println("i="+i+" With Junction ID="+aimsunApproaches.get(i).getJunctionID()+", Name="+
                    aimsunApproaches.get(i).getJunctionName()+", Section ID="+aimsunApproaches.get(i).getFirstSectionID());

            AimsunApproach aimsunApproach=aimsunApproaches.get(i);

            // Get default parameters
            Parameters parameters=parameterOperations.getDefaultParameters();

            // *****************Update the parameter settings******************
            // Update turning proportions
            parameters.setTurningProportion(parameterOperations.UpdateVehicleProportions(parameters.getTurningProportion(),
                    con,aimsunApproach, queryMeasures));
            parameters.setTurningProportion(parameterOperations.UpdateVehicleProportionsAccordingToLandIndicator(
                    parameters.getTurningProportion(),aimsunApproach.getGeoDesign().getTurnIndicator()));

            // Update saturation speeds
            parameters=parameterOperations.UpdateSaturationSpeeds(parameters,aimsunApproach);

            // Update signal settings and the saturation speed (permitted phases)
            parameters=parameterOperations.UpdateSignalSettings(parameters,ActiveControlPlans,aimsunApproach);

            // *****************Decision making******************************
            TrafficStateByApproach trafficStateByApproach=determineTrafficStates.StateIdentification(con,aimsunApproach,queryMeasures,parameters);

            trafficStateByApproach=assessStateAndQueue.Assessment(aimsunApproach,parameters,trafficStateByApproach);

            // *****************A new traffic state******************************
            TrafficState trafficState=new TrafficState(aimsunApproach,parameters,queryMeasures,trafficStateByApproach);
            trafficStateList.add(trafficState);
        }

        return trafficStateList;
    }
}
