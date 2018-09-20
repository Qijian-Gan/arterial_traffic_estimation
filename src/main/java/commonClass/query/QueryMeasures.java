package commonClass.query;

public class QueryMeasures {
    // Settings of data query
    public QueryMeasures(int _Year,int _Month,int _Day,int _DayOfWeek,boolean _Median,int [] _TimeOfDay,int _Interval){
        this.Year=_Year;
        this.Month=_Month;
        this.Day=_Day;
        this.DayOfWeek=_DayOfWeek;
        this.Median=_Median;
        this.TimeOfDay=_TimeOfDay;
        this.Interval=_Interval;
    }
    private int Year;
    private int Month;
    private int Day;
    private int DayOfWeek;
    private boolean Median; // Whether to use median or not
    private int [] TimeOfDay=null; // [From time, To time]
    private int Interval; // Aggregation interval

    // Get functions
    public int getYear() {
        return Year;
    }

    public int getMonth() {
        return Month;
    }

    public int getDay() {
        return Day;
    }

    public int getDayOfWeek() {
        return DayOfWeek;
    }

    public boolean isMedian() {
        return Median;
    }

    public int[] getTimeOfDay() {
        return TimeOfDay;
    }

    public int getInterval() {
        return Interval;
    }

    // Set functions
    public void setYear(int year) {
        Year = year;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public void setDay(int day) {
        Day = day;
    }

    public void setDayOfWeek(int dayOfWeek) {
        DayOfWeek = dayOfWeek;
    }

    public void setMedian(boolean median) {
        Median = median;
    }

    public void setTimeOfDay(int[] timeOfDay) {
        TimeOfDay = timeOfDay;
    }

    public void setInterval(int interval) {
        Interval = interval;
    }
}
