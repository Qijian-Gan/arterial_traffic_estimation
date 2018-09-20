package commonClass.simulationData;

import java.util.List;

public class Lane {
    public static class LaneStatistics{
        // This is the profile for lane statistics
        public LaneStatistics(int _SectionID, int _LaneID, int _VehicleType, double _LaneFlow, double _LaneFlowStd,
                              double _LaneSpeed, double _LaneSpeedStd){
            this.SectionID=_SectionID;
            this.LaneID=_LaneID;
            this.VehicleType=_VehicleType;
            this.LaneFlow=_LaneFlow;
            this.LaneFlowStd=_LaneFlowStd;
            this.LaneSpeed=_LaneSpeed;
            this.LaneSpeedStd=_LaneSpeedStd;
        }
        private int SectionID;
        private int LaneID;
        private int VehicleType;
        private double LaneFlow;
        private double LaneFlowStd;
        private double LaneSpeed;
        private double LaneSpeedStd;

        // Get functions
        public int getSectionID() {
            return SectionID;
        }

        public int getLaneID() {
            return LaneID;
        }

        public int getVehicleType() {
            return VehicleType;
        }

        public double getLaneFlow() {
            return LaneFlow;
        }

        public double getLaneFlowStd() {
            return LaneFlowStd;
        }

        public double getLaneSpeed() {
            return LaneSpeed;
        }

        public double getLaneSpeedStd() {
            return LaneSpeedStd;
        }

        // Set functions
        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setLaneID(int laneID) {
            LaneID = laneID;
        }

        public void setVehicleType(int vehicleType) {
            VehicleType = vehicleType;
        }

        public void setLaneFlow(double laneFlow) {
            LaneFlow = laneFlow;
        }

        public void setLaneFlowStd(double laneFlowStd) {
            LaneFlowStd = laneFlowStd;
        }

        public void setLaneSpeed(double laneSpeed) {
            LaneSpeed = laneSpeed;
        }

        public void setLaneSpeedStd(double laneSpeedStd) {
            LaneSpeedStd = laneSpeedStd;
        }
    }

    public static class LaneStatisticsByObjectID{
        // This is the profile of lane statistics by object ID
        public LaneStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                ,long _ExecDateTime, List<LaneStatistics> _laneStatisticsList){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.laneStatisticsList=_laneStatisticsList;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<LaneStatistics> laneStatisticsList;

        // Get functions
        public double getInputTime() {
            return InputTime;
        }

        public int getObjectID() {
            return ObjectID;
        }

        public double getFromTime() {
            return FromTime;
        }

        public int getInterval() {
            return Interval;
        }

        public long getExecDateTime() {
            return ExecDateTime;
        }

        public List<LaneStatistics> getLaneStatisticsList() {
            return laneStatisticsList;
        }

        // Set functions
        public void setInputTime(double inputTime) {
            InputTime = inputTime;
        }

        public void setObjectID(int objectID) {
            ObjectID = objectID;
        }

        public void setFromTime(double fromTime) {
            FromTime = fromTime;
        }

        public void setInterval(int interval) {
            Interval = interval;
        }

        public void setExecDateTime(long execDateTime) {
            ExecDateTime = execDateTime;
        }

        public void setLaneStatisticsList(List<LaneStatistics> laneStatisticsList) {
            this.laneStatisticsList = laneStatisticsList;
        }
    }

}
