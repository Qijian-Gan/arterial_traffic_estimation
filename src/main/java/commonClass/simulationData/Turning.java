package commonClass.simulationData;

import java.util.List;

public class Turning {
    public static class TurningStatistics{
        // This is the profile of turning statistics
        public TurningStatistics(int _TurnID, double _TurnFlow, double _TurnSpeed){
            this.TurnID=_TurnID;
            this.TurnFlow=_TurnFlow;
            this.TurnSpeed=_TurnSpeed;
        }
        private int TurnID;
        private double TurnFlow;
        private double TurnSpeed;

        // Get functions
        public int getTurnID() {
            return TurnID;
        }

        public double getTurnFlow() {
            return TurnFlow;
        }

        public double getTurnSpeed() {
            return TurnSpeed;
        }

        // Set functions
        public void setTurnID(int turnID) {
            TurnID = turnID;
        }

        public void setTurnFlow(double turnFlow) {
            TurnFlow = turnFlow;
        }

        public void setTurnSpeed(double turnSpeed) {
            TurnSpeed = turnSpeed;
        }
    }

    public static class TurningStatisticsByObjectID{
        // This is the profile of turning statistics by object ID
        public TurningStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                ,long _ExecDateTime,List<TurningStatistics> _turningStatisticsList){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.turningStatisticsList=_turningStatisticsList;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<TurningStatistics> turningStatisticsList;

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

        public List<TurningStatistics> getTurningStatisticsList() {
            return turningStatisticsList;
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

        public void setTurningStatisticsList(List<TurningStatistics> turningStatisticsList) {
            this.turningStatisticsList = turningStatisticsList;
        }
    }
}
