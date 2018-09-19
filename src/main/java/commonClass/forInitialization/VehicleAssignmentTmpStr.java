package commonClass.forInitialization;

import java.util.List;

public class VehicleAssignmentTmpStr{
    // This is the temporary structure during the vehicle assignment process
    public VehicleAssignmentTmpStr(List<SimVehicle> _VehicleUnAssignedQueued, List<SimVehicle> _VehicleUnAssignedMoving,
                                   List<SimVehicle> _VehicleAssigned, double [][] _RearBoundaryByLane){
        this.VehicleUnAssignedQueued=_VehicleUnAssignedQueued;
        this.VehicleUnAssignedMoving=_VehicleUnAssignedMoving;
        this.VehicleAssigned=_VehicleAssigned;
        this.RearBoundaryByLane=_RearBoundaryByLane;
    }
    private List<SimVehicle> VehicleUnAssignedQueued; // List of unassigned queued vehicles
    private List<SimVehicle> VehicleUnAssignedMoving; // List of unassigned moving vehicles
    private List<SimVehicle> VehicleAssigned; // List of vehicles assigned to a particular link and lane
    private double [][] RearBoundaryByLane; // Rear boundary by lane at a given section

    public List<SimVehicle> getVehicleAssigned() {
        return VehicleAssigned;
    }

    public List<SimVehicle> getVehicleUnAssignedMoving() {
        return VehicleUnAssignedMoving;
    }

    public List<SimVehicle> getVehicleUnAssignedQueued() {
        return VehicleUnAssignedQueued;
    }

    public double[][] getRearBoundaryByLane() {
        return RearBoundaryByLane;
    }

    public void setRearBoundaryByLane(double[][] rearBoundaryByLane) {
        RearBoundaryByLane = rearBoundaryByLane;
    }

    public void setVehicleAssigned(List<SimVehicle> vehicleAssigned) {
        VehicleAssigned = vehicleAssigned;
    }

    public void setVehicleUnAssignedMoving(List<SimVehicle> vehicleUnAssignedMoving) {
        VehicleUnAssignedMoving=vehicleUnAssignedMoving;
    }

    public void setVehicleUnAssignedQueued(List<SimVehicle> vehicleUnAssignedQueued) {
        VehicleUnAssignedQueued =vehicleUnAssignedQueued;
    }
}
