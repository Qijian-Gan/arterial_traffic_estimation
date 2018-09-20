package commonClass.signalData;

import java.util.List;

public class PhaseInfForApproach {
    // This the profile of phase information for a given approach
    public PhaseInfForApproach(int _JunctionID, int _FirstSectionID, double _Time, List<SignalByMovement> _signalByMovementList){
        this.JunctionID=_JunctionID;
        this.FirstSectionID=_FirstSectionID;
        this.Time=_Time;
        this.signalByMovementList=_signalByMovementList;
    }
    private int JunctionID;
    private int FirstSectionID;
    private double Time;
    private List<SignalByMovement> signalByMovementList;

    // Get functions
    public int getJunctionID() {
        return JunctionID;
    }

    public int getFirstSectionID() {
        return FirstSectionID;
    }

    public double getTime() {
        return Time;
    }

    public List<SignalByMovement> getSignalByMovementList() {
        return signalByMovementList;
    }

    // Set functions
    public void setJunctionID(int junctionID) {
        JunctionID = junctionID;
    }

    public void setFirstSectionID(int firstSectionID) {
        FirstSectionID = firstSectionID;
    }

    public void setTime(double time) {
        Time = time;
    }

    public void setSignalByMovementList(List<SignalByMovement> signalByMovementList) {
        this.signalByMovementList = signalByMovementList;
    }
}
