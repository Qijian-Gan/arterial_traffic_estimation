package initialization;

import commonClass.forGeneralNetwork.approach.*;
import commonClass.forGeneralNetwork.lane.*;
import commonClass.forAimsunNetwork.junction.*;
import commonClass.parameters.*;

import java.util.List;

public class rearBoundaryAdjustment {

    /**
     *
     * @param RearBoundaryByLane double [][]
     * @param Status String[] Estimated traffic states
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @return double [][] RearBoundaryByLane
     */
    public static double[][] AdjustWithState(double [][] RearBoundaryByLane, String[] Status, AimsunApproach
            aimsunApproach, Parameters parameters){
        // This function is used to adjust the rear boundaries according to the estimated traffic states,
        // particularly for left turns and right turns

        for(int i=0;i<Status.length;i++){ // Loop for each turning movement
            if(Status[i].contains("Lane Blockage")){// If we find lane blockage
                // get the lane-turning property
                List<LaneTurningProperty> laneTurningPropertyList=aimsunApproach.getLaneTurningProperty();
                int FirstSectionID=aimsunApproach.getFirstSectionID();
                LaneTurningProperty laneTurningProperty=null;
                for(int j=0;j<laneTurningPropertyList.size();j++){
                    if(laneTurningPropertyList.get(j).getSectionID()==FirstSectionID){
                        laneTurningProperty=laneTurningPropertyList.get(j);
                        break;
                    }
                }

                // Get the aimsun turning property (turns in the downstream section)
                List<AimsunTurning> aimsunTurningList=aimsunApproach.getTurningBelongToApproach().getTurningProperty();

                // Get the movement information
                String Movement="";
                if(i==0){
                    Movement="Left Turn";
                }else if(i==1){
                    Movement="Through";
                }else if(i==2){
                    Movement="Right Turn";
                }

                // Adjust the rear boundary condition (only for exclusive lanes)
                for(int j=0;j<laneTurningProperty.getLanes().size();j++){// Loop for each lane
                    if(laneTurningProperty.getLanes().get(j).getIsExclusive()==1){// If it is exclusive lane
                        int TurnID=laneTurningProperty.getLanes().get(j).getTurningMovements().get(0);// Get the turn ID
                        int LaneID=laneTurningProperty.getLanes().get(j).getLaneID(); // Get the lane ID
                        for(int k=0;k<aimsunTurningList.size();k++){ // Try to find the turn movement information
                            if(aimsunTurningList.get(k).getTurnID()==TurnID){
                                if(aimsunTurningList.get(k).getMovement().contains(Movement)){// Get the right turn with given movement
                                    double DistanceToStopbar=getFunctions.GetDistanceToStopbar(aimsunApproach,parameters); // Get the distance to stopbar
                                    RearBoundaryByLane[LaneID-1][2]=DistanceToStopbar; // Adjust the rear boundary
                                }
                            }
                        }
                    }
                }
            }
        }
        return RearBoundaryByLane;
    }

    /**
     *
     * @param RearBoundaryByLane double[][]
     * @param aimsunApproach AimsunApproach class
     * @param parameters Parameters class
     * @param SectionID Section ID
     * @return double[][] RearBoundaryByLane (Updated one)
     */
    public static double[][] ReAdjustmentForAssignment(double[][] RearBoundaryByLane,AimsunApproach aimsunApproach,Parameters parameters, int SectionID){
        // This function is used to adjust rear boundaries if the section is the most downstream one
        // It is necessary in the level-3 and level 4 assignment: not to assign vehicles within region from stopbar to advance detectors/Turning pocket

        if(SectionID==aimsunApproach.getFirstSectionID()){
            // Need to adjust the rear boundaries

            // Get the distance to stopbar
            double DistanceToStopbar=getFunctions.GetDistanceToStopbar(aimsunApproach,parameters);

            // Get the full-lane indicator
            int[] IsFullLane=aimsunApproach.getSectionBelongToApproach().getProperty().get(0).getIsFullLane(); // The first section

            // Adjust the rear boundaries
            for(int i=0;i<IsFullLane.length;i++){ // Loop for each lane
                if(IsFullLane[i]==1){// Is full lane?
                    RearBoundaryByLane[i][2]=Math.max(RearBoundaryByLane[i][2],DistanceToStopbar);// Get the maximum boundary
                }
            }
        }
        return RearBoundaryByLane;
    }


}
