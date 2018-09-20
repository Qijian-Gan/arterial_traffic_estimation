package commonClass.parameters;

public class IntersectionParams {
    // Intersection parameters
    public IntersectionParams(double _SaturationHeadway,double _SaturationSpeedLeft,
                              double _SaturationSpeedRight,double _SaturationSpeedThrough,
                              double _DistanceAdvanceDetector,double _LeftTurnPocket,
                              double _RightTurnPocket,double _DistanceToEnd){
        this.SaturationHeadway=_SaturationHeadway;
        this.SaturationSpeedLeft=_SaturationSpeedLeft;
        this.SaturationSpeedThrough=_SaturationSpeedThrough;
        this.SaturationSpeedRight=_SaturationSpeedRight;
        this.DistanceAdvanceDetector=_DistanceAdvanceDetector;
        this.LeftTurnPocket=_LeftTurnPocket;
        this.RightTurnPocket=_RightTurnPocket;
        this.DistanceToEnd=_DistanceToEnd;
    }
    private double SaturationHeadway;
    private double SaturationSpeedLeft;
    private double SaturationSpeedRight;
    private double SaturationSpeedThrough;
    private double DistanceAdvanceDetector;
    private double LeftTurnPocket;
    private double RightTurnPocket;
    private double DistanceToEnd;

    // Get functions
    public double getSaturationHeadway() {
        return SaturationHeadway;
    }

    public double getSaturationSpeedLeft() {
        return SaturationSpeedLeft;
    }

    public double getSaturationSpeedThrough() {
        return SaturationSpeedThrough;
    }

    public double getSaturationSpeedRight() {
        return SaturationSpeedRight;
    }

    public double getDistanceAdvanceDetector() {
        return DistanceAdvanceDetector;
    }

    public double getLeftTurnPocket() {
        return LeftTurnPocket;
    }

    public double getRightTurnPocket() {
        return RightTurnPocket;
    }

    public double getDistanceToEnd() {
        return DistanceToEnd;
    }

    // Set functions
    public void setSaturationHeadway(double saturationHeadway) {
        SaturationHeadway = saturationHeadway;
    }

    public void setSaturationSpeedLeft(double saturationSpeedLeft) {
        SaturationSpeedLeft = saturationSpeedLeft;
    }

    public void setSaturationSpeedThrough(double saturationSpeedThrough) {
        SaturationSpeedThrough = saturationSpeedThrough;
    }

    public void setSaturationSpeedRight(double saturationSpeedRight) {
        SaturationSpeedRight = saturationSpeedRight;
    }

    public void setDistanceAdvanceDetector(double distanceAdvanceDetector) {
        DistanceAdvanceDetector = distanceAdvanceDetector;
    }

    public void setLeftTurnPocket(double leftTurnPocket) {
        LeftTurnPocket = leftTurnPocket;
    }

    public void setRightTurnPocket(double rightTurnPocket) {
        RightTurnPocket = rightTurnPocket;
    }

    public void setDistanceToEnd(double distanceToEnd) {
        DistanceToEnd = distanceToEnd;
    }
}
