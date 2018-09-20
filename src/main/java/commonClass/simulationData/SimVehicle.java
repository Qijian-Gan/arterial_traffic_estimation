package commonClass.simulationData;

import java.util.List;

public class SimVehicle {
    public static class SimVehInfBySection{
        // This is the profile of vehicle trajectories by section
        public SimVehInfBySection(int _SectionID, double _InputTime, double _SelectTime, List<AimsunVehInf> _aimsunVehInfList){
            this.SectionID=_SectionID;
            this.InputTime=_InputTime;
            this.SelecTime=_SelectTime;
            this.aimsunVehInfList=_aimsunVehInfList;
        }
        private int SectionID;
        private double InputTime;
        private double SelecTime;
        private List<AimsunVehInf> aimsunVehInfList;

        // Get functions
        public int getSectionID() {
            return SectionID;
        }

        public double getInputTime() {
            return InputTime;
        }

        public double getSelecTime() {
            return SelecTime;
        }

        public List<AimsunVehInf> getAimsunVehInfList() {
            return aimsunVehInfList;
        }

        // Set functions

        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setInputTime(double inputTime) {
            InputTime = inputTime;
        }

        public void setSelecTime(double selecTime) {
            SelecTime = selecTime;
        }

        public void setAimsunVehInfList(List<AimsunVehInf> aimsunVehInfList) {
            this.aimsunVehInfList = aimsunVehInfList;
        }
    }


    public static class AimsunVehInf{
        // This is the profile of Aimsun vehicle information
        public AimsunVehInf(double _Time, int _VehicleID, int _VehicleType, int _SectionID, int _LaneID, double _CurrentPosition
                , double _CurrentSpeed, int _CentroidOrigin, int _CentroidDestination, double _DistanceToEnd){
            this.Time=_Time;
            this.VehicleID=_VehicleID;
            this.VehicleType=_VehicleType;
            this.SectionID=_SectionID;
            this.LaneID=_LaneID;
            this.CurrentPosition=_CurrentPosition;
            this.CurrentSpeed=_CurrentSpeed;
            this.CentroidOrigin=_CentroidOrigin;
            this.CentroidDestination=_CentroidDestination;
            this.DistanceToEnd=_DistanceToEnd;
        }
        private double Time;
        private int VehicleID;
        private int VehicleType;
        private int SectionID;
        private int LaneID;
        private double CurrentPosition;
        private double CurrentSpeed;
        private int CentroidOrigin;
        private int CentroidDestination;
        private double DistanceToEnd;

        // Get functions
        public double getTime() {
            return Time;
        }

        public int getVehicleID() {
            return VehicleID;
        }

        public int getVehicleType() {
            return VehicleType;
        }

        public int getSectionID() {
            return SectionID;
        }

        public int getLaneID() {
            return LaneID;
        }

        public double getCurrentPosition() {
            return CurrentPosition;
        }

        public double getCurrentSpeed() {
            return CurrentSpeed;
        }

        public int getCentroidOrigin() {
            return CentroidOrigin;
        }

        public int getCentroidDestination() {
            return CentroidDestination;
        }

        public double getDistanceToEnd() {
            return DistanceToEnd;
        }

        // Set functions
        public void setTime(double time) {
            Time = time;
        }

        public void setVehicleID(int vehicleID) {
            VehicleID = vehicleID;
        }

        public void setVehicleType(int vehicleType) {
            VehicleType = vehicleType;
        }

        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setLaneID(int laneID) {
            LaneID = laneID;
        }

        public void setCurrentPosition(double currentPosition) {
            CurrentPosition = currentPosition;
        }

        public void setCurrentSpeed(double currentSpeed) {
            CurrentSpeed = currentSpeed;
        }

        public void setCentroidOrigin(int centroidOrigin) {
            CentroidOrigin = centroidOrigin;
        }

        public void setCentroidDestination(int centroidDestination) {
            CentroidDestination = centroidDestination;
        }

        public void setDistanceToEnd(double distanceToEnd) {
            DistanceToEnd = distanceToEnd;
        }
    }

}
