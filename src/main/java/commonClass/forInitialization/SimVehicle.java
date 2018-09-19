package commonClass.forInitialization;

public class SimVehicle{
    // This is the profile of simulated vehicles to be inserted into Aimsun

    public SimVehicle(int _SectionID, int _LaneID, int _VehicleTypeInAimsun, int _OriginCentroid, int _DestinationCentroid,
                      double _InitialPosition, double _InitialSpeed, boolean _TrackOrNot){
        this.SectionID=_SectionID;
        this.LaneID=_LaneID;
        this.VehicleTypeInAimsun=_VehicleTypeInAimsun;
        this.OriginCentroid=_OriginCentroid;
        this.DestinationCentroid=_DestinationCentroid;
        this.InitialPosition=_InitialPosition;
        this.InitialSpeed=_InitialSpeed;
        this.TrackOrNot=_TrackOrNot;
    }
    private int SectionID;
    private int LaneID;
    private int VehicleTypeInAimsun;
    private int OriginCentroid;
    private int DestinationCentroid;
    private double InitialPosition;
    private double InitialSpeed;
    private boolean TrackOrNot;

    // Get functions
    public int getSectionID() {
        return SectionID;
    }

    public int getLaneID() {
        return LaneID;
    }

    public int getVehicleTypeInAimsun() {
        return VehicleTypeInAimsun;
    }

    public int getOriginCentroid() {
        return OriginCentroid;
    }

    public int getDestinationCentroid() {
        return DestinationCentroid;
    }

    public double getInitialPosition() {
        return InitialPosition;
    }

    public double getInitialSpeed() {
        return InitialSpeed;
    }

    public boolean isTrackOrNot() {
        return TrackOrNot;
    }

    // Set functions
    public void setSectionID(int sectionID) {
        SectionID = sectionID;
    }

    public void setLaneID(int laneID) {
        LaneID = laneID;
    }

    public void setVehicleTypeInAimsun(int vehicleTypeInAimsun) {
        VehicleTypeInAimsun = vehicleTypeInAimsun;
    }

    public void setOriginCentroid(int originCentroid) {
        OriginCentroid = originCentroid;
    }

    public void setDestinationCentroid(int destinationCentroid) {
        DestinationCentroid = destinationCentroid;
    }

    public void setInitialPosition(double initialPosition) {
        InitialPosition = initialPosition;
    }

    public void setInitialSpeed(double initialSpeed) {
        InitialSpeed = initialSpeed;
    }

    public void setTrackOrNot(boolean trackOrNot) {
        TrackOrNot = trackOrNot;
    }
}
