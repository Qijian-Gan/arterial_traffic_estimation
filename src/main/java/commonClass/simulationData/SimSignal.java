package commonClass.simulationData;

public class SimSignal {
    public static class AimsunSigInf{
        // This is the profile of Aimsun signal information
        public AimsunSigInf(double _TimeRelative, double _Time, int _JunctionID, int _ControlType, int _ControlPlanIndex,
                            int _NumOfRings, String _PhaseAndStartTimeInRing){
            this.TimeRelative=_TimeRelative;
            this.Time=_Time;
            this.JunctionID=_JunctionID;
            this.ControlType=_ControlType;
            this.ControlPlanIndex=_ControlPlanIndex;
            this.NumOfRings=_NumOfRings;
            this.PhaseAndStartTimeInRing=_PhaseAndStartTimeInRing;
        }
        private double TimeRelative;
        private double Time;
        private int JunctionID;
        private int ControlType;
        private int ControlPlanIndex;
        private int NumOfRings;
        private String PhaseAndStartTimeInRing;

        // Get functions
        public double getTimeRelative() {
            return TimeRelative;
        }

        public double getTime() {
            return Time;
        }

        public int getJunctionID() {
            return JunctionID;
        }

        public int getControlType() {
            return ControlType;
        }

        public int getControlPlanIndex() {
            return ControlPlanIndex;
        }

        public int getNumOfRings() {
            return NumOfRings;
        }

        public String getPhaseAndStartTimeInRing() {
            return PhaseAndStartTimeInRing;
        }

        // Set functions
        public void setTimeRelative(double timeRelative) {
            TimeRelative = timeRelative;
        }

        public void setTime(double time) {
            Time = time;
        }

        public void setJunctionID(int junctionID) {
            JunctionID = junctionID;
        }

        public void setControlType(int controlType) {
            ControlType = controlType;
        }

        public void setControlPlanIndex(int controlPlanIndex) {
            ControlPlanIndex = controlPlanIndex;
        }

        public void setNumOfRings(int numOfRings) {
            NumOfRings = numOfRings;
        }

        public void setPhaseAndStartTimeInRing(String phaseAndStartTimeInRing) {
            PhaseAndStartTimeInRing = phaseAndStartTimeInRing;
        }
    }

}
