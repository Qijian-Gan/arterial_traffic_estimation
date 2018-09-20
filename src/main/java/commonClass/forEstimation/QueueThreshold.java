package commonClass.forEstimation;

public class QueueThreshold {
    // Queue thresholds for three movements
    public QueueThreshold(QueueThresholdByMovement _QueueThresholdLeft, QueueThresholdByMovement _QueueThresholdThrough
            ,QueueThresholdByMovement _QueueThresholdRight){
        this.QueueThresholdLeft=_QueueThresholdLeft;
        this.QueueThresholdThrough=_QueueThresholdThrough;
        this.QueueThresholdRight=_QueueThresholdRight;
    }
    private QueueThresholdByMovement QueueThresholdLeft;
    private QueueThresholdByMovement QueueThresholdThrough;
    private QueueThresholdByMovement QueueThresholdRight;

    // Get functions
    public QueueThresholdByMovement getQueueThresholdLeft() {
        return QueueThresholdLeft;
    }

    public QueueThresholdByMovement getQueueThresholdThrough() {
        return QueueThresholdThrough;
    }

    public QueueThresholdByMovement getQueueThresholdRight() {
        return QueueThresholdRight;
    }

    // Set functions
    public void setQueueThresholdLeft(QueueThresholdByMovement queueThresholdLeft) {
        QueueThresholdLeft = queueThresholdLeft;
    }

    public void setQueueThresholdThrough(QueueThresholdByMovement queueThresholdThrough) {
        QueueThresholdThrough = queueThresholdThrough;
    }

    public void setQueueThresholdRight(QueueThresholdByMovement queueThresholdRight) {
        QueueThresholdRight = queueThresholdRight;
    }
}
