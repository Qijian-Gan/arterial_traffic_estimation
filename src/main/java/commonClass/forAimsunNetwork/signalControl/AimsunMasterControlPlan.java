package commonClass.forAimsunNetwork.signalControl;

public class AimsunMasterControlPlan {
    // This is the profile for Aimsun master control plan
    public AimsunMasterControlPlan(int _MasterPlanID, String _Name, int _ControlPlanID, int _StartingTime,
                                   int _Duration,int _Zone){
        this.MasterPlanID=_MasterPlanID;
        this.Name=_Name;
        this.ControlPlanID=_ControlPlanID;
        this.StartingTime=_StartingTime;
        this.Duration=_Duration;
        this.Zone=_Zone;
    }
    private int MasterPlanID;
    private String Name;
    private int ControlPlanID; // Control plan ID included in the master control plan
    private int StartingTime;
    private int Duration;
    private int Zone; // The traffic zone (may be related to the "Intersection"?)

    // Get functions
    public int getMasterPlanID() {
        return MasterPlanID;
    }

    public String getName() {
        return Name;
    }

    public int getControlPlanID() {
        return ControlPlanID;
    }

    public int getStartingTime() {
        return StartingTime;
    }

    public int getDuration() {
        return Duration;
    }

    public int getZone() {
        return Zone;
    }

    // Set functions
    public void setMasterPlanID(int masterPlanID) {
        MasterPlanID = masterPlanID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setControlPlanID(int controlPlanID) {
        ControlPlanID = controlPlanID;
    }

    public void setStartingTime(int startingTime) {
        StartingTime = startingTime;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setZone(int zone) {
        Zone = zone;
    }
}
