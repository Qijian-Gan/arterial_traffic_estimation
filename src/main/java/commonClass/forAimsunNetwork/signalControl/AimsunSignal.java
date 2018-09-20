package commonClass.forAimsunNetwork.signalControl;

public class AimsunSignal {
    // This if the profile for Aimsun signal
    public AimsunSignal(int _SignalID, int _NumTurnings, int [] _TurningIDs){
        this.SignalID=_SignalID;
        this.NumTurnings=_NumTurnings;
        this.TurningIDs=_TurningIDs;
    }
    private int SignalID;
    private int NumTurnings;
    private int [] TurningIDs; // Turn movements included in the signal

    // Get functions
    public int getSignalID() {
        return SignalID;
    }

    public int getNumTurnings() {
        return NumTurnings;
    }

    public int[] getTurningIDs() {
        return TurningIDs;
    }

    // Set functions
    public void setSignalID(int signalID) {
        SignalID = signalID;
    }

    public void setNumTurnings(int numTurnings) {
        NumTurnings = numTurnings;
    }

    public void setTurningIDs(int[] turningIDs) {
        TurningIDs = turningIDs;
    }
}
