package commonClass.forEstimation;

public class DetectorTypeByMovement {
    // Category/types of detectors by each movement: left-turn, through, and right-turn
    public DetectorTypeByMovement(String [] _Left,String [] _Through,String [] _Right){
        this.Left=_Left;
        this.Through=_Through;
        this.Right=_Right;
    }
    private String [] Left;
    private String [] Through;
    private String [] Right;

    // Get functions
    public String[] getLeft() {
        return Left;
    }

    public String[] getThrough() {
        return Through;
    }

    public String[] getRight() {
        return Right;
    }

    // Set functions
    public void setLeft(String[] left) {
        Left = left;
    }

    public void setThrough(String[] through) {
        Through = through;
    }

    public void setRight(String[] right) {
        Right = right;
    }
}
