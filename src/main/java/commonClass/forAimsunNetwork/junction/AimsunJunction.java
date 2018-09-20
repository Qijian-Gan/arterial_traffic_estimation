package commonClass.forAimsunNetwork.junction;

import java.util.List;

public class AimsunJunction {
    // This is the profile for Aimsun junction
    public AimsunJunction(int _JunctionID, String _JunctionName, String _JunctionExtID, int _Signalized, int _NumEntranceSections,
                          int _NumExitSections, int _NumTurns, int[] _EntranceSections, int [] _ExitSections,
                          List<AimsunTurning> _Turnings, List<AimsunSectionTurning> _SectionTurningList){
        this.JunctionID=_JunctionID;
        this.JunctionName=_JunctionName;
        this.JunctionExtID=_JunctionExtID;
        this.Signalized=_Signalized;
        this.NumEntranceSections=_NumEntranceSections;
        this.NumExitSections=_NumExitSections;
        this.NumTurns=_NumTurns;
        this.EntranceSections=_EntranceSections;
        this.ExitSections=_ExitSections;
        this.Turnings=_Turnings;
        this.SectionTurningList=_SectionTurningList;
    }
    private int JunctionID;
    private String JunctionName;
    private String JunctionExtID;
    private int Signalized; // whether this junction is signalized or not
    private int NumEntranceSections; // Number of entrance sections
    private int NumExitSections; // Number of exit sections
    private int NumTurns; // Number of turns
    private int [] EntranceSections; // Entrance sections
    private int [] ExitSections; // Exit sections
    private List<AimsunTurning> Turnings; // Turnings
    // Turns included in a section; Turning movements ordered from left to right in a give section
    private List<AimsunSectionTurning> SectionTurningList;

    // Get functions
    public int getJunctionID() {
        return JunctionID;
    }

    public String getJunctionName() {
        return JunctionName;
    }

    public String getJunctionExtID() {
        return JunctionExtID;
    }

    public int getSignalized() {
        return Signalized;
    }

    public int getNumEntranceSections() {
        return NumEntranceSections;
    }

    public int getNumExitSections() {
        return NumExitSections;
    }

    public int getNumTurns() {
        return NumTurns;
    }

    public int[] getEntranceSections() {
        return EntranceSections;
    }

    public int[] getExitSections() {
        return ExitSections;
    }

    public List<AimsunTurning> getTurnings() {
        return Turnings;
    }

    public List<AimsunSectionTurning> getSectionTurningList() {
        return SectionTurningList;
    }

    // Set functions
    public void setJunctionID(int junctionID) {
        JunctionID = junctionID;
    }

    public void setJunctionName(String junctionName) {
        JunctionName = junctionName;
    }

    public void setJunctionExtID(String junctionExtID) {
        JunctionExtID = junctionExtID;
    }

    public void setSignalized(int signalized) {
        Signalized = signalized;
    }

    public void setNumEntranceSections(int numEntranceSections) {
        NumEntranceSections = numEntranceSections;
    }

    public void setNumExitSections(int numExitSections) {
        NumExitSections = numExitSections;
    }

    public void setNumTurns(int numTurns) {
        NumTurns = numTurns;
    }

    public void setEntranceSections(int[] entranceSections) {
        EntranceSections = entranceSections;
    }

    public void setExitSections(int[] exitSections) {
        ExitSections = exitSections;
    }

    public void setTurnings(List<AimsunTurning> turnings) {
        Turnings = turnings;
    }

    public void setSectionTurningList(List<AimsunSectionTurning> sectionTurningList) {
        SectionTurningList = sectionTurningList;
    }
}
