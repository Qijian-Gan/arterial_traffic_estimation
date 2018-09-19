package initialization;

import dataProvider.getSimulationData;
import dataProvider.getSimulationData.*;
import dataProvider.loadSimulationData;
import commonClass.forInitialization.*;
import networkInput.reconstructNetwork;
import networkInput.reconstructNetwork.*;

import java.util.ArrayList;
import java.util.List;

public class generateVehiclesWithSimulation {

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> ForSingleJunction(AimsunApproach aimsunApproach, SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles from the backup simulations in Aimsun

        List<SimVehicle> simVehicleList=VehicleGenerationFromSimulation(aimsunApproach,
                simulationStatistics.getSimVehInfBySectionList());
        return simVehicleList;
    }

    /**
     *
     * @param aimsunApproach AimsunApproach class
     * @param simVehInfBySectionList List<SimVehInfBySection> class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGenerationFromSimulation(AimsunApproach aimsunApproach,
                                                                   List<SimVehInfBySection> simVehInfBySectionList){
        // This function is used to generate vehicles using simulation results

        // Columns in the MySQl Table:
        // Time, VehicleID, VehicleType, SectionID, LaneID, CurrentPosition, CurrentSpeed, CentroidOrigin,
        // CentroidDestination, DistanceToEnd
        List<SimVehicle> simVehicleList=new ArrayList<SimVehicle>();

        int JunctionID=aimsunApproach.getJunctionID();
        int FirstSectionID=aimsunApproach.getFirstSectionID();
        List<Integer> ListOfSections=aimsunApproach.getSectionBelongToApproach().getListOfSections();

        // Select the vehicle trajectories within the ListOfSections
        List<getSimulationData.SimVehInfBySection> SelectedSimVehInf=new ArrayList<getSimulationData.SimVehInfBySection>();
        for(int i=0;i<ListOfSections.size();i++){// Loop for each section
            for(int j=0;j<simVehInfBySectionList.size();j++){ // Loop for the available set of sections
                if(simVehInfBySectionList.get(j).getSectionID()==ListOfSections.get(i)){// Find the right one
                    SelectedSimVehInf.add(simVehInfBySectionList.get(j)); // Append it to the end
                    break;
                }
            }
        }

        // Generate simulated vehicles
        if(SelectedSimVehInf.size()==0){
            System.out.println("No simulation data available for the approach with Junction:"+JunctionID+
                    " and Section:"+FirstSectionID);
        }else{
            for(int i=0;i<SelectedSimVehInf.size();i++){ // Loop for each section
                List<loadSimulationData.AimsunVehInf> tmpSimVehicleList=SelectedSimVehInf.get(i).getAimsunVehInfList();
                for(int j=0;j<SelectedSimVehInf.get(i).getAimsunVehInfList().size();j++){ // Loop for each vehicle
                    int SectionID=tmpSimVehicleList.get(j).getSectionID();
                    int LaneID=tmpSimVehicleList.get(j).getLaneID();
                    int VehicleTypeInAimsun=tmpSimVehicleList.get(j).getVehicleType();
                    int OriginCentroid=tmpSimVehicleList.get(j).getCentroidOrigin();
                    int DestinationCentroid=tmpSimVehicleList.get(j).getCentroidDestination();
                    double InitialPosition=tmpSimVehicleList.get(j).getCurrentPosition();
                    double InitialSpeed=tmpSimVehicleList.get(j).getCurrentSpeed();
                    boolean TrackOrNot=false; // Do not track vehicles

                    SimVehicle simVehicle=new SimVehicle(SectionID,LaneID,VehicleTypeInAimsun,OriginCentroid,DestinationCentroid
                            ,InitialPosition,InitialSpeed,TrackOrNot);
                    simVehicleList.add(simVehicle);
                }
            }
        }
        return simVehicleList;
    }

}
