package commonClass.simulationData;

import java.util.List;

public class ControlPhase {
    public static class ControlPhaseStatistics{
        // This is the profile of control phase statistics
        public ControlPhaseStatistics(int _ControlPlanID,int _NodeID,int _PhaseID, double _ActiveTime,double _ActiveTimePercentage){
            this.ControlPlanID=_ControlPlanID;
            this.NodeID=_NodeID;
            this.PhaseID=_PhaseID;
            this.ActiveTime=_ActiveTime;
            this.ActiveTimePercentage=_ActiveTimePercentage;
        }
        private int ControlPlanID;
        private int NodeID;
        private int PhaseID;
        private double ActiveTime;
        private double ActiveTimePercentage;

        // Get functions
        public int getControlPlanID() {
            return ControlPlanID;
        }

        public int getNodeID() {
            return NodeID;
        }

        public int getPhaseID() {
            return PhaseID;
        }

        public double getActiveTime() {
            return ActiveTime;
        }

        public double getActiveTimePercentage() {
            return ActiveTimePercentage;
        }

        // Set functions
        public void setControlPlanID(int controlPlanID) {
            ControlPlanID = controlPlanID;
        }

        public void setNodeID(int nodeID) {
            NodeID = nodeID;
        }

        public void setPhaseID(int phaseID) {
            PhaseID = phaseID;
        }

        public void setActiveTime(double activeTime) {
            ActiveTime = activeTime;
        }

        public void setActiveTimePercentage(double activeTimePercentage) {
            ActiveTimePercentage = activeTimePercentage;
        }
    }

    public static class ControlPhaseStatisticsByObjectID{
        // This is the profile of control phase statistics by object ID
        public ControlPhaseStatisticsByObjectID(double _InputTime, int _ObjectID, double _FromTime, int _Interval
                ,long _ExecDateTime, List<ControlPhaseStatistics> _controlPhaseStatisticsList){
            this.InputTime=_InputTime;
            this.ObjectID=_ObjectID;
            this.FromTime=_FromTime;
            this.Interval=_Interval;
            this.ExecDateTime=_ExecDateTime;
            this.controlPhaseStatisticsList=_controlPhaseStatisticsList;
        }
        private double InputTime;
        private int ObjectID;
        private double FromTime;
        private int Interval;
        private long ExecDateTime;
        private List<ControlPhaseStatistics> controlPhaseStatisticsList;

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

        public List<ControlPhaseStatistics> getControlPhaseStatisticsList() {
            return controlPhaseStatisticsList;
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

        public void setControlPhaseStatisticsList(List<ControlPhaseStatistics> controlPhaseStatisticsList) {
            this.controlPhaseStatisticsList = controlPhaseStatisticsList;
        }
    }

}
