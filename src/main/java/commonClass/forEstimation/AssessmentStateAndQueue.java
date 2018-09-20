package commonClass.forEstimation;

public class AssessmentStateAndQueue {
    // Assessment of state and queue
    public AssessmentStateAndQueue(String [] _StatusAssessment,int [] _QueueAssessment){
        this.StatusAssessment=_StatusAssessment;
        this.QueueAssessment=_QueueAssessment;
    }
    private String[] StatusAssessment;
    private int[] QueueAssessment;

    // Get functions
    public String[] getStatusAssessment() {
        return StatusAssessment;
    }

    public int[] getQueueAssessment() {
        return QueueAssessment;
    }

    // Set functions
    public void setStatusAssessment(String[] statusAssessment) {
        StatusAssessment = statusAssessment;
    }

    public void setQueueAssessment(int[] queueAssessment) {
        QueueAssessment = queueAssessment;
    }
}
