package commonClass.forAimsunNetwork.signalControl;

public class AimsunPhase {
    // This is the profile for Aimsun phase
    public AimsunPhase(int _PhaseID, int _RingID, double _StartingTime, double _Duration, String _IsInterphase,
                       double _PermissiveStartTime, double _PermissiveEndTime,int _NumSignalInPhase, int [] _SignalIDs){
        this.PhaseID=_PhaseID;
        this.RingID=_RingID;
        this.StartingTime=_StartingTime;
        this.Duration=_Duration;
        this.IsInterphase=_IsInterphase;
        this.PermissiveStartTime=_PermissiveStartTime;
        this.PermissiveEndTime=_PermissiveEndTime;
        this.NumSignalInPhase=_NumSignalInPhase;
        this.SignalIDs=_SignalIDs;
    }
    private int PhaseID;
    private int RingID;
    private double StartingTime;
    private double Duration;
    private String IsInterphase; // Yellow and all red period in Aimsun
    private double PermissiveStartTime; // Permissive movement
    private double PermissiveEndTime;
    private int NumSignalInPhase;
    private int [] SignalIDs; // Signals included in the phase

    // Get functions
    public int getPhaseID() {
        return PhaseID;
    }

    public int getRingID() {
        return RingID;
    }

    public double getStartingTime() {
        return StartingTime;
    }

    public double getDuration() {
        return Duration;
    }

    public String getIsInterphase() {
        return IsInterphase;
    }

    public double getPermissiveStartTime() {
        return PermissiveStartTime;
    }

    public double getPermissiveEndTime() {
        return PermissiveEndTime;
    }

    public int getNumSignalInPhase() {
        return NumSignalInPhase;
    }

    public int[] getSignalIDs() {
        return SignalIDs;
    }

    // Set functions
    public void setPhaseID(int phaseID) {
        PhaseID = phaseID;
    }

    public void setRingID(int ringID) {
        RingID = ringID;
    }

    public void setStartingTime(double startingTime) {
        StartingTime = startingTime;
    }

    public void setDuration(double duration) {
        Duration = duration;
    }

    public void setIsInterphase(String isInterphase) {
        IsInterphase = isInterphase;
    }

    public void setPermissiveStartTime(double permissiveStartTime) {
        PermissiveStartTime = permissiveStartTime;
    }

    public void setPermissiveEndTime(double permissiveEndTime) {
        PermissiveEndTime = permissiveEndTime;
    }

    public void setNumSignalInPhase(int numSignalInPhase) {
        NumSignalInPhase = numSignalInPhase;
    }

    public void setSignalIDs(int[] signalIDs) {
        SignalIDs = signalIDs;
    }
}
