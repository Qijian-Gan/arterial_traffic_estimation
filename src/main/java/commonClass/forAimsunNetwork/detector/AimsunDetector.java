package commonClass.forAimsunNetwork.detector;

public class AimsunDetector {
    // This is the profile for Aimsun detector
    public AimsunDetector(int _DetectorID, String _ExternalID, int _SectionID, String _Movement,
                          int _NumOfLanes, int _FirstLane, int _LastLane,
                          double _InitialPosition, double _FinalPosition, double _Length){
        this.DetectorID=_DetectorID;
        this.ExternalID=_ExternalID;
        this.SectionID=_SectionID;
        this.Movement=_Movement;
        this.NumOfLanes=_NumOfLanes;
        this.FirstLane=_FirstLane;
        this.LastLane=_LastLane;
        this.InitialPosition=_InitialPosition;
        this.FinalPosition=_FinalPosition;
        this.Length=_Length;
    }
    private int DetectorID;
    private String ExternalID;
    private int SectionID;
    private String Movement;
    private int NumOfLanes;
    private int FirstLane; // Labelled from left to right
    private int LastLane; // Labelled from left to right
    private double InitialPosition; //The position as an offset from the entrance of the section.
    private double FinalPosition; //The position as an offset from the entrance of the section.
    private double Length;

    // Get functions
    public int getDetectorID() {
        return DetectorID;
    }

    public String getExternalID() {
        return ExternalID;
    }

    public int getSectionID() {
        return SectionID;
    }

    public String getMovement() {
        return Movement;
    }

    public int getNumOfLanes() {
        return NumOfLanes;
    }

    public int getFirstLane() {
        return FirstLane;
    }

    public int getLastLane() {
        return LastLane;
    }

    public double getInitialPosition() {
        return InitialPosition;
    }

    public double getFinalPosition() {
        return FinalPosition;
    }

    public double getLength() {
        return Length;
    }

    // Set functions
    public void setDetectorID(int detectorID) {
        DetectorID = detectorID;
    }

    public void setExternalID(String externalID) {
        ExternalID = externalID;
    }

    public void setSectionID(int sectionID) {
        SectionID = sectionID;
    }

    public void setMovement(String movement) {
        Movement = movement;
    }

    public void setNumOfLanes(int numOfLanes) {
        NumOfLanes = numOfLanes;
    }

    public void setFirstLane(int firstLane) {
        FirstLane = firstLane;
    }

    public void setLastLane(int lastLane) {
        LastLane = lastLane;
    }

    public void setInitialPosition(double initialPosition) {
        InitialPosition = initialPosition;
    }

    public void setFinalPosition(double finalPosition) {
        FinalPosition = finalPosition;
    }

    public void setLength(double length) {
        Length = length;
    }
}
