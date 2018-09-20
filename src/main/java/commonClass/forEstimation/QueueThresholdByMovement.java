package commonClass.forEstimation;

public class QueueThresholdByMovement {
    // Queue thresholds for each movement
    public QueueThresholdByMovement(double _QueueToAdvance,double _QueueWithMaxGreen, double _QueueToEnd){
        this.QueueToAdvance=_QueueToAdvance;
        this.QueueWithMaxGreen=_QueueWithMaxGreen;
        this.QueueToEnd=_QueueToEnd;
    }
    private double QueueToAdvance;
    private double QueueWithMaxGreen;
    private double QueueToEnd;

    // Get functions
    public double getQueueToAdvance() {
        return QueueToAdvance;
    }

    public double getQueueWithMaxGreen() {
        return QueueWithMaxGreen;
    }

    public double getQueueToEnd() {
        return QueueToEnd;
    }

    // Set functions
    public void setQueueToAdvance(double queueToAdvance) {
        QueueToAdvance = queueToAdvance;
    }

    public void setQueueWithMaxGreen(double queueWithMaxGreen) {
        QueueWithMaxGreen = queueWithMaxGreen;
    }

    public void setQueueToEnd(double queueToEnd) {
        QueueToEnd = queueToEnd;
    }
}
