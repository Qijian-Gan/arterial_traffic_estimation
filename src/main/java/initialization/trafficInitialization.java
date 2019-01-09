package initialization;

/**
 * Created by Qijian-Gan on 5/23/2018.
 */

import commonClass.forInitialization.*;
import commonClass.forGeneralNetwork.approach.*;
import commonClass.forAimsunNetwork.signalControl.*;
import commonClass.estimationData.*;
import commonClass.simulationData.SimulationStatistics;

import java.util.*;

public class trafficInitialization {

    //***************************************************************************************************************
    //************************** Main Function: Vehicle Generation  *************************************************
    //***************************************************************************************************************
    /**
     *
     * @param aimsunNetworkByApproach List<AimsunApproach>
     * @param estimationResultsList Map<Integer,EstimationResults> <FirstSectionID, EstimationResults>
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @param simulationStatistics SimulationStatistics class
     * @return List<SimVehicle> class
     */
    public static List<SimVehicle> VehicleGeneration(List<AimsunApproach> aimsunNetworkByApproach, Map<Integer,EstimationResults>
            estimationResultsList,List<AimsunControlPlanJunction> ActiveControlPlans, SimulationStatistics simulationStatistics){
        // This function is used to generate vehicles for Aimsun

        List<SimVehicle> simVehicleListEst=new ArrayList<SimVehicle>(); //Vehicles generated from estimation
        List<SimVehicle> simVehicleListSim=new ArrayList<SimVehicle>(); //Vehicles generated from simulation

        if(aimsunNetworkByApproach.size()>0){
            for(int i=0;i<aimsunNetworkByApproach.size();i++){// Loop for each approach
                System.out.println("Junction ID="+ aimsunNetworkByApproach.get(i).getJunctionID()+", Junction Name="+
                        aimsunNetworkByApproach.get(i).getJunctionName()+ ", First Section ID="+aimsunNetworkByApproach.get(i).getFirstSectionID());
                if(aimsunNetworkByApproach.get(i).getSignalized()==1){ // if it is a signalized junction
                    int FirstSectionID=aimsunNetworkByApproach.get(i).getFirstSectionID(); // Get the first section in the downstream
                    //if(FirstSectionID!=21931) continue;;
                    if(estimationResultsList.containsKey(FirstSectionID)){
                        // If we have estimated queues(non-negative values: -1 means no information; -2 means no such a movement)
                        List<SimVehicle> tmpListSimVehicle=generateVehiclesWithEstimation.ForSignalizedJunction(aimsunNetworkByApproach.get(i),
                                estimationResultsList.get(FirstSectionID),ActiveControlPlans,simulationStatistics);
                        simVehicleListEst.addAll(tmpListSimVehicle);
                    }else{// When no queue estimates, use the simulated traffic states from Aimsun instead
                        List<SimVehicle> tmpListSimVehicle=generateVehiclesWithSimulation.ForSingleJunction(aimsunNetworkByApproach.get(i),
                                simulationStatistics);
                        simVehicleListSim.addAll(tmpListSimVehicle);
                    }
                }
                else{// If it is not a signalized junction, e.g., freeway junction
                    // If BEATs simulation is not available, use the Aimsun simulation results
                    List<SimVehicle> tmpListSimVehicle=generateVehiclesWithSimulation.ForSingleJunction(aimsunNetworkByApproach.get(i),
                            simulationStatistics);
                    simVehicleListSim.addAll(tmpListSimVehicle);
                }
            }
        }

        // Re-adjust lane ID before inserting vehicles back to Aimsun
        // This is SO SAD that: Lane ID is labeled from 0 to N-1 (left to right)in GK functions, but from N to 1 (right to left) in AKI functions
        // We need to use the AKI function to insert vehicles back to Aimsun
        simVehicleListEst=laneAdjustment.ReAdjustLaneIDForSimVehicles(simVehicleListEst, aimsunNetworkByApproach); // Only for vehicles from Estimation
        simVehicleListEst.addAll(simVehicleListSim); // No need to adjust lanes for vehicles directly from simulation.

        // Return the list of sim vehicles
        return simVehicleListEst;
    }



}
