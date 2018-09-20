package estimation;

import commonClass.detectorData.SelectedDetectorData;
import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.query.QueryMeasures;
import dataProvider.getDetectorData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class getData {

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getExclusiveLeftTurnCounts(Connection con, AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to get left-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive left turn counts
            if(aimsunApproach.getDetectorProperty().getExclusiveLeftTurn()!=null){ // If exclusive left-turn detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getExclusiveLeftTurn().getNumLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().size();i++){
                    if(aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getMovement().equals("Left Turn")) {
                        // Only check left-turn detectors, not left-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getDetectorIDs();
                        List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getExclusiveLeftTurn().get(i).getNumberOfLanes();
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                SelectedDetectorData selectedDetectorData =
                                        getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.getDataAll()!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.isMedian()==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                    }
                                }
                            }
                            if(TotLanes!=0){ // Other detectors have data
                                TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                                TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                            }
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach class
     * @param queryMeasures QueryMeasures class
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getExclusiveRightTurnCounts(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to get right-turn counts
        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.getDetectorProperty().getExclusiveRightTurn()!=null){ // If exclusive right-turn detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getExclusiveRightTurn().getNumLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getExclusiveRightTurn().size();i++){
                    if(aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getMovement().equals("Right Turn")) {
                        // Only check right-turn detectors, not right-turn queue detectors
                        List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getDetectorIDs();
                        List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getExclusiveRightTurn().get(i).getNumberOfLanes();
                        if (DetectorIDs != null) {// If detector IDs exist
                            for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                                SelectedDetectorData selectedDetectorData =getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                                if(selectedDetectorData.getDataAll()!=null){
                                    TotLanes=TotLanes+DetectorLanes.get(j);
                                    if(queryMeasures.isMedian()==true) {//Use median?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                    }else{// Use average?
                                        TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                    }
                                }
                            }
                            if(TotLanes!=0){ // Other detectors have data
                                TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                                TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                            }
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }

    /**
     *
     * @param con Database connection
     * @param aimsunApproach AimsunApproach
     * @param queryMeasures QueryMeasures
     * @return double [] {TotCounts,TotLanes}
     */
    public static double [] getAdvanceCounts(Connection con,AimsunApproach aimsunApproach, QueryMeasures queryMeasures){
        // This function is used to get flow counts from Advance detectors; rescaled flow counts if no enough coverage

        double TotCounts=0;
        int TotLanes=0;
        Statement ps;
        try{
            // Create the statement
            ps= con.createStatement();
            // Get exclusive right turn counts
            if(aimsunApproach.getDetectorProperty().getAdvanceDetectors()!=null){ // If Advance detectors exit
                int ActualLanes=aimsunApproach.getGeoDesign().getNumOfUpstreamLanes();

                for(int i=0;i<aimsunApproach.getDetectorProperty().getAdvanceDetectors().size();i++){
                    List<Integer> DetectorIDs = aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getDetectorIDs();
                    List<Integer> DetectorLanes=aimsunApproach.getDetectorProperty().getAdvanceDetectors().get(i).getNumberOfLanes();
                    if (DetectorIDs != null) {// If detector IDs exist
                        for (int j = 0; j < DetectorIDs.size(); j++) { // Loop for each detector
                            SelectedDetectorData selectedDetectorData =getDetectorData.getDataForGivenDetector(ps, DetectorIDs.get(j), queryMeasures);
                            if(selectedDetectorData.getDataAll()!=null){
                                TotLanes=TotLanes+DetectorLanes.get(j);
                                if(queryMeasures.isMedian()==true) {//Use median?
                                    TotCounts = TotCounts + selectedDetectorData.getFlowOccMid()[0];
                                }else{// Use average?
                                    TotCounts = TotCounts + selectedDetectorData.getFlowOCCAvg()[0];
                                }
                            }
                        }
                        if(TotLanes!=0){// Rescale the flow
                            // Consider bad detectors or incomplete detector coverage
                            TotCounts=TotCounts*((double) ActualLanes/Math.min(TotLanes,ActualLanes));
                            TotLanes=Math.max(TotLanes,ActualLanes); // Add more lanes
                        }
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new double [] {TotCounts,TotLanes};
    }


}
