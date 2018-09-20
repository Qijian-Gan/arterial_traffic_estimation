package commonClass.simulationData;

import java.util.List;

public class SimInfFromSqlite {
    public SimInfFromSqlite(List<Double> _fromTimeList, List<Integer> _objectIDList, List<Long> _execDateTimeList){
        this.fromTimeList=_fromTimeList;
        this.objectIDList=_objectIDList;
        this.execDateTimeList=_execDateTimeList;
    }
    private List<Double> fromTimeList;
    private List<Integer> objectIDList;
    private List<Long> execDateTimeList;

    // Get functions
    public List<Double> getFromTimeList() {
        return fromTimeList;
    }

    public List<Integer> getObjectIDList() {
        return objectIDList;
    }

    public List<Long> getExecDateTimeList() {
        return execDateTimeList;
    }

    // Set functions
    public void setFromTimeList(List<Double> fromTimeList) {
        this.fromTimeList = fromTimeList;
    }

    public void setObjectIDList(List<Integer> objectIDList) {
        this.objectIDList = objectIDList;
    }

    public void setExecDateTimeList(List<Long> execDateTimeList) {
        this.execDateTimeList = execDateTimeList;
    }
}
