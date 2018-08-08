package dataProvider;

import estimation.trafficStateEstimation;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Qijian-Gan on 11/30/2017.
 */

public class getDetectorData {
    // This is the function to get detector data

    public static class SelectedDetectorData{
        // This is the data profile after queries
        public SelectedDetectorData(int _DetectorID, double[][] _DataAll, CategorizedData _categorizedData,
                                    double[][] _DataAvgByTime,double[][] _DataMidByTime, double[] _FlowOccAvg,
                                    double[] _FlowOccMid,String _Health){
            this.DetectorID=_DetectorID;
            this.DataAll=_DataAll;
            this.categorizedData=_categorizedData;
            this.DataAvgByTime=_DataAvgByTime;
            this.DataMidByTime=_DataMidByTime;
            this.FlowOCCAvg=_FlowOccAvg;
            this.FlowOccMid=_FlowOccMid;
            this.Health=_Health;
        }
        protected int DetectorID; // Detector ID
        protected double [][] DataAll=null;  // Time, Flow, Occupancy (All)
        protected CategorizedData categorizedData;
        protected double [][] DataAvgByTime=null; // Time, Flow, Occupancy (Median)
        protected double [][] DataMidByTime=null; // Time, Flow, Occupancy (Average)
        protected double[] FlowOCCAvg=null; // Averaged Flow & Occupancy over all data
        protected double[] FlowOccMid=null; // Median Flow & Occupancy over all data
        protected String Health; // Health index over all data


        public String getHealth() {
            return Health;
        }

        public double[][] getDataAll() {
            return DataAll;
        }

        public double[] getFlowOCCAvg() {
            return FlowOCCAvg;
        }

        public double[] getFlowOccMid() {
            return FlowOccMid;
        }
    }

    public static class CategorizedData{
        // This is the profile of categorized data
        public CategorizedData(List<Double> _Time,List<List<Double>> _CategorizedFlow,List<List<Double>> _CategorizedOcc){
            this.Time=_Time;
            this.CategorizedFlow=_CategorizedFlow;
            this.CategorizedOcc=_CategorizedOcc;
        }
        protected List<Double> Time;
        protected List<List<Double>> CategorizedFlow;
        protected List<List<Double>> CategorizedOcc;
    }

    /**
     *
     * @param ps SQL Statement
     * @param DetectorID Detector ID
     * @param queryMeasures Query measures
     * @return SelectedDetectorData
     */
    public static SelectedDetectorData getDataForGivenDetector(Statement ps, int DetectorID, trafficStateEstimation.QueryMeasures queryMeasures){
        // This is the function to get Processed data for a given detector

        // Get the Year, Month, Day settings setting
        int Year=queryMeasures.getYear();
        int Month=queryMeasures.getMonth();
        int Day=queryMeasures.getDay();
        int StartTime=0;
        int EndTime=24*3600;
        if(Year==-1) {
            // No input of year, get the current year
            Calendar now= Calendar.getInstance();
            Year = now.get(Calendar.YEAR);
        }
        // Construct the query
        String sql="Select Year, Month, Day, Time, Volume, Occupancy from detector_data_processed_"+Year+ " where DetectorID="+DetectorID;
        String sqlHealth="Select Year, Month, Day, Health from detector_health where DetectorID="+DetectorID+" and Year="+Year;
        if(Month>0){
            sql=sql+" and Month="+Month;
            sqlHealth=sqlHealth+" and Month="+Month;
        }
        if(Day>0){
            sql=sql+" and Day="+Day;
            sqlHealth=sqlHealth+" and Day="+Day;
        }
        if(queryMeasures.getTimeOfDay()!=null){
            StartTime=queryMeasures.getTimeOfDay()[0];
            EndTime=queryMeasures.getTimeOfDay()[1];
        }
        sql=sql+" and (Time>="+StartTime+" and Time<"+EndTime+");";

        int DayOfWeek=queryMeasures.getDayOfWeek();

        double [][] DataAll=getFlowOccFromSQL(ps,sql,sqlHealth,DayOfWeek);
        String Health;
        if(DataAll!=null){
            Health="Good";
        }else{
            Health="Bad/NoData";
        }

        CategorizedData categorizedData=getDataCategorizedByTime(DataAll, StartTime, EndTime, queryMeasures.getInterval());
        double[][] DataAvgByTime=getDataMidOrAvgByTime(categorizedData,"Average");
        double[][] DataMidByTime=getDataMidOrAvgByTime(categorizedData,"Median");

        // Get average/median over all data
        double[] FlowOccAvg=null;
        double[] FlowOccMid=null;
        if(DataAll!=null) {
            List<Double> FlowAll = new ArrayList<Double>();
            List<Double> OccAll = new ArrayList<Double>();
            for (int i = 0; i < DataAll.length; i++) {
                FlowAll.add(DataAll[i][1]);
                OccAll.add(DataAll[i][2]);
            }
            FlowOccAvg = new double[]{getAverageFromList(FlowAll), getAverageFromList(OccAll)};
            FlowOccMid = new double[]{getMedianFromList(FlowAll), getMedianFromList(OccAll)};
        }

        SelectedDetectorData selectedDetectorData= new SelectedDetectorData(DetectorID,DataAll,categorizedData,
                DataAvgByTime,DataMidByTime,FlowOccAvg,FlowOccMid,Health);
        return selectedDetectorData;

    }

    /**
     *
     * @param categorizedData CategorizedData
     * @param Type Median or average
     * @return double [][] Data
     */
    public static double [][] getDataMidOrAvgByTime(CategorizedData categorizedData, String Type){
        // This function is used to get the median data ordered by time
        double [][] Data=null;
        if(categorizedData!=null){
            // Initialization
            int NumInterval=categorizedData.Time.size();
            Data=new double[NumInterval][3];
            for(int i=0;i<NumInterval;i++) {
                // Loop for each interval
                Data[i][0]=categorizedData.Time.get(i);
                if(Type.equals("Median")){
                    Data[i][1]=getMedianFromList(categorizedData.CategorizedFlow.get(i));
                    Data[i][2]=getMedianFromList(categorizedData.CategorizedOcc.get(i));
                }else if(Type.equals("Average"))
                {
                    Data[i][1]=getAverageFromList(categorizedData.CategorizedFlow.get(i));
                    Data[i][2]=getAverageFromList(categorizedData.CategorizedOcc.get(i));
                }else{
                    System.out.println("Unknown type of method!");
                    System.exit(-1);
                }
            }
        }
        return Data;
    }

    /**
     *
     * @param DataAll double [][]
     * @param StartTime Start time
     * @param EndTime End time
     * @param Interval Time interval
     * @return CategorizedData
     */
    public static CategorizedData getDataCategorizedByTime(double [][] DataAll, int StartTime,int EndTime, int Interval ){
        // This function is used to get the data categorized by Time
        if(DataAll!=null){
            List<Double> TimeList=new ArrayList<Double>();
            List<List<Double>> CategorizedFlowList=new ArrayList<List<Double>>();
            List<List<Double>> CategorizedOccList=new ArrayList<List<Double>>();

            // Initialization
            int NumInterval=(EndTime-StartTime)/Interval;
            for(int i=0;i<NumInterval;i++){// Loop for each time intervals
                List<Double> CategorizedDataFlow=new ArrayList<Double>();
                List<Double> CategorizedDataOcc=new ArrayList<Double>();
                double Time=(double) (StartTime+i*Interval); // Get the time
                for (int j=0;j<DataAll.length;j++){
                    // Loop for each observation
                    if(DataAll[j][0]>=Time &&DataAll[j][0] <Time+Interval){
                        // Within the right time window
                        CategorizedDataFlow.add(DataAll[j][1]);
                        CategorizedDataOcc.add(DataAll[j][2]);
                    }
                }
                TimeList.add(Time);
                CategorizedFlowList.add(CategorizedDataFlow);
                CategorizedOccList.add(CategorizedDataOcc);
            }

            CategorizedData categorizedData=new CategorizedData(TimeList,CategorizedFlowList,CategorizedOccList);
            return categorizedData;
        }else{
            return null;
        }
    }

    /**
     *
     * @param ps SQL statement
     * @param sql SQL string
     * @param sqlHealth SQL string for "health"
     * @param DayOfWeek Day of week
     * @return double [][] DataAll
     */
    public static double[][] getFlowOccFromSQL(Statement ps, String sql, String sqlHealth, int DayOfWeek){
        // This function is used to get the Flow and Ooc from the SQL query
        // DayOfWeek: 0:All, 1-7: Sunday to Satursday, 8: Weekday, 9: Weekend
        double [][] DataAll=null;
        List<double[]> DataList=new ArrayList<double[]>();
        try{
            ResultSet resultSetHealth = ps.executeQuery(sqlHealth);
            // Get the health indexes;
            List<int []> HealthIndex=new ArrayList<int[]>();
            while(resultSetHealth.next()){
                int Year=resultSetHealth.getInt("Year");
                int Month=resultSetHealth.getInt("Month");
                int Day=resultSetHealth.getInt("Day");
                int Health=resultSetHealth.getInt("Health");
                HealthIndex.add(new int [] {Year,Month,Day,Health});
            }
            if(HealthIndex.size()>0) {
                ResultSet resultSet = ps.executeQuery(sql);
                while (resultSet.next()) {// Loop for the result set
                    // Get year-month-day
                    int _Year = resultSet.getInt("Year");
                    int _Month = resultSet.getInt("Month");
                    int _Day = resultSet.getInt("Day");
                    double Time = (double) resultSet.getInt("Time");
                    double Flow = resultSet.getDouble("Volume");
                    double Occ = resultSet.getDouble("Occupancy");

                    Calendar c = Calendar.getInstance();
                    String Date=_Day+"/"+_Month+"/"+_Year;
                    c.setTime(new SimpleDateFormat("dd/mm/yyyy").parse(Date));
                    int _DayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    if(DayOfWeek>0 && DayOfWeek<8){// A particular day of week
                        if (getHealthIndex(HealthIndex, _Year, _Month, _Day) == 1 && _DayOfWeek==DayOfWeek) {//If data is good
                            DataList.add(new double[]{Time, Flow, Occ}); // Append the data
                        }
                    }
                    else if(DayOfWeek==8){ // Weekday profile
                        if (getHealthIndex(HealthIndex, _Year, _Month, _Day) == 1 &&(_DayOfWeek>1 && _DayOfWeek<7)) {//If data is good
                            DataList.add(new double[]{Time, Flow, Occ}); // Append the data
                        }
                    }
                    else if(DayOfWeek==9){ // Weekend profile
                        if (getHealthIndex(HealthIndex, _Year, _Month, _Day) == 1 && (_DayOfWeek==1 || _DayOfWeek==7)) {//If data is good
                            DataList.add(new double[]{Time, Flow, Occ}); // Append the data
                        }
                    }
                    else if(DayOfWeek==0){ // All data
                        if (getHealthIndex(HealthIndex, _Year, _Month, _Day) == 1) {//If data is good
                            DataList.add(new double[]{Time, Flow, Occ}); // Append the data
                        }
                    }else{
                        System.out.println("Wrong day of week!");
                        System.exit(-1);
                    }
                }
            }
            // Convert it to matrix
            if(DataList.size()>0){
                DataAll=new double[DataList.size()][3];
                for(int i=0;i<DataList.size();i++){
                    DataAll[i]=DataList.get(i);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DataAll;
    }

    /**
     *
     * @param HealthIndex List<int[]> health index
     * @param Year
     * @param Month
     * @param Day
     * @return Health 0/1
     */
    public static int getHealthIndex(List<int[]> HealthIndex,int Year,int Month,int Day){
        // This function is used to get the health index
        int Health=0;
        for (int i=0;i<HealthIndex.size();i++){
            int [] tmpRow=HealthIndex.get(i);
            if(tmpRow[0]==Year && tmpRow[1]==Month && tmpRow[2]==Day){
                Health=tmpRow[3];
                break;
            }
        }
        return Health;
    }

    /**
     *
     * @param InputData List<Double> Input Data
     * @return Median double
     */
    public static double getMedianFromList(List<Double> InputData){
        // Get the median value
        double Median=-1;
        if(InputData.size()>0){
            // Have observations
            Collections.sort(InputData);
            if(InputData.size()%2==0){
                Median=0.5*(InputData.get(InputData.size()/2)+
                        InputData.get(InputData.size()/2-1));
            }else{
                Median=InputData.get(InputData.size()/2);
            }
        }
        return Median;
    }

    /**
     *
     * @param InputData List<Double> Input Data
     * @return Average double
     */
    public static double getAverageFromList(List<Double> InputData){
        // Get the average value
        double Average=-1;
        if(InputData.size()>0){
            // Have observations
            double sum=0;
            // Get to total sum
            for(int i=0;i<InputData.size();i++){
                sum=sum+InputData.get(i);
            }
            Average=sum/InputData.size();
        }
        return Average;
    }

}
