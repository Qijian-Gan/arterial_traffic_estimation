package commonClass.forGeneralNetwork.detector;

import java.util.List;

public class DetectorMovementProperty {
    // This is the profile for detector-movement property
    public DetectorMovementProperty(String _Movement, List<Integer> _DetectorIDs, List<Double> _DetectorLengths,
                                    List<Double> _DistancesToStopbar, List<Integer> _NumberOfLanes){
        this.Movement=_Movement;
        this.DetectorIDs=_DetectorIDs;
        this.DetectorLengths=_DetectorLengths;
        this.DistancesToStopbar=_DistancesToStopbar;
        this.NumberOfLanes=_NumberOfLanes;
    }
    private String Movement;
    private List<Integer> DetectorIDs; // Detector IDs belonging to the same movement
    private List<Double> DetectorLengths;
    private List<Double> DistancesToStopbar;
    private List<Integer> NumberOfLanes;

    // Get functions
    public String getMovement() {
        return Movement;
    }

    public List<Integer> getDetectorIDs() {
        return DetectorIDs;
    }

    public List<Double> getDetectorLengths() {
        return DetectorLengths;
    }

    public List<Double> getDistancesToStopbar() {
        return DistancesToStopbar;
    }

    public List<Integer> getNumberOfLanes() {
        return NumberOfLanes;
    }

    // Set functions
    public void setMovement(String movement) {
        Movement = movement;
    }

    public void setDetectorIDs(List<Integer> detectorIDs) {
        DetectorIDs = detectorIDs;
    }

    public void setDetectorLengths(List<Double> detectorLengths) {
        DetectorLengths = detectorLengths;
    }

    public void setDistancesToStopbar(List<Double> distancesToStopbar) {
        DistancesToStopbar = distancesToStopbar;
    }

    public void setNumberOfLanes(List<Integer> numberOfLanes) {
        NumberOfLanes = numberOfLanes;
    }
}
