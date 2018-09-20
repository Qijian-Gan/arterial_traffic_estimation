package commonClass.forGeneralNetwork.lane;

import java.util.List;

public class LaneTurningProperty {
    // This is the profile for lane-turning property for a give section
    // Lane property(include turn info)-->section
    public LaneTurningProperty(int _SectionID, int _NumLanes, List<LaneProperty> _Lanes){
        this.SectionID=_SectionID;
        this.NumLanes=_NumLanes;
        this.Lanes=_Lanes;
    }
    private int SectionID;
    private int NumLanes;
    private List<LaneProperty> Lanes; // Lane properties

    // Get functions
    public int getSectionID() {
        return SectionID;
    }

    public int getNumLanes() {
        return NumLanes;
    }

    public List<LaneProperty> getLanes() {
        return Lanes;
    }

    // Set functions
    public void setSectionID(int sectionID) {
        SectionID = sectionID;
    }

    public void setNumLanes(int numLanes) {
        NumLanes = numLanes;
    }

    public void setLanes(List<LaneProperty> lanes) {
        Lanes = lanes;
    }
}
