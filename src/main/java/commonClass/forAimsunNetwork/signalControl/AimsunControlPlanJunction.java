package commonClass.forAimsunNetwork.signalControl;

import java.util.ArrayList;
import java.util.List;

public class AimsunControlPlanJunction {
    // This is the profile of Aimsun control plan-junction
    public AimsunControlPlanJunction(int _PlanID, String _PlanExtID, String _PlanName, int _PlanOffset, int _JunctionID,
                                     String _JunctionName, String _ControlType, int _Offset, int _Cycle,
                                     int _NumBarriers, int _NumRings, int _NumPhases, int _NumSignals,
                                     List<AimsunPhase> _Phases, List<AimsunSignal> _Signals, List<AimsunRing> _Rings){
        this.PlanID=_PlanID;
        this.PlanExtID=_PlanExtID;
        this.PlanName=_PlanName;
        this.PlanOffset=_PlanOffset;
        this.JunctionID=_JunctionID;
        this.JunctionName=_JunctionName;
        this.ControlType=_ControlType;//Type: Unspecified, Uncontrolled, FixedControl, External, Actuated
        this.Offset=_Offset;
        this.Cycle=_Cycle;
        this.NumBarriers=_NumBarriers; // Number of barriers
        this.NumRings=_NumRings; // Number of rings
        this.NumPhases=_NumPhases; // Number of phases
        this.NumSignals=_NumSignals; // Number of signals
        this.Phases=_Phases; // Phases included
        this.Signals=_Signals; // Signals included
        this.Rings=_Rings; // Rings included
    }
    private int PlanID;
    private String PlanExtID;
    private String PlanName;
    private int PlanOffset;
    private int JunctionID;
    private String JunctionName;
    private String ControlType;
    private int Offset;
    private int Cycle;
    private int NumBarriers;
    private int NumRings;
    private int NumPhases;
    private int NumSignals;
    private List<AimsunPhase> Phases; // Phase information
    private List<AimsunSignal> Signals; // Signal information (in Aimsun): normally associated with traffic movements
    private List<AimsunRing> Rings; // Ring settings: dual rings or signal ring
    // Master control plans that associated with this control plan
    protected List<AimsunMasterControlPlan> MasterControlPlan =new ArrayList<AimsunMasterControlPlan>();

    // Get functions
    public int getPlanID() {
        return PlanID;
    }

    public String getPlanExtID() {
        return PlanExtID;
    }

    public String getPlanName() {
        return PlanName;
    }
    public int getPlanOffset() {
        return PlanOffset;
    }

    public int getJunctionID() {
        return JunctionID;
    }

    public String getJunctionName() {
        return JunctionName;
    }

    public String getControlType() {
        return ControlType;
    }

    public int getOffset() {
        return Offset;
    }

    public int getCycle() {
        return Cycle;
    }

    public int getNumBarriers() {
        return NumBarriers;
    }

    public int getNumRings() {
        return NumRings;
    }

    public int getNumPhases() {
        return NumPhases;
    }

    public int getNumSignals() {
        return NumSignals;
    }

    public List<AimsunPhase> getPhases() {
        return Phases;
    }

    public List<AimsunSignal> getSignals() {
        return Signals;
    }

    public List<AimsunRing> getRings() {
        return Rings;
    }

    public List<AimsunMasterControlPlan> getMasterControlPlan() {
        return MasterControlPlan;
    }

    // Set functions
    public void setPlanID(int planID) {
        PlanID = planID;
    }

    public void setPlanExtID(String planExtID) {
        PlanExtID = planExtID;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public void setPlanOffset(int planOffset) {
        PlanOffset = planOffset;
    }

    public void setJunctionID(int junctionID) {
        JunctionID = junctionID;
    }

    public void setJunctionName(String junctionName) {
        JunctionName = junctionName;
    }

    public void setControlType(String controlType) {
        ControlType = controlType;
    }

    public void setOffset(int offset) {
        Offset = offset;
    }

    public void setCycle(int cycle) {
        Cycle = cycle;
    }

    public void setNumBarriers(int numBarriers) {
        NumBarriers = numBarriers;
    }

    public void setNumRings(int numRings) {
        NumRings = numRings;
    }

    public void setNumPhases(int numPhases) {
        NumPhases = numPhases;
    }

    public void setNumSignals(int numSignals) {
        NumSignals = numSignals;
    }

    public void setPhases(List<AimsunPhase> phases) {
        Phases = phases;
    }

    public void setSignals(List<AimsunSignal> signals) {
        Signals = signals;
    }

    public void setRings(List<AimsunRing> rings) {
        Rings = rings;
    }

    public void setMasterControlPlan(List<AimsunMasterControlPlan> masterControlPlan) {
        MasterControlPlan = masterControlPlan;
    }
}
