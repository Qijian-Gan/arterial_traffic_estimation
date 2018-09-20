package commonClass.forEstimation;

import java.util.List;

public class TrafficStateByApproach {
    // Profile for traffic states for different groups of detectors: advance, exclusive left-/right-turn, general stopbar
    public TrafficStateByApproach(int _Time,List<TrafficStateByDetectorType> _AdvanceDetectors,List<TrafficStateByDetectorType> _ExclusiveLeftTurnDetectors
            , List<TrafficStateByDetectorType> _ExclusiveRightTurnDetectors,List<TrafficStateByDetectorType> _GeneralStopbarDetectors
            , QueueThreshold _queueThreshold,String[] _StateByMovement,int[] _QueueByMovement){
        this.Time=_Time;
        this.AdvanceDetectors=_AdvanceDetectors;
        this.ExclusiveLeftTurnDetectors=_ExclusiveLeftTurnDetectors;
        this.ExclusiveRightTurnDetectors=_ExclusiveRightTurnDetectors;
        this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
        this.queueThreshold=_queueThreshold;
        this.StateByMovement=_StateByMovement;
        this.QueueByMovement=_QueueByMovement;
    }
    private int Time;
    // Traffic states at detectors
    private List<TrafficStateByDetectorType> AdvanceDetectors; // At advance detectors
    private List<TrafficStateByDetectorType> ExclusiveLeftTurnDetectors; // At exclusive left turn detectors
    private List<TrafficStateByDetectorType> ExclusiveRightTurnDetectors; // At exclusive right turn detectors
    private List<TrafficStateByDetectorType> GeneralStopbarDetectors; // At general stopbar detectors
    // Traffic states by movements
    private QueueThreshold queueThreshold;
    private String[] StateByMovement;
    private int[] QueueByMovement;

    // Get functions
    public int getTime() {
        return Time;
    }

    public List<TrafficStateByDetectorType> getAdvanceDetectors() {
        return AdvanceDetectors;
    }

    public List<TrafficStateByDetectorType> getExclusiveLeftTurnDetectors() {
        return ExclusiveLeftTurnDetectors;
    }

    public List<TrafficStateByDetectorType> getExclusiveRightTurnDetectors() {
        return ExclusiveRightTurnDetectors;
    }

    public List<TrafficStateByDetectorType> getGeneralStopbarDetectors() {
        return GeneralStopbarDetectors;
    }

    public QueueThreshold getQueueThreshold() {
        return queueThreshold;
    }

    public String[] getStateByMovement() {
        return StateByMovement;
    }

    public int[] getQueueByMovement() {
        return QueueByMovement;
    }

    // Set functions
    public void setTime(int time) {
        Time = time;
    }

    public void setAdvanceDetectors(List<TrafficStateByDetectorType> advanceDetectors) {
        AdvanceDetectors = advanceDetectors;
    }

    public void setExclusiveLeftTurnDetectors(List<TrafficStateByDetectorType> exclusiveLeftTurnDetectors) {
        ExclusiveLeftTurnDetectors = exclusiveLeftTurnDetectors;
    }

    public void setExclusiveRightTurnDetectors(List<TrafficStateByDetectorType> exclusiveRightTurnDetectors) {
        ExclusiveRightTurnDetectors = exclusiveRightTurnDetectors;
    }

    public void setGeneralStopbarDetectors(List<TrafficStateByDetectorType> generalStopbarDetectors) {
        GeneralStopbarDetectors = generalStopbarDetectors;
    }

    public void setQueueThreshold(QueueThreshold queueThreshold) {
        this.queueThreshold = queueThreshold;
    }

    public void setStateByMovement(String[] stateByMovement) {
        StateByMovement = stateByMovement;
    }

    public void setQueueByMovement(int[] queueByMovement) {
        QueueByMovement = queueByMovement;
    }
}
