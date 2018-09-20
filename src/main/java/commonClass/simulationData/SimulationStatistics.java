package commonClass.simulationData;

import java.util.List;

public class SimulationStatistics {
    // This is the profile of simulation statistics
    public SimulationStatistics(List<Turning.TurningStatisticsByObjectID> _turningStatisticsByObjectIDList,
                                List<Lane.LaneStatisticsByObjectID> _laneStatisticsByObjectIDList,
                                List<Section.SectionStatisticsByObjectID> _sectionStatisticsByObjectIDList,
                                List<ControlTurn.ControlTurnStatisticsByObjectID> _controlTurnStatisticsByObjectIDList,
                                List<ControlSignal.ControlSignalStatisticsByObjectID> _controlSignalStatisticsByObjectIDList,
                                List<ControlPhase.ControlPhaseStatisticsByObjectID> _controlPhaseStatisticsByObjectIDList,
                                List<Centroid.CentroidStatistics> _centroidStatisticsList,
                                List<SimVehicle.SimVehInfBySection> _simVehInfBySectionList){
        this.turningStatisticsByObjectIDList=_turningStatisticsByObjectIDList;
        this.laneStatisticsByObjectIDList=_laneStatisticsByObjectIDList;
        this.sectionStatisticsByObjectIDList=_sectionStatisticsByObjectIDList;
        this.controlTurnStatisticsByObjectIDList=_controlTurnStatisticsByObjectIDList;
        this.controlSignalStatisticsByObjectIDList=_controlSignalStatisticsByObjectIDList;
        this.controlPhaseStatisticsByObjectIDList=_controlPhaseStatisticsByObjectIDList;
        this.centroidStatisticsList=_centroidStatisticsList;
        this.simVehInfBySectionList=_simVehInfBySectionList;
    }
    private List<Turning.TurningStatisticsByObjectID> turningStatisticsByObjectIDList; // Turning information
    private List<Lane.LaneStatisticsByObjectID> laneStatisticsByObjectIDList; // Lane information
    private List<Section.SectionStatisticsByObjectID> sectionStatisticsByObjectIDList; // Section information
    private List<ControlTurn.ControlTurnStatisticsByObjectID> controlTurnStatisticsByObjectIDList; // Control turn information
    private List<ControlSignal.ControlSignalStatisticsByObjectID> controlSignalStatisticsByObjectIDList; // Control signal information
    private List<ControlPhase.ControlPhaseStatisticsByObjectID> controlPhaseStatisticsByObjectIDList; // Control phase information

    private List<Centroid.CentroidStatistics> centroidStatisticsList; // Centroid information
    private List<SimVehicle.SimVehInfBySection> simVehInfBySectionList; //Vehicle trajectories

    // Get functions
    public List<Turning.TurningStatisticsByObjectID> getTurningStatisticsByObjectIDList() {
        return turningStatisticsByObjectIDList;
    }

    public List<Lane.LaneStatisticsByObjectID> getLaneStatisticsByObjectIDList() {
        return laneStatisticsByObjectIDList;
    }

    public List<Section.SectionStatisticsByObjectID> getSectionStatisticsByObjectIDList() {
        return sectionStatisticsByObjectIDList;
    }

    public List<ControlTurn.ControlTurnStatisticsByObjectID> getControlTurnStatisticsByObjectIDList() {
        return controlTurnStatisticsByObjectIDList;
    }

    public List<ControlSignal.ControlSignalStatisticsByObjectID> getControlSignalStatisticsByObjectIDList() {
        return controlSignalStatisticsByObjectIDList;
    }

    public List<ControlPhase.ControlPhaseStatisticsByObjectID> getControlPhaseStatisticsByObjectIDList() {
        return controlPhaseStatisticsByObjectIDList;
    }

    public List<Centroid.CentroidStatistics> getCentroidStatisticsList() {
        return centroidStatisticsList;
    }

    public List<SimVehicle.SimVehInfBySection> getSimVehInfBySectionList() {
        return simVehInfBySectionList;
    }

    // Set functions
    public void setTurningStatisticsByObjectIDList(List<Turning.TurningStatisticsByObjectID> turningStatisticsByObjectIDList) {
        this.turningStatisticsByObjectIDList = turningStatisticsByObjectIDList;
    }

    public void setLaneStatisticsByObjectIDList(List<Lane.LaneStatisticsByObjectID> laneStatisticsByObjectIDList) {
        this.laneStatisticsByObjectIDList = laneStatisticsByObjectIDList;
    }

    public void setSectionStatisticsByObjectIDList(List<Section.SectionStatisticsByObjectID> sectionStatisticsByObjectIDList) {
        this.sectionStatisticsByObjectIDList = sectionStatisticsByObjectIDList;
    }

    public void setControlTurnStatisticsByObjectIDList(List<ControlTurn.ControlTurnStatisticsByObjectID> controlTurnStatisticsByObjectIDList) {
        this.controlTurnStatisticsByObjectIDList = controlTurnStatisticsByObjectIDList;
    }

    public void setControlSignalStatisticsByObjectIDList(List<ControlSignal.ControlSignalStatisticsByObjectID> controlSignalStatisticsByObjectIDList) {
        this.controlSignalStatisticsByObjectIDList = controlSignalStatisticsByObjectIDList;
    }

    public void setControlPhaseStatisticsByObjectIDList(List<ControlPhase.ControlPhaseStatisticsByObjectID> controlPhaseStatisticsByObjectIDList) {
        this.controlPhaseStatisticsByObjectIDList = controlPhaseStatisticsByObjectIDList;
    }

    public void setCentroidStatisticsList(List<Centroid.CentroidStatistics> centroidStatisticsList) {
        this.centroidStatisticsList = centroidStatisticsList;
    }

    public void setSimVehInfBySectionList(List<SimVehicle.SimVehInfBySection> simVehInfBySectionList) {
        this.simVehInfBySectionList = simVehInfBySectionList;
    }
}
