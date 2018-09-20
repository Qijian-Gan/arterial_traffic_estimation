package commonClass.simulationData;

import java.util.List;

public class ControlTurn {
    public static class ControlTurnStatistics{
        // This is the profile of control turn statistics
        public ControlTurnStatistics(int _TurnID, int _VehicleType,double _State,double _ActiveTime,double _ActiveTimePercentage){
            this.TurnID=_TurnID;
            this.VehicleType=_VehicleType;
            this.State=_State;
            this.ActiveTime=_ActiveTime;
            this.ActiveTimePercentage=_ActiveTimePercentage;
        }
        private int TurnID;
        private int VehicleType;
        private double State;
        private double ActiveTime;
        private double ActiveTimePercentage;

        // Get functions
        public int getTurnID() {
            return TurnID;
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
        public void setTurnID(int turnID) {
            TurnID = turnID;
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

    public static class ControlTurnStatisticsByObjectID{
        // This is the profile of control turn statistics by object ID
        public ControlTurnStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                ,long _ExecDateTime , List<ControlTurnStatistics> _ControlTurnStatistics){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.controlTurnStatisticsList=_ControlTurnStatistics;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<ControlTurnStatistics> controlTurnStatisticsList;

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

        public List<ControlTurnStatistics> getControlTurnStatisticsList() {
            return controlTurnStatisticsList;
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

        public void setControlTurnStatisticsList(List<ControlTurnStatistics> controlTurnStatisticsList) {
            this.controlTurnStatisticsList = controlTurnStatisticsList;
        }
    }

}
