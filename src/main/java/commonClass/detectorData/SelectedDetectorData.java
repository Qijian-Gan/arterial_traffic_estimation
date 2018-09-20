package commonClass.detectorData;

public class SelectedDetectorData {
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
    private int DetectorID; // Detector ID
    private double [][] DataAll=null;  // Time, Flow, Occupancy (All)
    private CategorizedData categorizedData;
    private double [][] DataAvgByTime=null; // Time, Flow, Occupancy (Median)
    private double [][] DataMidByTime=null; // Time, Flow, Occupancy (Average)
    private double[] FlowOCCAvg=null; // Averaged Flow & Occupancy over all data
    private double[] FlowOccMid=null; // Median Flow & Occupancy over all data
    private String Health; // Health index over all data

    // Get functions
    public int getDetectorID() {
        return DetectorID;
    }

    public double[][] getDataAll() {
        return DataAll;
    }

    public CategorizedData getCategorizedData() {
        return categorizedData;
    }

    public double[][] getDataAvgByTime() {
        return DataAvgByTime;
    }

    public double[][] getDataMidByTime() {
        return DataMidByTime;
    }

    public double[] getFlowOCCAvg() {
        return FlowOCCAvg;
    }

    public double[] getFlowOccMid() {
        return FlowOccMid;
    }

    public String getHealth() {
        return Health;
    }

    // Set functions
    public void setDetectorID(int detectorID) {
        DetectorID = detectorID;
    }

    public void setDataAll(double[][] dataAll) {
        DataAll = dataAll;
    }

    public void setCategorizedData(CategorizedData categorizedData) {
        this.categorizedData = categorizedData;
    }

    public void setDataAvgByTime(double[][] dataAvgByTime) {
        DataAvgByTime = dataAvgByTime;
    }

    public void setDataMidByTime(double[][] dataMidByTime) {
        DataMidByTime = dataMidByTime;
    }

    public void setFlowOCCAvg(double[] flowOCCAvg) {
        FlowOCCAvg = flowOCCAvg;
    }

    public void setFlowOccMid(double[] flowOccMid) {
        FlowOccMid = flowOccMid;
    }

    public void setHealth(String health) {
        Health = health;
    }
}
