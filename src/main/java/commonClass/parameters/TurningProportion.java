package commonClass.parameters;

public class TurningProportion {
    //Turning proportions
    public TurningProportion(double [] _LeftTurn,double [] _LeftTurnQueue,double [] _AdvanceLeftTurn,
                             double [] _RightTurn,double [] _RightTurnQueue,double [] _AdvanceRightTurn,
                             double [] _Advance,double [] _AllMovements,double [] _AdvanceThrough,
                             double [] _Through,double [] _AdvanceLeftAndThrough,double [] _LeftAndThrough,
                             double [] _AdvanceLeftAndRight,double [] _LeftAndRight,double [] _AdvanceThroughAndRight,
                             double [] _ThroughAndRight){
        this.LeftTurn=_LeftTurn;
        this.LeftTurnQueue=_LeftTurnQueue;
        this.AdvanceLeftTurn=_AdvanceLeftTurn;
        this.RightTurn=_RightTurn;
        this.RightTurnQueue=_RightTurnQueue;
        this.AdvanceRightTurn=_AdvanceRightTurn;
        this.Advance=_Advance;
        this.AllMovements=_AllMovements;
        this.AdvanceThrough=_AdvanceThrough;
        this.Through=_Through;
        this.AdvanceLeftAndThrough=_AdvanceLeftAndThrough;
        this.LeftAndThrough=_LeftAndThrough;
        this.AdvanceLeftAndRight=_AdvanceLeftAndRight;
        this.LeftAndRight=_LeftAndRight;
        this.AdvanceThroughAndRight=_AdvanceThroughAndRight;
        this.ThroughAndRight=_ThroughAndRight;
    }
    private double [] LeftTurn; // Exclusive left turn
    private double [] LeftTurnQueue;
    private double [] AdvanceLeftTurn;
    private double [] RightTurn; // Exclusive right turn
    private double [] RightTurnQueue;
    private double [] AdvanceRightTurn;
    private double [] Advance;   // Mixed through, left turn, and right turn
    private double [] AllMovements;
    private double [] AdvanceThrough; // Exclusive through
    private double [] Through;
    private double [] AdvanceLeftAndThrough; // Left turn and through only
    private double [] LeftAndThrough;
    private double [] AdvanceLeftAndRight;  // Left turn and right turn only
    private double [] LeftAndRight;
    private double [] AdvanceThroughAndRight; // Through and right turn only
    private double [] ThroughAndRight;

    // Get functions
    public double[] getLeftTurn() {
        return LeftTurn;
    }

    public double[] getLeftTurnQueue() {
        return LeftTurnQueue;
    }

    public double[] getAdvanceLeftTurn() {
        return AdvanceLeftTurn;
    }

    public double[] getRightTurn() {
        return RightTurn;
    }

    public double[] getRightTurnQueue() {
        return RightTurnQueue;
    }

    public double[] getAdvanceRightTurn() {
        return AdvanceRightTurn;
    }

    public double[] getAdvance() {
        return Advance;
    }

    public double[] getAllMovements() {
        return AllMovements;
    }

    public double[] getAdvanceThrough() {
        return AdvanceThrough;
    }

    public double[] getThrough() {
        return Through;
    }

    public double[] getAdvanceLeftAndThrough() {
        return AdvanceLeftAndThrough;
    }

    public double[] getLeftAndThrough() {
        return LeftAndThrough;
    }

    public double[] getAdvanceLeftAndRight() {
        return AdvanceLeftAndRight;
    }

    public double[] getLeftAndRight() {
        return LeftAndRight;
    }

    public double[] getAdvanceThroughAndRight() {
        return AdvanceThroughAndRight;
    }

    public double[] getThroughAndRight() {
        return ThroughAndRight;
    }

    // Set functions
    public void setLeftTurn(double[] leftTurn) {
        LeftTurn = leftTurn;
    }

    public void setLeftTurnQueue(double[] leftTurnQueue) {
        LeftTurnQueue = leftTurnQueue;
    }

    public void setAdvanceLeftTurn(double[] advanceLeftTurn) {
        AdvanceLeftTurn = advanceLeftTurn;
    }

    public void setRightTurn(double[] rightTurn) {
        RightTurn = rightTurn;
    }

    public void setRightTurnQueue(double[] rightTurnQueue) {
        RightTurnQueue = rightTurnQueue;
    }

    public void setAdvanceRightTurn(double[] advanceRightTurn) {
        AdvanceRightTurn = advanceRightTurn;
    }

    public void setAdvance(double[] advance) {
        Advance = advance;
    }

    public void setAllMovements(double[] allMovements) {
        AllMovements = allMovements;
    }

    public void setAdvanceThrough(double[] advanceThrough) {
        AdvanceThrough = advanceThrough;
    }

    public void setThrough(double[] through) {
        Through = through;
    }

    public void setAdvanceLeftAndThrough(double[] advanceLeftAndThrough) {
        AdvanceLeftAndThrough = advanceLeftAndThrough;
    }

    public void setLeftAndThrough(double[] leftAndThrough) {
        LeftAndThrough = leftAndThrough;
    }

    public void setAdvanceLeftAndRight(double[] advanceLeftAndRight) {
        AdvanceLeftAndRight = advanceLeftAndRight;
    }

    public void setLeftAndRight(double[] leftAndRight) {
        LeftAndRight = leftAndRight;
    }

    public void setAdvanceThroughAndRight(double[] advanceThroughAndRight) {
        AdvanceThroughAndRight = advanceThroughAndRight;
    }

    public void setThroughAndRight(double[] throughAndRight) {
        ThroughAndRight = throughAndRight;
    }
}
