package commonClass.forEstimation;

public class AssessmentStateAndQueueByMovement {
    // Assessment of state and queue by movement: left-turn, through, and right-turn
    public AssessmentStateAndQueueByMovement(String _StatusAssessment,int _QueueAssessment){
        this.StatusAssessment=_StatusAssessment;
        this.QueueAssessment=_QueueAssessment;
    }
    private String StatusAssessment;
    private int QueueAssessment;

    // Get functions
    public String getStatusAssessment() {
        return StatusAssessment;
    }

    public int getQueueAssessment() {
        return QueueAssessment;
    }

    // Set functions
    public void setStatusAssessment(String statusAssessment) {
        StatusAssessment = statusAssessment;
    }

    public void setQueueAssessment(int queueAssessment) {
        QueueAssessment = queueAssessment;
    }
}
