package commonClass.forAimsunNetwork.junction;

public class AimsunSectionTurning {
    // This is the profile for Aimsun section-turning
    public AimsunSectionTurning(int _SectionID, int _NumTurns, int [] _TurnIDFromLeftToRight){
        this.SectionID=_SectionID;
        this.NumTurns=_NumTurns;
        this.TurnIDFromLeftToRight=_TurnIDFromLeftToRight;
    }
    private int SectionID;
    private int NumTurns;
    private int [] TurnIDFromLeftToRight; //Write the turn orders by section from left to right

    // Get functions
    public int getSectionID() {
        return SectionID;
    }

    public int getNumTurns() {
        return NumTurns;
    }

    public int[] getTurnIDFromLeftToRight() {
        return TurnIDFromLeftToRight;
    }

    // Set functions
    public void setSectionID(int sectionID) {
        SectionID = sectionID;
    }

    public void setNumTurns(int numTurns) {
        NumTurns = numTurns;
    }

    public void setTurnIDFromLeftToRight(int[] turnIDFromLeftToRight) {
        TurnIDFromLeftToRight = turnIDFromLeftToRight;
    }
}
