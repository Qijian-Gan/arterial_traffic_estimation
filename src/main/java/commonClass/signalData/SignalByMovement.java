package commonClass.signalData;

public class SignalByMovement {
    // This is the signal format by movement that is used to determine the proportions of queued and moving vehicles
    public SignalByMovement(String _Movement, double _Cycle, double _GreenTime, double _RedTime, String _CurrentStatus,
                            double _DurationSinceActivated){
        this.Movement=_Movement;
        this.Cycle=_Cycle;
        this.GreenTime=_GreenTime;
        this.RedTime=_RedTime;
        this.CurrentStatus=_CurrentStatus;
        this.DurationSinceActivated=_DurationSinceActivated;
    }
    private String Movement;
    private double Cycle;
    private double GreenTime;
    private double RedTime;
    private String CurrentStatus;
    private double DurationSinceActivated;

    // Get functions
    public String getMovement() {
        return Movement;
    }

    public double getCycle() {
        return Cycle;
    }

    public double getGreenTime() {
        return GreenTime;
    }

    public double getRedTime() {
        return RedTime;
    }

    public String getCurrentStatus() {
        return CurrentStatus;
    }

    public double getDurationSinceActivated() {
        return DurationSinceActivated;
    }

    // Set functions
    public void setMovement(String movement) {
        Movement = movement;
    }

    public void setCycle(double cycle) {
        Cycle = cycle;
    }

    public void setGreenTime(double greenTime) {
        GreenTime = greenTime;
    }

    public void setRedTime(double redTime) {
        RedTime = redTime;
    }

    public void setCurrentStatus(String currentStatus) {
        CurrentStatus = currentStatus;
    }

    public void setDurationSinceActivated(double durationSinceActivated) {
        DurationSinceActivated = durationSinceActivated;
    }
}
