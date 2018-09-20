package dataProvider;

import commonClass.forEstimation.*;
import commonClass.query.*;
import commonClass.parameters.*;
import util.util;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class loadEstimationResults {

    public static void LoadEstimationResultsToDatabase(Connection con, String TableName, List<TrafficState> trafficStateList){
        // This function is used to load estimation results to the database

        long timeStamp =ConvertCurrentDateTimeToInteger();

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
                sql=sql+SQLThreshold+"\",\"";

                String paramsString=ConstructStringFromParameters(trafficState.getParameters());
                sql=sql+paramsString+"\");";

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

    public static String ConstructStringFromParameters(Parameters parameters){
        // This function is used to construct string from parameters

        String parameterString="";

        // Construct vehicle parameters
        VehicleParams vehicleParams=parameters.getVehicleParams();
        String vehParameters="vehicleParams{VehicleLength="+vehicleParams.getVehicleLength()+",StartupLostTime="
                +vehicleParams.getStartupLostTime()+",JamSpacing="+vehicleParams.getJamSpacing()+"}";
        parameterString=parameterString+vehParameters+";";

        // Construct intersection parameters
        IntersectionParams intersectionParams=parameters.getIntersectionParams();
        String intParameters="intersectionParams{SaturationHeadway="+intersectionParams.getSaturationHeadway()+",SaturationSpeedLeft="+
                intersectionParams.getSaturationSpeedLeft()+",SaturationSpeedThrough="+intersectionParams.getSaturationSpeedThrough()+
                ",SaturationSpeedRight="+intersectionParams.getSaturationSpeedRight()+",DistanceAdvanceDetector="+
                intersectionParams.getDistanceAdvanceDetector()+",LeftTurnPocket="+intersectionParams.getLeftTurnPocket()+
                ",RightTurnPocket="+intersectionParams.getRightTurnPocket()+",DistanceToEnd="+intersectionParams.getDistanceToEnd()+"}";
        parameterString=parameterString+intParameters+";";

        // Construct signal settings
        SignalSettings signalSettings=parameters.getSignalSettings();
        String signalParams="signalSettings{CycleLength="+signalSettings.getCycleLength()+",LeftTurnGreen="+signalSettings.getLeftTurnGreen()+
                ",ThroughGreen="+signalSettings.getThroughGreen()+",RightTurnGreen="+signalSettings.getRightTurnGreen()+
                ",LeftTurnSetting="+signalSettings.getLeftTurnSetting()+"}";
        parameterString=parameterString+signalParams+";";

        // Construct turning proportions
        TurningProportion turningPrortions=parameters.getTurningProportion();
        String turningParams="turningProportions{LeftTurn="+ArrayToString(turningPrortions.getLeftTurn())+",AdvanceLeftTurn="+
                ArrayToString(turningPrortions.getAdvanceLeftTurn())+",RightTurn="+ArrayToString(turningPrortions.getRightTurn())+
                ",AdvanceRightTurn="+ArrayToString(turningPrortions.getAdvanceRightTurn())+",Advance="+
                ArrayToString(turningPrortions.getAdvance())+",AllMovements="+ArrayToString(turningPrortions.getAllMovements())+
                ",AdvanceThrough="+ArrayToString(turningPrortions.getAdvanceThrough())+",Through="+ArrayToString(turningPrortions.getThrough())+
                ",AdvanceLeftAndThrough="+ArrayToString(turningPrortions.getAdvanceLeftAndThrough())+",LeftAndThrough="+
                ArrayToString(turningPrortions.getLeftAndThrough())+",AdvanceLeftAndRight="+ArrayToString(turningPrortions.getAdvanceLeftAndRight())+
                ",LeftAndRight="+ArrayToString(turningPrortions.getLeftAndRight())+",AdvanceThroughAndRight="+
                ArrayToString(turningPrortions.getAdvanceThroughAndRight())+",ThroughAndRight="+ArrayToString(turningPrortions.getThroughAndRight())+"}";
        parameterString=parameterString+turningParams+";";

        // Construct estimation parameters
        EstimationParams estimationParams=parameters.getEstimationParams();
        String estimationString="estimationParams{FFSpeedForAdvDet="+estimationParams.getFFSpeedForAdvDet()+",OccThresholdForAdvDet="+
                estimationParams.getOccThresholdForAdvDet()+"}";
        parameterString=parameterString+estimationString+";";

        return parameterString;
    }

    public static String ArrayToString(double []InputArray){
        // This function is convert array to string
        String OutputString="";
        for(int i=0;i<InputArray.length-1;i++){
            OutputString=OutputString+InputArray[i]+"-";
        }
        OutputString=OutputString+InputArray[InputArray.length-1];
        return OutputString;
    }

    public static String ConstructStringFromDetectorState(List<TrafficStateByDetectorType> trafficStateByDetectorTypeList){
        // This function is construct string for detector state

        String SQL="";
        for(int j=0;j<trafficStateByDetectorTypeList.size();j++){
            String tmpSQL=trafficStateByDetectorTypeList.get(j).getDetectorType()+";"+trafficStateByDetectorTypeList.get(j).getRate()+
                    ";"+trafficStateByDetectorTypeList.get(j).getAvgOcc()+";"+trafficStateByDetectorTypeList.get(j).getAvgFlow()+";"+
                    trafficStateByDetectorTypeList.get(j).getTotLanes()+"&";
            SQL=SQL+tmpSQL;
        }
        return SQL;
    }

    public static long ConvertCurrentDateTimeToInteger(){
        // This function is used to convert current date & time into an integer

        Calendar cal = Calendar.getInstance();
        long Year=cal.get(Calendar.YEAR);
        long Month=cal.get(Calendar.MONTH)+1; // Starts from zero, need to add 1
        long Day=cal.get(Calendar.DAY_OF_MONTH);
        long Hour=cal.get(Calendar.HOUR_OF_DAY);
        long Minute=cal.get(Calendar.MINUTE);
        long Second=cal.get(Calendar.SECOND);

        long DateTime=(((Year*10000+Month*100+Day)*100+Hour)*100+Minute)*100+Second;
        return DateTime;
    }
}
