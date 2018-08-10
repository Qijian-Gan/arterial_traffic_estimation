package dataProvider;

import java.sql.Connection;
import java.util.*;
import java.sql.*;

import com.intellij.util.Function;
import estimation.trafficStateEstimation.*;

public class getEstimationResults {

    public static class EstimationResults{
        public EstimationResults(int _JunctionID, String _JunctionName, int _FirstSectionID, String _FirstSectionName, int _Time,
                                 AssessmentStateAndQueue _assessmentStateAndQueue,Parameters _parameters, long _UpdateDateTime){
            this.JunctionID=_JunctionID;
            this.JunctionName=_JunctionName;
            this.FirstSectionID=_FirstSectionID;
            this.FirstSectionName=_FirstSectionName;
            this.Time=_Time;
            this.assessmentStateAndQueue=_assessmentStateAndQueue;
            this.parameters=_parameters;
            this.UpdateDateTime=_UpdateDateTime;
        }
        protected int JunctionID;
        protected String JunctionName;
        protected int FirstSectionID;
        protected String FirstSectionName;
        protected int Time;
        protected AssessmentStateAndQueue assessmentStateAndQueue;
        protected Parameters parameters;
        protected long UpdateDateTime;

        public AssessmentStateAndQueue getAssessmentStateAndQueue() {
            return assessmentStateAndQueue;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public int getTime() {
            return Time;
        }
    }

    public static Map<Integer,EstimationResults> GetEstimationResultsFromDatabase(Connection conAimsunSimulation, String TableName,
                                                                           QueryMeasures queryMeasures){
        // This function is used to get estimation results from database


        Map<Integer, EstimationResults> estimationResultsMap = new HashMap<Integer, EstimationResults>();
        Map<Integer, Long> estimationDateTimeMap = new HashMap<Integer, Long>();
        HashSet<Integer> uniqueFirstSectionID=new HashSet<Integer>();
        // Construct the query measure string
        String queryMeasuresSQL="\"Year="+queryMeasures.getYear()+";Month="+queryMeasures.getMonth()+";Day="
                +queryMeasures.getDay()+";DayOfWeek="+queryMeasures.getDayOfWeek()+";Median="
                +queryMeasures.isMedian()+";Interval="+queryMeasures.getInterval()
                +";StartTime="+queryMeasures.getTimeOfDay()[0]+";EndTime="+queryMeasures.getTimeOfDay()[1]+"\"";
        // Search
        String sql="select JunctionID, JunctionName, FirstSectionID, FirstSectionName, Time, StateByMovement,QueueByMovement," +
                "Params,UpdateDateTime from "+
                TableName+ " where QueryMeasures="+queryMeasuresSQL+";";
        try {
            Statement ps=conAimsunSimulation.createStatement();
            ResultSet resultSet=ps.executeQuery(sql);
            while (resultSet.next()) {
                int JunctionID=resultSet.getInt("JunctionID");
                String JunctionName=resultSet.getString("JunctionName");
                int FirstSectionID=resultSet.getInt("FirstSectionID");
                String FirstSectionName=resultSet.getString("FirstSectionName");
                int Time=resultSet.getInt("Time");

                String [] StateByMovement=resultSet.getString("StateByMovement").split(";");
                String [] QueueByMovement=resultSet.getString("QueueByMovement").split(";");
                int[] QueueByMovementInteger=new int[]{Integer.parseInt(QueueByMovement[0]),Integer.parseInt(QueueByMovement[1]),
                        Integer.parseInt(QueueByMovement[2])};
                AssessmentStateAndQueue assessmentStateAndQueue=new AssessmentStateAndQueue(StateByMovement,QueueByMovementInteger);

                long UpdateDateTime=resultSet.getLong("UpdateDateTime");

                String paramsString=resultSet.getString("Params");
                Parameters parameters=ConvertStringToParams(paramsString);

                EstimationResults estimationResults=new EstimationResults(JunctionID, JunctionName, FirstSectionID, FirstSectionName, Time,
                        assessmentStateAndQueue, parameters, UpdateDateTime);

                // Always get the latest estimate for a given intersection approach
                if(uniqueFirstSectionID.add(FirstSectionID)){// Can add the section
                    // New attribute
                    estimationDateTimeMap.put(FirstSectionID,UpdateDateTime);
                    estimationResultsMap.put(FirstSectionID,estimationResults);
                }else{ // Already exist FirstSectionID?
                    long UpdateDateTimePre=estimationDateTimeMap.get(FirstSectionID); // Get the previous update date and time
                    if(UpdateDateTimePre<UpdateDateTime){ // Find the latest one?
                        // Remove old ones
                        estimationResultsMap.remove(FirstSectionID);
                        estimationDateTimeMap.remove(FirstSectionID);
                        // Get the latest one
                        estimationDateTimeMap.put(FirstSectionID,UpdateDateTime);
                        estimationResultsMap.put(FirstSectionID,estimationResults);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estimationResultsMap;
    }

    public static Parameters ConvertStringToParams(String paramsString){
        // This function is used to convert string to Parameters (class)

        String [] strArray=paramsString.split(";");

        VehicleParams vehicleParams=GetVehicleParamsFromString(strArray[0].trim());
        IntersectionParams intersectionParams=GetIntersectionParamsFromString(strArray[1].trim());
        SignalSettings signalSettings=GetSignalSettingsFromString(strArray[2].trim());
        TurningProportion turningProportion=GetTurningProportionFromString(strArray[3].trim());
        EstimationParams estimationParams=GetEstimationParamsFromString(strArray[4].trim());

        Parameters parameters=new Parameters(vehicleParams,intersectionParams,signalSettings,turningProportion,estimationParams);
        return parameters;
    }

    /**
     *
     * @param InputString
     * @return VehicleParams (Class)
     */
    public static VehicleParams GetVehicleParamsFromString(String InputString){
        // This function is used to get vehicle parameters

        String [] strArray1=InputString.split("\\{");
        String [] strArray2=strArray1[1].split("}");
        String [] strArray3=strArray2[0].split(",");

        double VehicleLength=Double.parseDouble(strArray3[0].split("=")[1].trim());
        double StartupLostTime=Double.parseDouble(strArray3[1].split("=")[1].trim());
        double JamSpacing=Double.parseDouble(strArray3[2].split("=")[1].trim());

        VehicleParams vehicleParams=new VehicleParams(VehicleLength,StartupLostTime,JamSpacing);
        return vehicleParams;
    }

    /**
     *
     * @param InputString
     * @return IntersectionParams (class)
     */
    public static IntersectionParams GetIntersectionParamsFromString(String InputString){
        // This function is used to get IntersectionParams (class)

        String [] strArray1=InputString.split("\\{");
        String [] strArray2=strArray1[1].split("}");
        String [] strArray3=strArray2[0].split(",");

        double SaturationHeadway=Double.parseDouble(strArray3[0].split("=")[1].trim());
        double SaturationSpeedLeft=Double.parseDouble(strArray3[1].split("=")[1].trim());
        double SaturationSpeedThrough=Double.parseDouble(strArray3[2].split("=")[1].trim());
        double SaturationSpeedRight=Double.parseDouble(strArray3[3].split("=")[1].trim());
        double DistanceAdvanceDetector=Double.parseDouble(strArray3[4].split("=")[1].trim());
        double LeftTurnPocket=Double.parseDouble(strArray3[5].split("=")[1].trim());
        double RightTurnPocket=Double.parseDouble(strArray3[6].split("=")[1].trim());
        double DistanceToEnd=Double.parseDouble(strArray3[7].split("=")[1].trim());

        IntersectionParams intersectionParams=new IntersectionParams(SaturationHeadway,SaturationSpeedLeft, SaturationSpeedRight,
                SaturationSpeedThrough,DistanceAdvanceDetector,LeftTurnPocket,RightTurnPocket,DistanceToEnd);
        return intersectionParams;
    }

    /**
     *
     * @param InputString
     * @return SignalSettings (class)
     */
    public static SignalSettings GetSignalSettingsFromString(String InputString){
        // This function is used to get signal settings

        String [] strArray1=InputString.split("\\{");
        String [] strArray2=strArray1[1].split("}");
        String [] strArray3=strArray2[0].split(",");

        int CycleLength=Integer.parseInt(strArray3[0].split("=")[1].trim());
        int LeftTurnGreen=Integer.parseInt(strArray3[1].split("=")[1].trim());
        int ThroughGreen=Integer.parseInt(strArray3[2].split("=")[1].trim());
        int RightTurnGreen=Integer.parseInt(strArray3[3].split("=")[1].trim());
        String LeftTurnSetting=strArray3[4].split("=")[1].trim();

        SignalSettings signalSettings=new SignalSettings(CycleLength,LeftTurnGreen,ThroughGreen,RightTurnGreen,LeftTurnSetting);
        return signalSettings;
    }

    /**
     *
     * @param InputString
     * @return EstimationParams (class)
     */
    public static EstimationParams GetEstimationParamsFromString(String InputString){
        // This function is used to get estimation parameters

        String [] strArray1=InputString.split("\\{");
        String [] strArray2=strArray1[1].split("}");
        String [] strArray3=strArray2[0].split(",");

        double FFSpeedForAdvDet=Double.parseDouble(strArray3[0].split("=")[1].trim());
        double OccThresholdForAdvDet=Double.parseDouble(strArray3[1].split("=")[1].trim());

        EstimationParams estimationParams=new EstimationParams(FFSpeedForAdvDet,OccThresholdForAdvDet);
        return estimationParams;
    }

    /**
     *
     * @param InputString
     * @return TurningProportion (class)
     */
    public static TurningProportion GetTurningProportionFromString(String InputString){
        // This function is used to get turning proportions

        String [] strArray1=InputString.split("\\{");
        String [] strArray2=strArray1[1].split("}");
        String [] strArray3=strArray2[0].split(",");

        double[] LeftTurn=GetRatiosFromString(strArray3[0].split("=")[1].trim());
        double[] LeftTurnQueue=LeftTurn;
        double[] AdvanceLeftTurn=GetRatiosFromString(strArray3[1].split("=")[1].trim());
        double[] RightTurn=GetRatiosFromString(strArray3[2].split("=")[1].trim());
        double[] RightTurnQueue=RightTurn;
        double[] AdvanceRightTurn=GetRatiosFromString(strArray3[3].split("=")[1].trim());
        double[] Advance=GetRatiosFromString(strArray3[4].split("=")[1].trim());
        double[] AllMovements=GetRatiosFromString(strArray3[5].split("=")[1].trim());
        double[] AdvanceThrough=GetRatiosFromString(strArray3[6].split("=")[1].trim());
        double[] Through=GetRatiosFromString(strArray3[7].split("=")[1].trim());
        double[] AdvanceLeftAndThrough=GetRatiosFromString(strArray3[8].split("=")[1].trim());
        double[] LeftAndThrough=GetRatiosFromString(strArray3[9].split("=")[1].trim());
        double[] AdvanceLeftAndRight=GetRatiosFromString(strArray3[10].split("=")[1].trim());
        double[] LeftAndRight=GetRatiosFromString(strArray3[11].split("=")[1].trim());
        double[] AdvanceThroughAndRight=GetRatiosFromString(strArray3[12].split("=")[1].trim());
        double[] ThroughAndRight=GetRatiosFromString(strArray3[13].split("=")[1].trim());

        TurningProportion turningProportion=new TurningProportion(LeftTurn,LeftTurnQueue,AdvanceLeftTurn,
                RightTurn,RightTurnQueue,AdvanceRightTurn,Advance,AllMovements,AdvanceThrough,
                Through,AdvanceLeftAndThrough,LeftAndThrough,AdvanceLeftAndRight,LeftAndRight,
                AdvanceThroughAndRight,ThroughAndRight);
        return turningProportion;
    }

    /**
     *
     * @param InputString
     * @return double[] TurningRatios
     */
    public static double[] GetRatiosFromString(String InputString){
        // This function is used to reconstruct the turning proportions

        String [] strArray=InputString.split("-");
        double[] TurningRatios=new double[]{Double.parseDouble(strArray[0].trim()),Double.parseDouble(strArray[1].trim()),
                Double.parseDouble(strArray[2].trim())};
        return TurningRatios;
    }
}
