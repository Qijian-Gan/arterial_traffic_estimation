package commonClass.bluetoothData;

import commonClass.query.QueryMeasures;
import java.util.List;
import java.util.Map;

public class TravelTimeStatistics {

    private String LocationFrom;
    private String LocationTo;
    private QueryMeasures queryMeasures;
    private List<int[]> TravelTimeAll; // [Date,Time,Travel Time]
    private Map<Integer,List<Integer>> TravelTimeByInterval; // <Time, List of travel times> Hash map
    private Map<Integer,Integer> MedianTravelTime; // <Time,Median travel time> Hash map
    private Map<Integer,Integer> AverageTravelTime; // <Time, Average travel time> Hash map

    public TravelTimeStatistics(String _LocationFrom, String _LocationTo, QueryMeasures _queryMeasures, List<int[]> _TravelTimeAll
            ,Map<Integer,List<Integer>> _TravelTimeByInterval,Map<Integer,Integer> _MedianTravelTime,Map<Integer,Integer> _AverageTravelTime){
        this.LocationFrom=_LocationFrom;
        this.LocationTo=_LocationTo;
        this.queryMeasures=_queryMeasures;
        this.TravelTimeAll=_TravelTimeAll;
        this.TravelTimeByInterval=_TravelTimeByInterval;
        this.MedianTravelTime=_MedianTravelTime;
        this.AverageTravelTime=_AverageTravelTime;
    }
    // Get functions
    public String getLocationFrom() {
        return LocationFrom;
    }

    public String getLocationTo() {
        return LocationTo;
    }

    public QueryMeasures getQueryMeasures() {
        return queryMeasures;
    }

    public List<int[]> getTravelTimeAll() {
        return TravelTimeAll;
    }

    public Map<Integer, List<Integer>> getTravelTimeByInterval() {
        return TravelTimeByInterval;
    }

    public Map<Integer, Integer> getMedianTravelTime() {
        return MedianTravelTime;
    }

    public Map<Integer, Integer> getAverageTravelTime() {
        return AverageTravelTime;
    }

    // Set functions
    public void setLocationFrom(String locationFrom) {
        LocationFrom = locationFrom;
    }

    public void setLocationTo(String locationTo) {
        LocationTo = locationTo;
    }

    public void setQueryMeasures(QueryMeasures queryMeasures) {
        this.queryMeasures = queryMeasures;
    }

    public void setTravelTimeAll(List<int[]> travelTimeAll) {
        TravelTimeAll = travelTimeAll;
    }

    public void setTravelTimeByInterval(Map<Integer, List<Integer>> travelTimeByInterval) {
        TravelTimeByInterval = travelTimeByInterval;
    }

    public void setMedianTravelTime(Map<Integer, Integer> medianTravelTime) {
        MedianTravelTime = medianTravelTime;
    }

    public void setAverageTravelTime(Map<Integer, Integer> averageTravelTime) {
        AverageTravelTime = averageTravelTime;
    }
}
