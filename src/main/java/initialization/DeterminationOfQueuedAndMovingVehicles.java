package initialization;

import dataProvider.getSignalData;
import dataProvider.getSignalData.*;
import estimation.trafficStateEstimation.*;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;


import java.util.List;

public class determinationOfQueuedAndMovingVehicles {

    /**
     *
     * @param EstimationTime Estimation Time
     * @param EstimationQueues Estimation Queues int[3]
     * @param aimsunApproach AimsunApproach (class)
     * @param parameters Parameters (class)
     * @param ActiveControlPlans List<AimsunControlPlanJunction> class
     * @return QueuedAndMovingVehicles: A 2*3 Matrix: row (queued, moving), column (left turn, through, right turn)
     */
    public static int[][] AssignAccordingToPhaseInfAndCurrentTime(int EstimationTime, int[] EstimationQueues
            , AimsunApproach aimsunApproach, Parameters parameters, List<AimsunControlPlanJunction> ActiveControlPlans){
        // This function is used to assign queued and moving vehicles according to the phase information and current estimation time

        // Initialization
        // Row 1: Queue[LT,TH,RT]
        // Row 2: Moving[LT,TH,RT]
        int [][] QueuedAndMovingVehicles=new int[2][3];
        for(int i=0;i<2;i++){ // Queued & Moving
            for(int j=0;j<3;j++){ // Left-turn, Through, Right-turn
                QueuedAndMovingVehicles[i][j]=0;
            }
        }

        if(ActiveControlPlans.size()==0){
            // If no active control plans available
            for(int j=0;j<3;j++){
                if(EstimationQueues[j]>0){ // Half for queued vehicles, and the other half for moving vehicles
                    QueuedAndMovingVehicles[0][j]= (int) Math.ceil(EstimationQueues[j]*0.5); // Queued vehicles
                    QueuedAndMovingVehicles[1][j]= (int) Math.ceil(EstimationQueues[j]*0.5); // Moving Vehicles
                }
            }
        }else{
            // Get the phase information for the given approach and time with the list of active control plans
            // The control plans can be the actual ones from the field or the one extracted from Aimsun
            PhaseInfForApproach phaseInfForApproach=getSignalData.GetSignalPhasingForGivenApproachAndTimeFromAimsun
                    (aimsunApproach, EstimationTime, ActiveControlPlans,parameters);

            for(int i=0;i<EstimationQueues.length;i++){// Loop for each movement
                if(EstimationQueues[i]>0){
                    double Headway=parameters.getIntersectionParams().getSaturationHeadway();
                    // Get the signal by Movement
                    String Movement=null;
                    if(i==0){
                        Movement="Left Turn";
                    }
                    if(i==1){
                        Movement="Through";
                    }
                    if(i==2){
                        Movement="Right Turn";
                    }
                    // "Signal" is a special term in Aimsun: Turning \belongs to\ Signal \belongs to\ Phase \belongs to\ Cycle
                    // We want to know how many signals are involved in the given "Movement"
                    SignalByMovement signalByMovement=getFunctions.GetCorrespondingSigalByMovement
                            (phaseInfForApproach.getSignalByMovementList(),Movement);

                    // Get the Maximum and Minimum numbers of queued vehicles, return [min, max]
                    int [] MaxMinQueuedVehicles=DetermineMaxAndMinVehicles(EstimationQueues[i],signalByMovement,Headway);

                    // Determine the proportions of queued and moving vehicles (contains some engineering adjustments)
                    int[] NumQueuedAndMoving=DetermineQueuedAndMovingVehicles(MaxMinQueuedVehicles, signalByMovement,Headway);

                    QueuedAndMovingVehicles[0][i]=NumQueuedAndMoving[0]; // Queued vehicles
                    QueuedAndMovingVehicles[1][i]=NumQueuedAndMoving[1]; // Moving vehicles
                }
            }
        }
        return QueuedAndMovingVehicles;
    }

    /**
     *
     * @param AverageQueue Average Queue
     * @param signalByMovement SignalByMovement class
     * @param Headway Vehcle saturation headway
     * @return MaxMinQueue: int [min, max]
     */
    public static int[] DetermineMaxAndMinVehicles(int AverageQueue, SignalByMovement signalByMovement, double Headway){
        // This function is used to determine the maximum and minimum numbers of queued vehicles for a given movement

        int [] MaxMinQueue =new int[]{0,0}; // [Min, Max]

        double GreenTime=signalByMovement.getGreenTime();
        double NumVehicleByGreen=GreenTime/Headway;

        // Get the minimum number of queued vehicles
        if(NumVehicleByGreen/2<AverageQueue){
            // If the green time is not enough to clear all queued vehicles
            MaxMinQueue[0]=(int) Math.ceil((AverageQueue*2-NumVehicleByGreen)/2); // Residual queue
        }else{
            MaxMinQueue[0]=0;
        }

        // Get the maximum number of queued vehicles
        // This is a very rough estimate, assuming the maximum queue is at most twice of the average queue
        MaxMinQueue[1]=Math.min(AverageQueue*2,(int)Math.ceil(NumVehicleByGreen/2.0)+AverageQueue);
        return MaxMinQueue;
    }

    /**
     *
     * @param MaxMinQueuedVehicles int[min, max]
     * @param signalByMovement SignalByMovement class
     * @return NumQueuedAndMoving: int[queued, moving]
     */
    public static int[] DetermineQueuedAndMovingVehicles(int [] MaxMinQueuedVehicles, getSignalData.SignalByMovement signalByMovement,
                                                         double Headway){
        // This function is used to determine the proportions of queued and moving vehicles based on the signal input
        // This part contains some engineering adjustments

        int [] NumQueuedAndMoving=new int[]{0,0}; // Queued (first)& Moving (next)

        // We are assuming the number of vehicles within an approach is the maximum number of queued vehicles.
        // Depending on the signal settings, there will be a proportion of moving vehicles, and a proportion of queued ones
        if(signalByMovement.getCurrentStatus().equals("Green")){
            if(signalByMovement.getDurationSinceActivated()>signalByMovement.getGreenTime()){
                System.out.println("Wrong green time and activation duration for the given movement: "+ signalByMovement.getMovement());
                System.exit(-1);
            }else{
                double RequiredGreenTime=Headway*MaxMinQueuedVehicles[1]; // Required green time if all vehicles (max) are cleared.
                // Need to check whether the allocated green time is able to clear the queue or not
                double proportion=Math.min(signalByMovement.getDurationSinceActivated(),RequiredGreenTime)/
                        Math.min(signalByMovement.getGreenTime(),RequiredGreenTime);
                NumQueuedAndMoving[1]=(int) Math.ceil((MaxMinQueuedVehicles[1]-MaxMinQueuedVehicles[0])*proportion); // Moving
                NumQueuedAndMoving[0]=Math.max(0,MaxMinQueuedVehicles[1]-NumQueuedAndMoving[1]); // The rest are queued vehicles
            }
        }else if(signalByMovement.getCurrentStatus().equals("Red")){
            if(signalByMovement.getDurationSinceActivated()>signalByMovement.getRedTime()){
                System.out.println("Wrong red time and activation duration for the given movement: "+ signalByMovement.getMovement());
                System.exit(-1);
            }else{
                double proportion=signalByMovement.getDurationSinceActivated()/signalByMovement.getRedTime();
                NumQueuedAndMoving[0]=MaxMinQueuedVehicles[0]+(int) Math.ceil((MaxMinQueuedVehicles[1]-MaxMinQueuedVehicles[0])*proportion); // Queued
                NumQueuedAndMoving[1]=Math.max(0,MaxMinQueuedVehicles[1]-NumQueuedAndMoving[0]); // The rest are moving vehicles
            }
        }else{
            System.out.println("Unknown signal status for the given movement: "+ signalByMovement.getMovement());
            System.exit(-1);
        }
        return NumQueuedAndMoving;
    }

}
