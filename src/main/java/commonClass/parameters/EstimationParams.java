package commonClass.parameters;

public class EstimationParams {
    // Estimation parameters
    public EstimationParams(double _FFSpeedForAdvDet,double _OccThresholdForAdvDet){
        this.FFSpeedForAdvDet=_FFSpeedForAdvDet;
        this.OccThresholdForAdvDet=_OccThresholdForAdvDet;
    }
    private double FFSpeedForAdvDet;
    private double OccThresholdForAdvDet;

    // Get functions
    public double getFFSpeedForAdvDet() {
        return FFSpeedForAdvDet;
    }

    public double getOccThresholdForAdvDet() {
        return OccThresholdForAdvDet;
    }

    // Set functions
    public void setFFSpeedForAdvDet(double FFSpeedForAdvDet) {
        this.FFSpeedForAdvDet = FFSpeedForAdvDet;
    }

    public void setOccThresholdForAdvDet(double occThresholdForAdvDet) {
        OccThresholdForAdvDet = occThresholdForAdvDet;
    }
}
