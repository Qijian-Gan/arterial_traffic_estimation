package dataProvider;

import estimation.trafficStateEstimation.*;
import util.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;

public class loadEstimationResults {

    public static void LoadEstimationResultsToDatabase(Connection con, String TableName, List<TrafficState> trafficStateList){
        // This function is used to load estimation results to the database

        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

        List<String> SQLStrings=new ArrayList<String>();
        for(int i=0;i<trafficStateList.size();i++){ // Loop for each intersection approach
            if(trafficStateList.get(i).getTrafficStateByApproach().getQueueThreshold()!=null){
                TrafficState trafficState=trafficStateList.get(i);
                String sql="insert into "+TableName+" values(\""+trafficState.getAimsunApproach().getJunctionID()+"\",\""+
                        trafficState.getAimsunApproach().getJunctionName()+"\",\""+
                        trafficState.getAimsunApproach().getFirstSectionID()+"\",\""+
                        trafficState.getAimsunApproach().getFirstSectionName()+"\",\""+
                        trafficState.getAimsunApproach().getGeoDesign().getLinkLength()+"\",\""+
                        trafficState.getAimsunApproach().getGeoDesign().getNumOfUpstreamLanes()+"\",\""+
                        (trafficState.getAimsunApproach().getGeoDesign().getNumOfDownstreamLanes()+
                        trafficState.getAimsunApproach().getGeoDesign().getExclusiveLeftTurn().getNumLanes()+
                        trafficState.getAimsunApproach().getGeoDesign().getExclusiveRightTurn().getNumLanes())+"\",\"";

                // Time
                sql=sql+trafficState.getTrafficStateByApproach().getTime()+"\",\"";

                // State by movement
                String[] stateByMovement=trafficState.getTrafficStateByApproach().getStateByMovement();
                String SQLState=stateByMovement[0]+";"+stateByMovement[1]+";"+stateByMovement[2];
                sql=sql+SQLState+"\",\"";

                //Queue by movement
                int[] queueByMovement=trafficState.getTrafficStateByApproach().getQueueByMovement();
                String SQLQueue=queueByMovement[0]+";"+queueByMovement[1]+";"+queueByMovement[2];
                sql=sql+SQLQueue+"\",\"";

                sql=sql+timeStamp+"\",\"";

                // Query measures
                QueryMeasures queryMeasures=trafficState.getQueryMeasures();
                String queryMeasuresSQL="Year="+queryMeasures.getYear()+";Month="+queryMeasures.getMonth()+";Day="
                        +queryMeasures.getDay()+";DayOfWeek="+queryMeasures.getDayOfWeek()+";Median="
                        +queryMeasures.isMedian()+";Interval="+queryMeasures.getInterval()
                        +";StartTime="+queryMeasures.getTimeOfDay()[0]+";EndTime="+queryMeasures.getTimeOfDay()[1];
                sql=sql+queryMeasuresSQL+"\",\"";

                // Advance detectors
                List<TrafficStateByDetectorType> advanceDetectors=trafficState.getTrafficStateByApproach().getAdvanceDetectors();
                String SQLAdvance=ConstructStringFromDetectorState(advanceDetectors);
                sql=sql+SQLAdvance+"\",\"";

                // Exclusive left-turn detectors
                List<TrafficStateByDetectorType> exclusiveLTDetectors=trafficState.getTrafficStateByApproach().getExclusiveLeftTurnDetectors();
                String SQLLT=ConstructStringFromDetectorState(exclusiveLTDetectors);
                sql=sql+SQLLT+"\",\"";

                // Exclusive right-turn detectors
                List<TrafficStateByDetectorType> exclusiveRTDetectors=trafficState.getTrafficStateByApproach().getExclusiveRightTurnDetectors();
                String SQLRT=ConstructStringFromDetectorState(exclusiveRTDetectors);
                sql=sql+SQLRT+"\",\"";

                // Exclusive general stopbar detectors
                List<TrafficStateByDetectorType> generalStopbarDetectors=trafficState.getTrafficStateByApproach().getGeneralStopbarDetectors();
                String SQLGP=ConstructStringFromDetectorState(generalStopbarDetectors);
                sql=sql+SQLGP+"\",\"";

                // Queue thresholds
                QueueThreshold queueThreshold=trafficState.getTrafficStateByApproach().getQueueThreshold();
                String SQLThreshold=queueThreshold.getQueueThresholdLeft().getQueueToAdvance()+";"+
                        queueThreshold.getQueueThresholdLeft().getQueueWithMaxGreen()+";"+
                        queueThreshold.getQueueThresholdLeft().getQueueToEnd()+"&";
                SQLThreshold=SQLThreshold+queueThreshold.getQueueThresholdThrough().getQueueToAdvance()+";"+
                        queueThreshold.getQueueThresholdThrough().getQueueWithMaxGreen()+";"+
                        queueThreshold.getQueueThresholdThrough().getQueueToEnd()+"&";
                SQLThreshold=SQLThreshold+queueThreshold.getQueueThresholdRight().getQueueToAdvance()+";"+
                        queueThreshold.getQueueThresholdRight().getQueueWithMaxGreen()+";"+
                        queueThreshold.getQueueThresholdRight().getQueueToEnd()+"&";
                sql=sql+SQLThreshold+"\");";

                SQLStrings.add(sql);
            }
        }

        try{
            Statement ps=con.createStatement();
            util.insertSQLBatch(ps, SQLStrings, 10000);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static String ConstructStringFromDetectorState(List<TrafficStateByDetectorType> trafficStateByDetectorTypeList){

        String SQL="";
        for(int j=0;j<trafficStateByDetectorTypeList.size();j++){
            String tmpSQL=trafficStateByDetectorTypeList.get(j).getDetectorType()+";"+trafficStateByDetectorTypeList.get(j).getRate()+
                    ";"+trafficStateByDetectorTypeList.get(j).getAvgOcc()+";"+trafficStateByDetectorTypeList.get(j).getAvgFlow()+";"+
                    trafficStateByDetectorTypeList.get(j).getTotLanes()+"&";
            SQL=SQL+tmpSQL;
        }
        return SQL;
    }
}
