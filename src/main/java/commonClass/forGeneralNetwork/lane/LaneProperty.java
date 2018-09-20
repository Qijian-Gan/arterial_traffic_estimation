package commonClass.forGeneralNetwork.lane;

import java.util.List;

public class LaneProperty {
    // This is the profile for lane property
    public LaneProperty(int _LaneID, int _IsExclusive, List<Integer> _TurningMovements, List<Double> _Proportions, double _Length){
        this.LaneID=_LaneID;
        this.IsExclusive=_IsExclusive;
        this.TurningMovements=_TurningMovements;
        this.Proportions=_Proportions;
        this.Length=_Length;
    }
    private int LaneID;
    private int IsExclusive; // Is an exclusive lane or not
    private List<Integer> TurningMovements; // Turning movements on the lane
    private List<Double> Proportions; // Proportions of the turning movements on the lane
    private double Length;

    // Get functions
    public int getLaneID() {
        return LaneID;
    }

    public int getIsExclusive() {
        return IsExclusive;
    }

    public List<Integer> getTurningMovements() {
        return TurningMovements;
    }

    public List<Double> getProportions() {
        return Proportions;
    }

    public double getLength() {
        return Length;
    }

    // Set functions
    public void setLaneID(int laneID) {
        LaneID = laneID;
    }

    public void setIsExclusive(int isExclusive) {
        IsExclusive = isExclusive;
    }

    public void setTurningMovements(List<Integer> turningMovements) {
        TurningMovements = turningMovements;
    }

    public void setProportions(List<Double> proportions) {
        Proportions = proportions;
    }

    public void setLength(double length) {
        Length = length;
    }
}
