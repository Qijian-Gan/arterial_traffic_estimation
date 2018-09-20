package commonClass.simulationData;

import java.util.List;

public class Centroid {
    public static class CentroidStatistics{
        // This is the profile of centroid statistics
        public CentroidStatistics(int _SectionID, double _InputTime, double _Interval, double _DistToStopbarThreshold, List<int[]> _ODList,
                                  List<int[]> _DownstreamODList,List<ODByLane> _ODListByLane,List<ODByLane> _DownstreamODListByLane){
            this.SectionID=_SectionID;
            this.InputTime=_InputTime;
            this.Interval=_Interval;
            this.DistToStopbarThreshold=_DistToStopbarThreshold;
            this.ODList=_ODList;
            this.DownstreamODList=_DownstreamODList;
            this.ODListByLane=_ODListByLane;
            this.DownstreamODListByLane=_DownstreamODListByLane;
        }
        private int SectionID;
        private double InputTime;
        private double Interval;
        private double DistToStopbarThreshold; // Used to calculate ODList & ODListByLane in the downstream
        private List<int[]> ODList;
        private List<int[]> DownstreamODList;
        private List<ODByLane> ODListByLane;
        private List<ODByLane> DownstreamODListByLane;

        // Get functions
        public int getSectionID() {
            return SectionID;
        }

        public double getInputTime() {
            return InputTime;
        }

        public double getInterval() {
            return Interval;
        }

        public double getDistToStopbarThreshold() {
            return DistToStopbarThreshold;
        }

        public List<int[]> getODList() {
            return ODList;
        }

        public List<int[]> getDownstreamODList() {
            return DownstreamODList;
        }

        public List<ODByLane> getDownstreamODListByLane() {
            return DownstreamODListByLane;
        }

        public List<ODByLane> getODListByLane() {
            return ODListByLane;
        }

        // Set functions
        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setInputTime(double inputTime) {
            InputTime = inputTime;
        }

        public void setInterval(double interval) {
            Interval = interval;
        }

        public void setDistToStopbarThreshold(double distToStopbarThreshold) {
            DistToStopbarThreshold = distToStopbarThreshold;
        }

        public void setODList(List<int[]> ODList) {
            this.ODList = ODList;
        }

        public void setDownstreamODList(List<int[]> downstreamODList) {
            DownstreamODList = downstreamODList;
        }

        public void setODListByLane(List<ODByLane> ODListByLane) {
            this.ODListByLane = ODListByLane;
        }

        public void setDownstreamODListByLane(List<ODByLane> downstreamODListByLane) {
            DownstreamODListByLane = downstreamODListByLane;
        }
    }

    public static class ODByLane{
        // This is the profile of OD information by Lane ID
        public ODByLane(int _LaneID, List<int[]> _ODList){
            this.LaneID=_LaneID;
            this.ODList=_ODList;
        }
        private int LaneID;
        private List<int[]> ODList;

        // Get functions
        public int getLaneID() {
            return LaneID;
        }

        public List<int[]> getODList() {
            return ODList;
        }

        // Set functions
        public void setLaneID(int laneID) {
            LaneID = laneID;
        }

        public void setODList(List<int[]> ODList) {
            this.ODList = ODList;
        }
    }

    public static class SimVehODInf{
        // This is the property of simulated vehicle's OD information
        public SimVehODInf(int _SectionID,int _LaneID,int _VehicleType,int _CentroidOrigin,int _CentroidDestination,double _DistanceToEnd){
            this.SectionID=_SectionID;
            this.LaneID=_LaneID;
            this.VehicleType=_VehicleType;
            this.CentroidOrigin=_CentroidOrigin;
            this.CentroidDestination=_CentroidDestination;
            this.DistanceToEnd=_DistanceToEnd;
        }
        private int SectionID;
        private int LaneID;
        private int VehicleType;
        private int CentroidOrigin;
        private int CentroidDestination;
        private double DistanceToEnd;

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
        public void setSectionID(int sectionID) {
            SectionID = sectionID;
        }

        public void setLaneID(int laneID) {
            LaneID = laneID;
        }

        public void setVehicleType(int vehicleType) {
            VehicleType = vehicleType;
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
