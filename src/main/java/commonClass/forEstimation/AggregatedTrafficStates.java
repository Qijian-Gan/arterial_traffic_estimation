package commonClass.forEstimation;

public class AggregatedTrafficStates {
    // Profile for aggregated traffic states
    public AggregatedTrafficStates(double [] _AggregatedStatus,double [] _AvgOccupancy,double [] _AggregatedTotLanes,
                                   double [] _ThresholdLow, double [] _ThresholdHigh){
        this.AggregatedStatus=_AggregatedStatus;
        this.AvgOccupancy=_AvgOccupancy;
        this.AggregatedTotLanes=_AggregatedTotLanes;
        this.ThresholdLow=_ThresholdLow;
        this.ThresholdHigh=_ThresholdHigh;
    }
    private double [] AggregatedStatus; // Status: rate
    private double [] AvgOccupancy; // Avg Occupancy
    private double [] AggregatedTotLanes; // Total lanes
    private double [] ThresholdLow; // Aggregated low thresholds [left, through, right]
    private double [] ThresholdHigh;// Aggregated high thresholds [left, through, right]

    // Get functions
    public double[] getAggregatedStatus() {
        return AggregatedStatus;
    }

    public double[] getAvgOccupancy() {
        return AvgOccupancy;
    }

    public double[] getAggregatedTotLanes() {
        return AggregatedTotLanes;
    }

    public double[] getThresholdLow() {
        return ThresholdLow;
    }

    public double[] getThresholdHigh() {
        return ThresholdHigh;
    }

    // Set functions
    public void setAggregatedStatus(double[] aggregatedStatus) {
        AggregatedStatus = aggregatedStatus;
    }

    public void setAvgOccupancy(double[] avgOccupancy) {
        AvgOccupancy = avgOccupancy;
    }

    public void setAggregatedTotLanes(double[] aggregatedTotLanes) {
        AggregatedTotLanes = aggregatedTotLanes;
    }

    public void setThresholdLow(double[] thresholdLow) {
        ThresholdLow = thresholdLow;
    }

    public void setThresholdHigh(double[] thresholdHigh) {
        ThresholdHigh = thresholdHigh;
    }
}
