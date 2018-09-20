package commonClass.forAimsunNetwork.signalControl;

public class AimsunRing {
    // This is the profile for Aimsun control rings: Coordination settings
    public AimsunRing(int _RingID,int _CoordinatedPhase, double _Offset, int _GetMatchesOffsetWithEndOfPhase){
        this.RingID=_RingID;
        this.CoordinatedPhase=_CoordinatedPhase;
        this.Offset=_Offset;
        this.GetMatchesOffsetWithEndOfPhase=_GetMatchesOffsetWithEndOfPhase;
    }
    private int RingID;
    private int CoordinatedPhase; // Which phase is coordinated?
    private double Offset; // What is the offset?
    private int GetMatchesOffsetWithEndOfPhase; // The matching point is from the end of the phase or not?

    // Get functions
    public int getRingID() {
        return RingID;
    }

    public int getCoordinatedPhase() {
        return CoordinatedPhase;
    }

    public double getOffset() {
        return Offset;
    }

    public int getGetMatchesOffsetWithEndOfPhase() {
        return GetMatchesOffsetWithEndOfPhase;
    }

    // Set functions
    public void setRingID(int ringID) {
        RingID = ringID;
    }

    public void setCoordinatedPhase(int coordinatedPhase) {
        CoordinatedPhase = coordinatedPhase;
    }

    public void setOffset(double offset) {
        Offset = offset;
    }

    public void setGetMatchesOffsetWithEndOfPhase(int getMatchesOffsetWithEndOfPhase) {
        GetMatchesOffsetWithEndOfPhase = getMatchesOffsetWithEndOfPhase;
    }
}
