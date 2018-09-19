package initialization;

import commonClass.forInitialization.*;
import networkInput.reconstructNetwork;
import networkInput.reconstructNetwork.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class laneAdjustment {

    //***************************************************************************************************************
    //*************** Lane ID Adjustment ****************************************************************************
    //***************************************************************************************************************
    /**
     *
     * @param simVehicleList List<SimVehicle> class
     * @param aimsunNetworkByApproach List<AimsunApproach> class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> ReAdjustLaneIDForSimVehicles(List<SimVehicle> simVehicleList, List<AimsunApproach> aimsunNetworkByApproach){
        // This function is used to adjust lane ID for sim vehicles
        Map<Integer,Integer> SectionNumLanesMap=GenerateSectionNumLanesMap(aimsunNetworkByApproach);

        for(int i=0;i<simVehicleList.size();i++){ // Loop for each sim vehicle
            int SectionID=simVehicleList.get(i).getSectionID();
            int CurLaneID=simVehicleList.get(i).getLaneID();
            if(SectionNumLanesMap.containsKey(SectionID)) { // Find the SectionID?
                int NumLanes = SectionNumLanesMap.get(SectionID); // Get the number of lanes
                int LaneIDAimsun = NumLanes - CurLaneID + 1; // Update the lane ID
                simVehicleList.get(i).setLaneID(LaneIDAimsun); // Reset it
            }
        }
        return simVehicleList;
    }

    /**
     *
     * @param aimsunNetworkByApproach List<AimsunApproach> aimsunNetworkByApproach class
     * @return Map<Integer,Integer> The map of section-Num of Lanes
     */
    public static Map<Integer,Integer> GenerateSectionNumLanesMap(List<AimsunApproach> aimsunNetworkByApproach){
        // This function is used to generate the map of section-Num of Lanes

        Map<Integer, Integer> SectionNumLanes=new HashMap<Integer, Integer>();
        for(int i=0;i<aimsunNetworkByApproach.size();i++){ // Loop for each approach
            for(int j=0;j<aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().size();j++){// Loop for each section
                int SectionID=aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().get(j).getSectionID();
                int NumOfLanes=aimsunNetworkByApproach.get(i).getSectionBelongToApproach().getProperty().get(j).getNumLanes();
                SectionNumLanes.put(SectionID,NumOfLanes); // Create the hash map
            }
        }
        return SectionNumLanes;
    }

}
