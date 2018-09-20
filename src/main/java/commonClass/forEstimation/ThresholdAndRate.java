package commonClass.forEstimation;

public class ThresholdAndRate {
    // Thresholds and Rates
    public ThresholdAndRate(String _Rate,double [] _Thresholds){
        this.Rate=_Rate;
        this.Thresholds=_Thresholds;
    }
    private String Rate; // This is a string
    private double [] Thresholds;

    // Get functions
    public String getRate() {
        return Rate;
    }

    public double[] getThresholds() {
        return Thresholds;
    }

    // Set functions

    public void setRate(String rate) {
        Rate = rate;
    }

    public void setThresholds(double[] thresholds) {
        Thresholds = thresholds;
    }
}
