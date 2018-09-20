package commonClass.forAimsunNetwork.junction;

public class AimsunTurning {
    // This is the profile for Aimsun turning
    public AimsunTurning(int _TurnID, int _OrigSectionID, int _DestSectionID, int _OrigFromLane, int _OrigToLane,
                         int _DestFromLane, int _DestToLane, String _Movement, double _TurningSpeed, double _TurningAngle){
        this.TurnID=_TurnID;
        this.OrigSectionID=_OrigSectionID;
        this.DestSectionID=_DestSectionID;
        this.OrigFromLane=_OrigFromLane;
        this.OrigToLane=_OrigToLane;
        this.DestFromLane=_DestFromLane;
        this.DestToLane=_DestToLane;
        this.Movement=_Movement;
        this.TurningSpeed=_TurningSpeed;
        this.TurningAngle=_TurningAngle;
    }
    private int TurnID;
    private int OrigSectionID;
    private int DestSectionID;
    private int OrigFromLane; // Labelled from left to right
    private int OrigToLane; // Labelled from left to right
    private int DestFromLane; // Labelled from left to right
    private int DestToLane; // Labelled from left to right
    private String Movement;
    private double TurningSpeed;
    private double TurningAngle;

    // Get functions
    public int getTurnID() {
        return TurnID;
    }

    public int getOrigSectionID() {
        return OrigSectionID;
    }

    public int getDestSectionID() {
        return DestSectionID;
    }

    public int getOrigFromLane() {
        return OrigFromLane;
    }

    public int getOrigToLane() {
        return OrigToLane;
    }

    public int getDestFromLane() {
        return DestFromLane;
    }

    public int getDestToLane() {
        return DestToLane;
    }

    public String getMovement() {
        return Movement;
    }

    public double getTurningSpeed() {
        return TurningSpeed;
    }

    public double getTurningAngle() {
        return TurningAngle;
    }

    // Set functions
    public void setTurnID(int turnID) {
        TurnID = turnID;
    }

    public void setOrigSectionID(int origSectionID) {
        OrigSectionID = origSectionID;
    }

    public void setDestSectionID(int destSectionID) {
        DestSectionID = destSectionID;
    }

    public void setOrigFromLane(int origFromLane) {
        OrigFromLane = origFromLane;
    }

    public void setOrigToLane(int origToLane) {
        OrigToLane = origToLane;
    }

    public void setDestFromLane(int destFromLane) {
        DestFromLane = destFromLane;
    }

    public void setDestToLane(int destToLane) {
        DestToLane = destToLane;
    }

    public void setMovement(String movement) {
        Movement = movement;
    }

    public void setTurningSpeed(double turningSpeed) {
        TurningSpeed = turningSpeed;
    }

    public void setTurningAngle(double turningAngle) {
        TurningAngle = turningAngle;
    }
}
