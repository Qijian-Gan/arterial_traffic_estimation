package commonClass.estimationData;

import commonClass.forEstimation.*;
import commonClass.parameters.*;

public class EstimationResults {
    public EstimationResults(int _JunctionID, String _JunctionName, int _FirstSectionID, String _FirstSectionName, int _Time,
                             AssessmentStateAndQueue _assessmentStateAndQueue, Parameters _parameters, long _UpdateDateTime){
        this.JunctionID=_JunctionID;
        this.JunctionName=_JunctionName;
        this.FirstSectionID=_FirstSectionID;
        this.FirstSectionName=_FirstSectionName;
        this.Time=_Time;
        this.assessmentStateAndQueue=_assessmentStateAndQueue;
        this.parameters=_parameters;
        this.UpdateDateTime=_UpdateDateTime;
    }
    private int JunctionID;
    private String JunctionName;
    private int FirstSectionID;
    private String FirstSectionName;
    private int Time;
    private AssessmentStateAndQueue assessmentStateAndQueue;
    private Parameters parameters;
    private long UpdateDateTime;

    // Get functions
    public int getJunctionID() {
        return JunctionID;
    }

    public String getJunctionName() {
        return JunctionName;
    }

    public int getFirstSectionID() {
        return FirstSectionID;
    }

    public String getFirstSectionName() {
        return FirstSectionName;
    }

    public int getTime() {
        return Time;
    }

    public AssessmentStateAndQueue getAssessmentStateAndQueue() {
        return assessmentStateAndQueue;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public long getUpdateDateTime() {
        return UpdateDateTime;
    }

    // Set functions
    public void setJunctionID(int junctionID) {
        JunctionID = junctionID;
    }

    public void setJunctionName(String junctionName) {
        JunctionName = junctionName;
    }

    public void setFirstSectionID(int firstSectionID) {
        FirstSectionID = firstSectionID;
    }

    public void setFirstSectionName(String firstSectionName) {
        FirstSectionName = firstSectionName;
    }

    public void setTime(int time) {
        Time = time;
    }

    public void setAssessmentStateAndQueue(AssessmentStateAndQueue assessmentStateAndQueue) {
        this.assessmentStateAndQueue = assessmentStateAndQueue;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setUpdateDateTime(long updateDateTime) {
        UpdateDateTime = updateDateTime;
    }
}
