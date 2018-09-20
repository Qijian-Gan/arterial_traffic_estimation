package commonClass.simulationData;

import java.util.List;

public class ControlSignal {
    public static class ControlSignalStatistics{
        // This is the profile of control signal statistics
        public ControlSignalStatistics(int _NodeID, int _SignalGroupID, int _VehicleType,double _State,
                                       double _ActiveTime,double _ActiveTimePercentage){
            this.NodeID=_NodeID;
            this.SignalGroupID=_SignalGroupID;
            this.VehicleType=_VehicleType;
            this.State=_State;
            this.ActiveTime=_ActiveTime;
            this.ActiveTimePercentage=_ActiveTimePercentage;
        }
        private int NodeID;
        private int SignalGroupID;
        private int VehicleType;
        private double State;
        private double ActiveTime;
        private double ActiveTimePercentage;

        // Get functions
        public int getNodeID() {
            return NodeID;
        }

        public int getSignalGroupID() {
            return SignalGroupID;
        }

        public int getVehicleType() {
            return VehicleType;
        }

        public double getState() {
            return State;
        }

        public double getActiveTime() {
            return ActiveTime;
        }

        public double getActiveTimePercentage() {
            return ActiveTimePercentage;
        }

        // Set functions
        public void setNodeID(int nodeID) {
            NodeID = nodeID;
        }

        public void setSignalGroupID(int signalGroupID) {
            SignalGroupID = signalGroupID;
        }

        public void setVehicleType(int vehicleType) {
            VehicleType = vehicleType;
        }

        public void setState(double state) {
            State = state;
        }

        public void setActiveTime(double activeTime) {
            ActiveTime = activeTime;
        }

        public void setActiveTimePercentage(double activeTimePercentage) {
            ActiveTimePercentage = activeTimePercentage;
        }
    }

    public static class ControlSignalStatisticsByObjectID{
        // This is the profile of control signal statistics by object ID
        public ControlSignalStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                ,long _ExecDateTime, List<ControlSignalStatistics> _controlSignalStatisticsList){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.controlSignalStatisticsList=_controlSignalStatisticsList;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<ControlSignalStatistics> controlSignalStatisticsList;

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

        public List<ControlSignalStatistics> getControlSignalStatisticsList() {
            return controlSignalStatisticsList;
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

        public void setControlSignalStatisticsList(List<ControlSignalStatistics> controlSignalStatisticsList) {
            this.controlSignalStatisticsList = controlSignalStatisticsList;
        }
    }

}
