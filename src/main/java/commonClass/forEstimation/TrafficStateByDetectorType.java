package commonClass.forEstimation;

import java.util.List;

public class TrafficStateByDetectorType {
    // Property of traffic states by detector type
    public TrafficStateByDetectorType(String _DetectorType, String _Rate, double[] _Thresholds, double _AvgOcc,
                                      double _AvgFlow, double _TotLanes, List<Double> _Occupancies, List<Double> _Flows){
        this.DetectorType=_DetectorType;
        this.Rate=_Rate;
        this.Thresholds=_Thresholds;
        this.AvgOcc=_AvgOcc;
        this.AvgFlow=_AvgFlow;
        this.TotLanes=_TotLanes;
        this.Occupancies=_Occupancies;
        this.Flows=_Flows;
    }
    private String DetectorType; // Detector type
    private String Rate; // This is a string
    private double [] Thresholds;
    private double AvgOcc; // Average occ and flow
    private double AvgFlow;
    private double TotLanes; // Total number of lanes
    private List<Double> Occupancies; // List of occupancies
    private List<Double> Flows; // List of flows

    // Get functions
    public String getDetectorType() {
        return DetectorType;
    }

    public String getRate() {
        return Rate;
    }

    public double[] getThresholds() {
        return Thresholds;
    }

    public double getAvgOcc() {
        return AvgOcc;
    }

    public double getAvgFlow() {
        return AvgFlow;
    }

    public double getTotLanes() {
        return TotLanes;
    }

    public List<Double> getOccupancies() {
        return Occupancies;
    }

    public List<Double> getFlows() {
        return Flows;
    }

    // Set functions
    public void setDetectorType(String detectorType) {
        DetectorType = detectorType;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public void setThresholds(double[] thresholds) {
        Thresholds = thresholds;
    }

    public void setAvgOcc(double avgOcc) {
        AvgOcc = avgOcc;
    }

    public void setAvgFlow(double avgFlow) {
        AvgFlow = avgFlow;
    }

    public void setTotLanes(double totLanes) {
        TotLanes = totLanes;
    }

    public void setOccupancies(List<Double> occupancies) {
        Occupancies = occupancies;
    }

    public void setFlows(List<Double> flows) {
        Flows = flows;
    }
}
