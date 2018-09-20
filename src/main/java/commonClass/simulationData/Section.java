package commonClass.simulationData;

import java.util.List;

public class Section {

    public static class SectionStatistics{
        // This is the profile for section statistics
        public SectionStatistics(int _SectionID, int _VehicleType, double _SectionFlow, double _SectionFlowStd,
                                 double _SectionSpeed, double _SectionSpeedStd){
            this.SectionID=_SectionID;
            this.VehicleType=_VehicleType;
            this.SectionFlow=_SectionFlow;
            this.SectionFlowStd=_SectionFlowStd;
            this.SectionSpeed=_SectionSpeed;
            this.SectionSpeedStd=_SectionSpeedStd;
        }
        private int SectionID;
        private int VehicleType;
        private double SectionFlow;
        private double SectionFlowStd;
        private double SectionSpeed;
        private double SectionSpeedStd;

        // Get functions
        public int getSectionID() {
            return SectionID;
        }

        public int getVehicleType() {
            return VehicleType;
        }

        public double getSectionFlow() {
            return SectionFlow;
        }

        public double getSectionFlowStd() {
            return SectionFlowStd;
        }

        public double getSectionSpeed() {
            return SectionSpeed;
        }

        public double getSectionSpeedStd() {
            return SectionSpeedStd;
        }

        // Set functions
        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setVehicleType(int vehicleType) {
            VehicleType = vehicleType;
        }

        public void setSectionFlow(double sectionFlow) {
            SectionFlow = sectionFlow;
        }

        public void setSectionFlowStd(double sectionFlowStd) {
            SectionFlowStd = sectionFlowStd;
        }

        public void setSectionSpeed(double sectionSpeed) {
            SectionSpeed = sectionSpeed;
        }

        public void setSectionSpeedStd(double sectionSpeedStd) {
            SectionSpeedStd = sectionSpeedStd;
        }
    }

    public static class SectionStatisticsByObjectID{
        // This is the profile of section statistics by object ID
        public SectionStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                , long _ExecDateTime, List<SectionStatistics> _SectionStatistics){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.sectionStatisticsList=_SectionStatistics;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<SectionStatistics> sectionStatisticsList;

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

        public List<SectionStatistics> getSectionStatisticsList() {
            return sectionStatisticsList;
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

        public void setSectionStatisticsList(List<SectionStatistics> sectionStatisticsList) {
            this.sectionStatisticsList = sectionStatisticsList;
        }
    }

}
