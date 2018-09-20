package commonClass.parameters;

public class VehicleParams {
    // Vehicle parameters
    public VehicleParams(double _VehicleLength,double _StartupLostTime,double _JamSpacing){
        this.VehicleLength=_VehicleLength;
        this.StartupLostTime=_StartupLostTime;
        this.JamSpacing=_JamSpacing;
    }
    private double VehicleLength;
    private double StartupLostTime;
    private double JamSpacing;

    // Get functions
    public double getVehicleLength() {
        return VehicleLength;
    }

    public double getStartupLostTime() {
        return StartupLostTime;
    }

    public double getJamSpacing() {
        return JamSpacing;
    }

    // Set functions
    public void setVehicleLength(double vehicleLength) {
        VehicleLength = vehicleLength;
    }

    public void setStartupLostTime(double startupLostTime) {
        StartupLostTime = startupLostTime;
    }

    public void setJamSpacing(double jamSpacing) {
        JamSpacing = jamSpacing;
    }
}
