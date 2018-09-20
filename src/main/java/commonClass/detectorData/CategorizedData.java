package commonClass.detectorData;

import java.util.List;

public class CategorizedData {
    // This is the profile of categorized data
    public CategorizedData(List<Double> _Time, List<List<Double>> _CategorizedFlow, List<List<Double>> _CategorizedOcc){
        this.Time=_Time;
        this.CategorizedFlow=_CategorizedFlow;
        this.CategorizedOcc=_CategorizedOcc;
    }
    private List<Double> Time;
    private List<List<Double>> CategorizedFlow;
    private List<List<Double>> CategorizedOcc;

    // Get functions
    public List<Double> getTime() {
        return Time;
    }

    public List<List<Double>> getCategorizedFlow() {
        return CategorizedFlow;
    }

    public List<List<Double>> getCategorizedOcc() {
        return CategorizedOcc;
    }

    // Set functions
    public void setTime(List<Double> time) {
        Time = time;
    }

    public void setCategorizedFlow(List<List<Double>> categorizedFlow) {
        CategorizedFlow = categorizedFlow;
    }

    public void setCategorizedOcc(List<List<Double>> categorizedOcc) {
        CategorizedOcc = categorizedOcc;
    }
}
