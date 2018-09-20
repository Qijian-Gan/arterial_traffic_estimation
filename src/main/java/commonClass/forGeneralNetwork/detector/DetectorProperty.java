package commonClass.forGeneralNetwork.detector;

import java.util.List;

public class DetectorProperty {
    // This is the profile for detector property at a given approach
    // It is categorized into four different types based on the topology:
    // -->exclusive left turn, exclusive right turn, advance, and general stopbar
    //For each category, it may consist of several combinations of detection movements
    // Exclusive left turn: left turn
    // Exclusive right turn: right turn
    // General stopbar: all movements, through, left-through, left-right, through-right
    // Advance: all movements, left/right/through only, left-through, left-right, through-right
    public DetectorProperty(List<DetectorMovementProperty> _ExclusiveLeftTurn,
                            List<DetectorMovementProperty> _ExclusiveRightTurn,
                            List<DetectorMovementProperty> _AdvanceDetectors,
                            List<DetectorMovementProperty> _GeneralStopbarDetectors){
        this.ExclusiveLeftTurn=_ExclusiveLeftTurn;
        this.ExclusiveRightTurn=_ExclusiveRightTurn;
        this.AdvanceDetectors=_AdvanceDetectors;
        this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
    }
    protected List<DetectorMovementProperty> ExclusiveLeftTurn; // Exclusive left turn (stop-bar) detectors
    protected List<DetectorMovementProperty> ExclusiveRightTurn; // Exclusive right turn (stop-bar) detectors
    protected List<DetectorMovementProperty> AdvanceDetectors; // Advance detectors (exclusive or combined movements)
    // General stop-bar detectors (through, or combined movements)
    protected List<DetectorMovementProperty> GeneralStopbarDetectors;

    // Get functions
    public List<DetectorMovementProperty> getExclusiveLeftTurn() {
        return ExclusiveLeftTurn;
    }

    public List<DetectorMovementProperty> getExclusiveRightTurn() {
        return ExclusiveRightTurn;
    }

    public List<DetectorMovementProperty> getAdvanceDetectors() {
        return AdvanceDetectors;
    }

    public List<DetectorMovementProperty> getGeneralStopbarDetectors() {
        return GeneralStopbarDetectors;
    }

    // Set functions
    public void setAdvanceDetectors(List<DetectorMovementProperty> advanceDetectors) {
        AdvanceDetectors = advanceDetectors;
    }

    public void setExclusiveLeftTurn(List<DetectorMovementProperty> exclusiveLeftTurn) {
        ExclusiveLeftTurn = exclusiveLeftTurn;
    }

    public void setExclusiveRightTurn(List<DetectorMovementProperty> exclusiveRightTurn) {
        ExclusiveRightTurn = exclusiveRightTurn;
    }

    public void setGeneralStopbarDetectors(List<DetectorMovementProperty> generalStopbarDetectors) {
        GeneralStopbarDetectors = generalStopbarDetectors;
    }
}
