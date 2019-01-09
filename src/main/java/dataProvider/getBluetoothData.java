package dataProvider;

import commonClass.query.QueryMeasures;
import commonClass.bluetoothData.*;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class getBluetoothData {

    public static TravelTimeStatistics getTravelTimeStatistics(Connection con, String LocationFrom, String LocationTo, QueryMeasures queryMeasures){
        // This function is used to get travel statistics

        int StartTime=0;
        int EndTime=24*3600;
        if(queryMeasures.getTimeOfDay()!=null){
            StartTime=queryMeasures.getTimeOfDay()[0];
            EndTime=queryMeasures.getTimeOfDay()[1];
        }
        TravelTimeStatistics travelTimeStatistics=null;
        try {
            Statement ps=con.createStatement();
            String sql="select DateAtA, TimeAtA, TravelTime from bluetooth_travel_time where LocationA=\""+LocationFrom
                    +"\" and LocationB=\""+LocationTo +"\" and TimeAtA>="+StartTime+ " and TimeAtA<="+EndTime+";";
            ResultSet resultset=ps.executeQuery(sql);
            List<int[]> DateTimeTravelTime=new ArrayList<>();
            while(resultset.next()){ // Always get the last one.
                int DateAtA=resultset.getInt("DateAtA");
                int TimeAtA=resultset.getInt("TimeAtA");
                int TravelTime=resultset.getInt("TravelTime");
                DateTimeTravelTime.add(new int[]{DateAtA,TimeAtA,TravelTime});
            }

            // Filtering
            DateTimeTravelTime=filterByYear(DateTimeTravelTime,queryMeasures.getYear());
            DateTimeTravelTime=filterByMonth(DateTimeTravelTime,queryMeasures.getMonth());
            DateTimeTravelTime=filterByDay(DateTimeTravelTime,queryMeasures.getDay());
            DateTimeTravelTime=filterByDayOfWeek(DateTimeTravelTime,queryMeasures.getDayOfWeek());

            int Interval=queryMeasures.getInterval();
            // Get travel time by interval
            travelTimeStatistics=new TravelTimeStatistics(LocationFrom, LocationTo, queryMeasures, new ArrayList<>(DateTimeTravelTime),
                    null,null,null);
            travelTimeStatistics=getTravelTimeByInterval(travelTimeStatistics,DateTimeTravelTime,Interval, StartTime,EndTime);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return travelTimeStatistics;
    }

    /**
     *
     * @param travelTimeStatistics TravelTimeStatistics (incomplete information)
     * @param DateTimeTravelTime Input variable [Date, Time, Travel Time]
     * @param Interval Time interval
     * @param StartTime Start time
     * @param EndTime End time
     * @return TravelTimeStatistics (complete information)
     */
    public static TravelTimeStatistics getTravelTimeByInterval(TravelTimeStatistics travelTimeStatistics, List<int[]> DateTimeTravelTime
            ,int Interval, int StartTime,int EndTime){
        // This function is used to get travel times categorized by intervals

        // Initialization
        Map<Integer,List<Integer>> TravelTimeByInterval=new HashMap<>();
        Map<Integer,Integer> MedianTravelTime=new HashMap<>();
        Map<Integer,Integer> AverageTravelTime=new HashMap<>();

        // Get number of intervals
        int NumOfInterval=Math.floorDiv(EndTime-StartTime,Interval);
        for(int i=0;i<NumOfInterval;i++){ // Loop for each interval
            int Time=i*Interval+StartTime; // Interval i's time
            List<int[]> RemoveDateTimeTravelTime=new ArrayList<>();
            List<Integer> TravelTimes=new ArrayList<>();
            for(int j=0;j<DateTimeTravelTime.size();j++){
                if(DateTimeTravelTime.get(j)[1]>=Time &&DateTimeTravelTime.get(j)[1]<Time+Interval){ // Within the time interval??
                    TravelTimes.add(DateTimeTravelTime.get(j)[2]); // Add the travel time
                    RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(j));
                }
            }

            // Get median and average travel times
            int[] MedianAverageTravelTime=getMedianAndAverageTravelTimeFromList(TravelTimes);
            TravelTimeByInterval.put(Time,TravelTimes); // Generate the map
            MedianTravelTime.put(Time,MedianAverageTravelTime[0]);
            AverageTravelTime.put(Time,MedianAverageTravelTime[1]);

            // Remove the selected travel times, which can increase the searching speed
            DateTimeTravelTime.removeAll(RemoveDateTimeTravelTime);
        }

        // Update the travel time statistics
        travelTimeStatistics.setTravelTimeByInterval(TravelTimeByInterval);
        travelTimeStatistics.setMedianTravelTime(MedianTravelTime);
        travelTimeStatistics.setAverageTravelTime(AverageTravelTime);
        return travelTimeStatistics;
    }

    /**
     *
     * @param TravelTimes List<Integer>
     * @return new int[]{MedianTravelTime,AverageTravelTime}
     */
    public static int[] getMedianAndAverageTravelTimeFromList(List<Integer> TravelTimes){
        // This function is used to get median and average travel time from the list

        if(TravelTimes.size()==1){
            return new int[]{TravelTimes.get(0),TravelTimes.get(0)};
        }else{
            Collections.sort(TravelTimes);
            int MedianTravelTime;
            if(Math.floorMod(TravelTimes.size(),2)==1){ // Odd number
                MedianTravelTime=TravelTimes.get(Math.floorDiv(TravelTimes.size(),2));
            }else{// Even number
                int idx=Math.floorDiv(TravelTimes.size(),2);
                MedianTravelTime=(TravelTimes.get(idx)+TravelTimes.get(idx-1))/2;
            }
            int Sum=0;
            for(int i=0;i<TravelTimes.size();i++){
                Sum=Sum+TravelTimes.get(i);
            }
            int AverageTravelTime=Sum/TravelTimes.size();

            return new int[]{MedianTravelTime,AverageTravelTime};
        }
    }

    /**
     *
     * @param DateTimeTravelTime Input variable [Date, Time, Travel Time]
     * @param SelectYear Selected Year
     * @return DateTimeTravelTime after removal
     */
    public static List<int[]> filterByYear(List<int[]> DateTimeTravelTime,int SelectYear){
        // This function is used to filter the data by year

        if(SelectYear>0){// There is a year selected?
            List<int[]> RemoveDateTimeTravelTime=new ArrayList<>();
            for(int i=0;i<DateTimeTravelTime.size();i++){
                int Date=DateTimeTravelTime.get(i)[0];
                if(Math.floorDiv(Date,10000)!=SelectYear){ // Not the selected year??
                    RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                }
            }
            DateTimeTravelTime.removeAll(RemoveDateTimeTravelTime);
        }
        return DateTimeTravelTime;
    }

    /**
     *
     * @param DateTimeTravelTime Input variable [Date, Time, Travel Time]
     * @param SelectMonth Selected Month
     * @return DateTimeTravelTime after removal
     */
    public static List<int[]> filterByMonth(List<int[]> DateTimeTravelTime,int SelectMonth){
        // This function is used to filter the data by month

        if(SelectMonth>0){
            List<int[]> RemoveDateTimeTravelTime=new ArrayList<>();
            for(int i=0;i<DateTimeTravelTime.size();i++){
                int Date=DateTimeTravelTime.get(i)[0];
                if(Math.floorDiv(Math.floorMod(Date,10000),100)!=SelectMonth){ // Not the selected month??
                    RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                }
            }
            DateTimeTravelTime.removeAll(RemoveDateTimeTravelTime);
        }
        return DateTimeTravelTime;
    }

    /**
     *
     * @param DateTimeTravelTime Input variable [Date, Time, Travel Time]
     * @param SelectDay Selected Day
     * @return DateTimeTravelTime after removal
     */
    public static List<int[]> filterByDay(List<int[]> DateTimeTravelTime,int SelectDay){
        // This function is used to filter the data by day

        if(SelectDay>0){
            List<int[]> RemoveDateTimeTravelTime=new ArrayList<>();
            for(int i=0;i<DateTimeTravelTime.size();i++){
                int Date=DateTimeTravelTime.get(i)[0];
                if(Math.floorMod(Date,100)!=SelectDay){ // Not the selected day??
                    RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                }
            }
            DateTimeTravelTime.removeAll(RemoveDateTimeTravelTime);
        }
        return DateTimeTravelTime;
    }

    /**
     *
     * @param DateTimeTravelTime Input variable [Date, Time, Travel Time]
     * @param SelectDayOfWeek Selected day of week
     * @return DateTimeTravelTime after removal
     */
    public static List<int[]> filterByDayOfWeek(List<int[]> DateTimeTravelTime,int SelectDayOfWeek){
        // This function is used to filter the data by day of week

        // Calendar.DAY_OF_WEEK: This field takes values SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY ( 1 to 7)
        // Select DayOfWeek: 1-7 (Sunday to Saturday), 8: weekday, 9: weekend
        if(SelectDayOfWeek>0){
            List<int[]> RemoveDateTimeTravelTime=new ArrayList<>();
            Calendar c = Calendar.getInstance();
            Date date;
            for(int i=0;i<DateTimeTravelTime.size();i++){
                int Date=DateTimeTravelTime.get(i)[0];
                String DateString=util.convertDateNumToDateString(Date); // converted to "MM/dd/YYYY"
                try {
                    date=new SimpleDateFormat("MM/dd/yyyy").parse(DateString);
                    c.setTime(date);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                    if(SelectDayOfWeek<8) { // Sunday to Saturday
                        if (dayOfWeek != SelectDayOfWeek) {
                            RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                        }
                    }else if(SelectDayOfWeek==8){ // Not Weekdays
                        if (dayOfWeek ==1 ||dayOfWeek ==7) {
                            RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                        }
                    }else if(SelectDayOfWeek==9){ // Not weekends
                        if (dayOfWeek !=1 ||dayOfWeek !=7) {
                            RemoveDateTimeTravelTime.add(DateTimeTravelTime.get(i));
                        }
                    }else{
                        System.out.println("Unknown Day of Week!");
                        System.exit(-1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            DateTimeTravelTime.removeAll(RemoveDateTimeTravelTime);

        }
        return DateTimeTravelTime;
    }

}
