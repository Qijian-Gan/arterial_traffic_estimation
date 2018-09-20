package commonClass.parameters;

public class Parameters {
    // Parameter settings
    public Parameters(VehicleParams _vehicleParams, IntersectionParams _intersectionParams,SignalSettings _signalSettings,
                      TurningProportion _turningProportion,EstimationParams _estimationParams){
        this.vehicleParams=_vehicleParams;
        this.intersectionParams=_intersectionParams;
        this.signalSettings=_signalSettings;
        this.turningProportion=_turningProportion;
        this.estimationParams=_estimationParams;
    }
    private VehicleParams vehicleParams; // Vehicle parameters
    private IntersectionParams intersectionParams; // Intersection parameters
    private SignalSettings signalSettings; // Signal Settings
    private TurningProportion turningProportion; // Turning proportions
    private EstimationParams estimationParams; // Estimation parameters

    // Get functions
    public VehicleParams getVehicleParams() {
        return vehicleParams;
    }

    public IntersectionParams getIntersectionParams() {
        return intersectionParams;
    }

    public SignalSettings getSignalSettings() {
        return signalSettings;
    }

    public TurningProportion getTurningProportion() {
        return turningProportion;
    }

    public EstimationParams getEstimationParams() {
        return estimationParams;
    }

    // Set functions
    public void setVehicleParams(VehicleParams vehicleParams) {
        this.vehicleParams = vehicleParams;
    }

    public void setIntersectionParams(IntersectionParams intersectionParams) {
        this.intersectionParams = intersectionParams;
    }

    public void setSignalSettings(SignalSettings signalSettings) {
        this.signalSettings = signalSettings;
    }

    public void setTurningProportion(TurningProportion turningProportion) {
        this.turningProportion = turningProportion;
    }

    public void setEstimationParams(EstimationParams estimationParams) {
        this.estimationParams = estimationParams;
    }
}
