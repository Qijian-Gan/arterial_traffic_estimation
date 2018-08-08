package dataProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import estimation.trafficStateEstimation.*;
import networkInput.readFromAimsun;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qijian-Gan on 12/8/2017.
 */
public class getSignalData {

    public static class PhaseInfForApproach{
        // This the profile of phase information for a given approach
        public PhaseInfForApproach(int _JunctionID, int _FirstSectionID, double _Time, List<SignalByMovement> _signalByMovementList){
            this.JunctionID=_JunctionID;
            this.FirstSectionID=_FirstSectionID;
            this.Time=_Time;
            this.signalByMovementList=_signalByMovementList;
        }
        protected int JunctionID;
        protected int FirstSectionID;
        protected double Time;
        protected List<SignalByMovement> signalByMovementList;

        public List<SignalByMovement> getSignalByMovementList() {
            return signalByMovementList;
        }
    }

    public static class SignalByMovement{
        // This is the signal format by movement that is used to determine the proportions of queued and moving vehicles
        public SignalByMovement(String _Movement, double _Cycle, double _GreenTime, double _RedTime, String _CurrentStatus,
                                double _DurationSinceActivated){
            this.Movement=_Movement;
            this.Cycle=_Cycle;
            this.GreenTime=_GreenTime;
            this.RedTime=_RedTime;
            this.CurrentStatus=_CurrentStatus;
            this.DurationSinceActivated=_DurationSinceActivated;
        }
        protected String Movement;
        protected double Cycle;
        protected double GreenTime;
        protected double RedTime;
        protected String CurrentStatus;
        protected double DurationSinceActivated;

        public String getMovement() {
            return Movement;
        }

        public double getGreenTime() {
            return GreenTime;
        }

        public String getCurrentStatus() {
            return CurrentStatus;
        }

        public double getDurationSinceActivated() {
            return DurationSinceActivated;
        }

        public double getRedTime() {
            return RedTime;
        }
    }

    /**
     *
     * @param network  AimsunNetworkByApproach class
     * @param queryMeasures Query measures
     * @param ActualFieldSigConnection Signal database connection
     * @return List<AimsunControlPlanJunction>
     */
    public static List<AimsunControlPlanJunction> GetActiveControlPlansForGivenDayAndTime(AimsunNetworkByApproach network
            ,QueryMeasures queryMeasures,Connection ActualFieldSigConnection){
        // This function is used to get active control plans: either from the field or Aimsun

        List<AimsunControlPlanJunction> ActiveControlPlans=new ArrayList<AimsunControlPlanJunction>();
        if(ActualFieldSigConnection==null) {
            // Currently using the control plan information in Aimsun which is coded in the field
            ActiveControlPlans = getSignalData.GetActiveControlPlansForGivenDayAndTimeFromAimsun
                    (network.getAimsunNetwork().getAimsunMasterControlPlanList(), network.getAimsunNetwork().getAimsunControlPlanJunctionList()
                            , queryMeasures.getDayOfWeek(), queryMeasures.getTimeOfDay()[0]);
        }else{
            // But can be overwritten by the actual input from the field in the future
        }

        return ActiveControlPlans;
    }

    /**
     *
     * @param aimsunMasterControlPlanList List<AimsunMasterControlPlan>
     * @param aimsunControlPlanJunctionList List<AimsunControlPlanJunction>
     * @param SelectedDayID SelectedDayID: 0:All, 1-7: Sunday-Saturday, 8: weekday, 9: weekend
     * @param Time Time (seconds)
     * @return
     */
    public static List<AimsunControlPlanJunction> GetActiveControlPlansForGivenDayAndTimeFromAimsun
            (List<AimsunMasterControlPlan> aimsunMasterControlPlanList, List<AimsunControlPlanJunction> aimsunControlPlanJunctionList
                    , int SelectedDayID, int Time){
        // This function is used to get active control plans from Aimsun

        // Initialization
        List<AimsunControlPlanJunction> ActiveControlPlans= new ArrayList<AimsunControlPlanJunction>();

        // Currently only consider two master control plans
        // This part can be revised if more information can be obtained in the future
        String MasterPlanName;
        if(SelectedDayID==0 || (SelectedDayID>=1&&SelectedDayID<=8))
            MasterPlanName="Weekday";
        else
            MasterPlanName="Weekend";

        if(aimsunMasterControlPlanList.size()==0){
            System.out.println("No master control plans available!");
        }else if(aimsunControlPlanJunctionList.size()==0){
            System.out.println("No control plans available!");
        }else{
            // Get active control plans
            List<Integer> ActiveControlPlanID=new ArrayList<Integer>();
            for(int i=0;i<aimsunMasterControlPlanList.size();i++){ // Loop for all master control plans
                if(aimsunMasterControlPlanList.get(i).getName().equals(MasterPlanName)){ // Loop for each master control plan
                    if(aimsunMasterControlPlanList.get(i).getStartingTime()<=Time &&
                            (aimsunMasterControlPlanList.get(i).getStartingTime()+aimsunMasterControlPlanList.get(i).getDuration())>=Time) {
                        // Within the time intervals
                        ActiveControlPlanID.add(aimsunMasterControlPlanList.get(i).getControlPlanID());
                    }
                }
            }
            // Obtain detailed information of active control plans
            if(ActiveControlPlanID.size()==0){
                System.out.println("No active control plans!");
            }else{
                for(int i=0;i<ActiveControlPlanID.size();i++){ // Loop for each active control plan ID
                    for(int j=0;j<aimsunControlPlanJunctionList.size();j++){// Loop for each existing control plan ID
                        if(aimsunControlPlanJunctionList.get(j).getPlanID()==ActiveControlPlanID.get(i)
                                &&aimsunControlPlanJunctionList.get(j).getCycle()!=0){
                            // Find the same ID and it is signal control
                            ActiveControlPlans.add(aimsunControlPlanJunctionList.get(j));
                            break;
                        }
                    }
                }
            }
        }
        return ActiveControlPlans;
    }

    /**
     *
     * @param aimsunControlPlanJunctionList List<AimsunControlPlanJunction>
     * @param aimsunApproach AimsunApproach
     * @return SignalSettings
     */
    public static SignalSettings GetGreenTimesForApproachFromSignalPlansInAimsun
            (List<AimsunControlPlanJunction> aimsunControlPlanJunctionList,AimsunApproach aimsunApproach){
        // This function is used to get the green times for approach from signal plans in Aimsun

        // Select the control plan for a given junction
        List<AimsunControlPlanJunction> SelectedControlPlans=new ArrayList<AimsunControlPlanJunction>();
        for(int i=0; i<aimsunControlPlanJunctionList.size();i++){// Loop for each active control plan
            if(aimsunApproach.getJunctionID()==aimsunControlPlanJunctionList.get(i).getJunctionID()){
                // Find the same junction ID
                if(!aimsunControlPlanJunctionList.get(i).getControlType().equals("Unspecified")){
                    // Ignore the "unspecified" control plan
                    SelectedControlPlans.add(aimsunControlPlanJunctionList.get(i));
                }
            }
        }

        if(SelectedControlPlans.size()==0){
            System.out.println("No active control plans for junction:"+aimsunApproach.getJunctionID());
            return null; // No active control plans
        }else if(SelectedControlPlans.size()>1){
            System.out.println("Too many active control plans for junction:"+aimsunApproach.getJunctionID());
            return null; // Can not decide which one to use
        }else{
            // Cycle length
            int CycleLength=SelectedControlPlans.get(0).getCycle();
            // Green time for left turn
            int LeftTurnGreen=FindGreenTimeByMovement(aimsunApproach,"Left Turn",SelectedControlPlans.get(0));
            // Green time for through
            int ThroughGreen=FindGreenTimeByMovement(aimsunApproach,"Through",SelectedControlPlans.get(0));
            // Green time for right turn
            int RightTurnGreen=FindGreenTimeByMovement(aimsunApproach,"Right Turn",SelectedControlPlans.get(0));
            // Check the type of left turns: protected, permitted, and protected-permitted
            String LeftTurnSetting=CheckLeftTurnSettings(aimsunApproach, SelectedControlPlans.get(0));

            SignalSettings signalSettings=new SignalSettings(CycleLength,LeftTurnGreen,ThroughGreen,RightTurnGreen,LeftTurnSetting);
            return signalSettings;
        }
    }

    /**
     *
     * @param aimsunApproach AimsunApproach
     * @param Movement Turning movement: Left Turn, Through, and Right Turn
     * @param CurrentControlPlan AimsunControlPlanJunction class
     * @return MaxGreen (seconds)
     */
    public static int FindGreenTimeByMovement(AimsunApproach aimsunApproach, String Movement,AimsunControlPlanJunction CurrentControlPlan){
        // This function is used to find green time by traffic movement

        // Get the turning information
        TurningBelongToApproach aimsunTurningInf=aimsunApproach.getTurningBelongToApproach();

        int MaxGreen=0;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.getTurningProperty();
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            // There may be multiple turnings with the same type, e.g., left turn
            if(aimsunTurningList.get(i).getMovement().equals(Movement)){
                //If find the corresponding turning movement
                int TurnID=aimsunTurningList.get(i).getTurnID();

                // Check the signals in which the turn is involved
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.getSignals().size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.getSignals().get(j).getNumTurnings();k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.getSignals().get(j).getTurningIDs()[k]==TurnID){
                            //Find the associated turn
                            SignalByTurn.add(CurrentControlPlan.getSignals().get(j).getSignalID()); // Append the signal ID
                            break;
                        }
                    }
                }
                // Check the total green time for the given turn
                int TotalGreen=0;
                for(int j=0;j<SignalByTurn.size();j++){// Loop for each involved signal
                    int SignalID=SignalByTurn.get(j);
                    for(int k=0;k<CurrentControlPlan.getPhases().size();k++){ // Loop for each phase
                        for(int t=0;t<CurrentControlPlan.getPhases().get(k).getNumSignalInPhase();t++){
                            // Check whether the signal belongs to the phase or not
                            if(CurrentControlPlan.getPhases().get(k).getSignalIDs()[t]==SignalID){
                                // For a turn involved in multiple signals, add up the green times
                                TotalGreen=TotalGreen+(int)CurrentControlPlan.getPhases().get(k).getDuration();
                            }
                        }
                    }
                }
                // Update the maximum green time
                if(MaxGreen<TotalGreen){
                    MaxGreen=TotalGreen;
                }
            }
        }
        return MaxGreen;
    }


    /**
     *
     * @param aimsunApproach AimsunApproach
     * @param CurrentControlPlan Current control plan; AimsunControlPlanJunction class
     * @return String: Permitted, Protected, Protected-Permitted, N/A
     */
    public static String CheckLeftTurnSettings(AimsunApproach aimsunApproach, AimsunControlPlanJunction CurrentControlPlan){
        // This function is used to check the left-turn settings
        // A left-turn is protected when: no other conflicting movements exist
        // Two steps: (i) Check the turns with a phase to see whether there exist conflicting through movements
        // (ii) Check the conflicting turns in other phases which share a portion of activation time.

        // Get the turning information
        TurningBelongToApproach aimsunTurningInf=aimsunApproach.getTurningBelongToApproach();
        List<AimsunTurning> aimsunTurningInJunction=aimsunApproach.getAimsunJunction().getTurnings();

        boolean[] Status=CheckConflictingMovementInPhaseForLeftTurn(aimsunTurningInf, aimsunTurningInJunction, CurrentControlPlan);
        boolean IsProtected=Status[0];
        boolean IsPermitted=Status[1];
        if(!IsPermitted){
            // If within a phase, the left-turn is not permitted. Need to check the movements in other phases.
            // Else, it should be "Permitted"
            Status=CheckConflictingMovementOtherPhaseForLeftTurn(aimsunTurningInf,aimsunTurningInJunction,
                    CurrentControlPlan);
            IsProtected=Status[0];
            IsPermitted=Status[1];
        }

        // Return the settings
        if(IsPermitted && !IsProtected)
            return "Permitted";
        else if(!IsPermitted && IsProtected)
            return "Protected";
        else if(IsPermitted && IsProtected)
            return "Protected-Permitted";
        else {
            System.out.println("No left-turn settings!");
            return "N/A";
        }
    }

    /**
     *
     * @param aimsunTurningInf TurningBelongToApproach class
     * @param aimsunTurningInJunction List<AimsunTurning>
     * @param CurrentControlPlan Current control plan; AimsunControlPlanJunction class
     * @return  boolean[]{IsProtected,IsPermitted}
     */
    public static boolean[] CheckConflictingMovementInPhaseForLeftTurn(TurningBelongToApproach aimsunTurningInf,
            List<AimsunTurning> aimsunTurningInJunction,AimsunControlPlanJunction CurrentControlPlan){
        // Check whether there exist conflicting movements in the same phase for left turn

        // Note: this algorithm is designed for the Aimsun structure of Signal settings
        // I.e., Turn \in Signal Group \in Phase
        // Normally, we don't see a turn is involved in more than one signal group.

        boolean IsProtected = false;
        boolean IsPermitted=false;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.getTurningProperty();
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            int FirstSectionID=aimsunTurningList.get(i).getOrigSectionID();
            if(aimsunTurningList.get(i).getMovement().equals("Left Turn")){
                //If find the corresponding turning movement

                // Get the involved signals: movements within the same signals are OK
                int TurnID=aimsunTurningList.get(i).getTurnID();
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.getSignals().size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.getSignals().get(j).getNumTurnings();k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.getSignals().get(j).getTurningIDs()[k]==TurnID){
                            // Find the corresponding Signal movement
                            SignalByTurn.add(CurrentControlPlan.getSignals().get(j).getSignalID());
                            break;
                        }
                    }
                }

                // Check other signals within the same phase
                // It is possible to have conflicting movements within a phase, e.g., permitted left-turn & Through movements
                for(int j=0;j<SignalByTurn.size();j++){ // Loop for each signal group
                    for(int k=0;k<CurrentControlPlan.getPhases().size();k++){// Loop for each phase

                        List<Integer> OtherSignal=new ArrayList<Integer>();
                        for(int p=0;p<CurrentControlPlan.getPhases().get(k).getNumSignalInPhase();p++){
                            if(CurrentControlPlan.getPhases().get(k).getSignalIDs()[p]==SignalByTurn.get(j)){
                                // If find the signal ID
                                for(int t=0;t<CurrentControlPlan.getPhases().get(k).getNumSignalInPhase();t++){
                                    if(t!=p){// Add other signals
                                        OtherSignal.add(CurrentControlPlan.getPhases().get(k).getSignalIDs()[t]);
                                    }
                                }
                                break;
                            }
                        }

                        // Check other turning movements in these signals
                        List<Integer> OtherTurns=new ArrayList<Integer>();
                        for(int t=0;t<OtherSignal.size();t++){
                            for(int s=0;s<CurrentControlPlan.getSignals().size();s++){
                                if(CurrentControlPlan.getSignals().get(s).getSignalID()==OtherSignal.get(t)){
                                    // Find the same signal ID
                                    for(int p=0;p<CurrentControlPlan.getSignals().get(s).getNumTurnings();p++){
                                        OtherTurns.add(CurrentControlPlan.getSignals().get(s).getTurningIDs()[p]);
                                    }
                                    break;
                                }
                            }
                        }

                        // Check conflicting turning movements: especially for through movements
                        int NumThroughMovements=0;
                        for(int t=0;t<OtherTurns.size();t++){ // Loop for other turns
                            for(int s=0;s<aimsunTurningInJunction.size();s++){ // Loop for all turns inside the junction
                                if(OtherTurns.get(t)==aimsunTurningInJunction.get(s).getTurnID()){// Find the turn ID
                                    if(aimsunTurningInJunction.get(s).getOrigSectionID()!=FirstSectionID
                                            && aimsunTurningInJunction.get(s).getMovement().equals("Through")){
                                        // Not the same section and is a through movement
                                        NumThroughMovements=NumThroughMovements+1;
                                    }
                                }
                            }
                        }

                        // If it has conflicting through movements with a phase
                        if(NumThroughMovements>0) {
                            // If conflicting movements are found, e.g., phase 4 is assigned to the N-S directions, then the "left-turn" is permitted.
                            IsPermitted = true;
                        }else{
                            // If within a given phase, no conflicting movement is found. Then the "left-turn" should be protected in this phase
                            // e.g., two left turns in the east- and west- bounds
                            IsProtected =true;
                        }

                    }
                }
            }
        }

        // Return the results
        return new boolean[]{IsProtected,IsPermitted};
    }

    /**
     *
     * @param aimsunTurningInf TurningBelongToApproach class
     * @param aimsunTurningInJunction List<AimsunTurning>
     * @param CurrentControlPlan Current control plan
     * @return boolean[]{IsProtected,IsPermitted}
     */
    public static boolean[] CheckConflictingMovementOtherPhaseForLeftTurn(
            TurningBelongToApproach aimsunTurningInf,List<AimsunTurning> aimsunTurningInJunction,
            AimsunControlPlanJunction CurrentControlPlan){
        // This function is used to check conflicting traffic movements in other phases for left turn

        boolean IsProtected=false;
        boolean IsPermitted=false;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.getTurningProperty();
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            int FirstSectionID=aimsunTurningList.get(i).getOrigSectionID();
            if(aimsunTurningList.get(i).getMovement().equals("Left Turn")){
                //If find the corresponding turning movement

                // Get the involved signals: movements within the same signals are OK
                int TurnID=aimsunTurningList.get(i).getTurnID();
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.getSignals().size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.getSignals().get(j).getNumTurnings();k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.getSignals().get(j).getTurningIDs()[k]==TurnID){
                            // Find the corresponding Signal movement
                            SignalByTurn.add(CurrentControlPlan.getSignals().get(j).getSignalID());
                            break;
                        }
                    }
                }

                // Check the phase ID, start time, and end time
                List<int []> PhaseIDAndTime=new ArrayList<int[]>();
                for(int j=0;j<SignalByTurn.size();j++){
                    for(int k=0;k<CurrentControlPlan.getPhases().size();k++){
                        for(int p=0;p<CurrentControlPlan.getPhases().get(k).getNumSignalInPhase();p++){
                            if(CurrentControlPlan.getPhases().get(k).getSignalIDs()[p]==SignalByTurn.get(j)){
                                // If find the signal ID
                                int PhaseID=CurrentControlPlan.getPhases().get(k).getPhaseID();
                                int StartTime=(int)CurrentControlPlan.getPhases().get(k).getStartingTime();
                                int EndTime= StartTime+(int)(CurrentControlPlan.getPhases().get(k).getDuration());
                                PhaseIDAndTime.add(new int[]{PhaseID,StartTime,EndTime});
                                break;
                            }
                        }
                    }
                }
                // Check other phases having the overlap of activation times
                List<List<Integer>> OtherTurns=new ArrayList<List<Integer>>();
                boolean FindOverlapPhase;
                for(int j=0;j<PhaseIDAndTime.size();j++){
                    FindOverlapPhase=false;
                    for(int k=0;k<CurrentControlPlan.getPhases().size();k++){
                        if(CurrentControlPlan.getPhases().get(k).getPhaseID()!=PhaseIDAndTime.get(j)[0]){
                            int tmpStartTime=(int)CurrentControlPlan.getPhases().get(k).getStartingTime();
                            int tmpEndTime=tmpStartTime+(int) CurrentControlPlan.getPhases().get(k).getDuration();
                            if(PhaseIDAndTime.get(j)[1]>=tmpStartTime && PhaseIDAndTime.get(j)[1]<=tmpEndTime){
                                // Target phase start time is with the current phase
                                FindOverlapPhase=true;
                            }
                            if(PhaseIDAndTime.get(j)[2]>=tmpStartTime && PhaseIDAndTime.get(j)[2]<=tmpEndTime){
                                // Target phase end time is with the current phase
                                FindOverlapPhase=true;
                            }
                            if(tmpStartTime>=PhaseIDAndTime.get(j)[1] && tmpStartTime<=PhaseIDAndTime.get(j)[2]){
                                // Current phase start time is within the target phase
                                FindOverlapPhase=true;
                            }
                            if(tmpEndTime>=PhaseIDAndTime.get(j)[1] && tmpEndTime<=PhaseIDAndTime.get(j)[2]){
                                // Current phase end time is within the target phase
                                FindOverlapPhase=true;
                            }
                            if(FindOverlapPhase){ // If found
                                List<Integer> TurnInPhase=new ArrayList<Integer>();
                                for(int p=0; p<CurrentControlPlan.getPhases().get(k).getNumSignalInPhase();p++){
                                    // Loop for the signals within the phase
                                    int tmpSignalID=CurrentControlPlan.getPhases().get(k).getSignalIDs()[p];
                                    for(int t=0; t<CurrentControlPlan.getNumSignals();t++){
                                        if(CurrentControlPlan.getSignals().get(t).getSignalID()==tmpSignalID){
                                            // Add all turns associated to the phase
                                            for(int s=0; s<CurrentControlPlan.getSignals().get(t).getNumTurnings();s++){
                                                TurnInPhase.add(CurrentControlPlan.getSignals().get(t).getTurningIDs()[s]);
                                            }
                                        }
                                    }
                                }
                                //Append the associated turns
                                OtherTurns.add(TurnInPhase);
                            }
                            break;
                        }
                    }
                }
                // Check conflicting turning movements: especially for through movements
                if(OtherTurns.size()==0){// No Other turns
                    IsProtected=true;
                }else {
                    for (int j = 0; j < OtherTurns.size(); j++) { // Loop for the turns in each phase
                        int NumThroughMovements = 0;
                        for (int p = 0; p < OtherTurns.get(j).size(); p++) {
                            for (int k = 0; k < aimsunTurningInJunction.size(); k++) {
                                // Loop for all turns inside the junction
                                if (OtherTurns.get(j).get(p) == aimsunTurningInJunction.get(k).getTurnID()) {
                                    // Find the turn ID
                                    if (aimsunTurningInJunction.get(k).getOrigSectionID() != FirstSectionID
                                            && aimsunTurningInJunction.get(k).getMovement().equals("Through")) {
                                        // Not the same section and is a through movement
                                        NumThroughMovements = NumThroughMovements + 1;
                                    }
                                }
                            }
                        }
                        if (NumThroughMovements == 0) { // If in other phase, no conflicting movements
                            IsProtected = true;
                        } else {// Or else, permitted
                            IsPermitted = true;
                        }
                    }
                }
            }
        }

        return new boolean[]{IsProtected,IsPermitted};
    }


    /**
     *
     * @param aimsunApproach  AimsunApproach class
     * @param Time Time (seconds)
     * @param controlPlanJunctionList List<AimsunControlPlanJunction> class
     * @param parameters Parameters class
     * @return PhaseInfForApproach class
     */
    public static PhaseInfForApproach GetSignalPhasingForGivenApproachAndTimeFromAimsun(AimsunApproach aimsunApproach, double Time
            , List<AimsunControlPlanJunction> controlPlanJunctionList,Parameters parameters){
        // This function is used to get signal phasing information for a given approach and time

        int JunctionID=aimsunApproach.getJunctionID();
        int FirstSectionID=aimsunApproach.getFirstSectionID();
        List<AimsunTurning> aimsunTurningList=aimsunApproach.getTurningBelongToApproach().getTurningProperty();

        // Get the control plan in the junction
        AimsunControlPlanJunction aimsunControlPlanJunction=null;
        for(int i=0;i<controlPlanJunctionList.size();i++){
            if(controlPlanJunctionList.get(i).getJunctionID()==JunctionID){
                aimsunControlPlanJunction=controlPlanJunctionList.get(i);
                break;
            }
        }

        List<SignalByMovement> signalByMovementList = new ArrayList<SignalByMovement>();
        SignalSettings signalSettings=parameters.getSignalSettings();
        if(aimsunControlPlanJunction==null){
            System.out.println("No control plans in Aimsun for Junction:"+JunctionID);
            // If there is no control plan information, use the default settings
            // Assume it is red period and has been activated for 1/3 of the time.
            for (int i = 0; i < aimsunTurningList.size(); i++) {
                if(aimsunTurningList.get(i).getMovement().equals("Left Turn")){
                    signalByMovementList.add(new SignalByMovement(aimsunTurningList.get(i).getMovement(),signalSettings.getCycleLength()
                            , signalSettings.getLeftTurnGreen(),signalSettings.getCycleLength() - signalSettings.getLeftTurnGreen()
                            ,"Red",(signalSettings.getLeftTurnGreen()/3.0)));
                }else{
                    signalByMovementList.add(new SignalByMovement(aimsunTurningList.get(i).getMovement(),signalSettings.getCycleLength()
                            , signalSettings.getThroughGreen(),signalSettings.getCycleLength() - signalSettings.getThroughGreen()
                            ,"Red",(signalSettings.getThroughGreen()/3.0)));
                }
            }
            return new PhaseInfForApproach(JunctionID,FirstSectionID,Time,signalByMovementList);
        }else {
            // Get the end time of the last cycle and the actuation duration given the input of current time
            double [] EndTimeAndActivationDuration=DetermineTimeOfLastCycleForJunctionFromAimsun(aimsunControlPlanJunction, Time);
            double ActivatedDurationInOneCycle=EndTimeAndActivationDuration[0];
            double EndTimeOfLastCycle=EndTimeAndActivationDuration[1];

            // Get the signal phase information by movement
            for (int i = 0; i < aimsunTurningList.size(); i++) {
                SignalByMovement signalByMovement = DetermineSignalStateByMovementForGivenApproachAndTime(aimsunControlPlanJunction
                        , aimsunTurningList.get(i),ActivatedDurationInOneCycle);
                signalByMovementList.add(signalByMovement);
            }

            //Return the phase information for a given approach and time
            PhaseInfForApproach phaseInfForApproach = new PhaseInfForApproach(JunctionID, FirstSectionID, Time, signalByMovementList);
            return phaseInfForApproach;
        }
    }

    /**
     *
     * @param aimsunControlPlanJunction AimsunControlPlanJunction
     * @param CurrentTime Current time (seconds)
     * @return double []{EndTimeOfLastCycle,ActivatedDurationInOneCycle}
     */
    public static double[] DetermineTimeOfLastCycleForJunctionFromAimsun(AimsunControlPlanJunction aimsunControlPlanJunction
            , double CurrentTime){
        // This function is used to determine the time of last cycle for a given junction in Aimsun

        int ControlPlanStartTime=aimsunControlPlanJunction.getPlanOffset();
        List<AimsunRing> RingInf=aimsunControlPlanJunction.getRings(); // It contains the coordination information inside

        List<AimsunPhase> aimsunPhases=aimsunControlPlanJunction.getPhases();
        double [][] PhaseIDStartTimeDuration=new  double[aimsunPhases.size()][3];
        for(int i=0;i<aimsunPhases.size();i++){
            PhaseIDStartTimeDuration[i][0]=aimsunPhases.get(i).getPhaseID();
            PhaseIDStartTimeDuration[i][1]=aimsunPhases.get(i).getStartingTime();
            PhaseIDStartTimeDuration[i][2]=aimsunPhases.get(i).getDuration();
        }

        // Check whether the ring is coordinated (if coordinated, the phase ID is greater than zero)
        boolean IsCoordinated=false;
        int CoordinatedPhaseID=0;
        double PhaseOffset=0;
        int FromEndOfPhase=0;
        for(int i=0;i<RingInf.size();i++){
            if(RingInf.get(i).getCoordinatedPhase()>0){
                IsCoordinated=true;
                CoordinatedPhaseID=RingInf.get(i).getCoordinatedPhase();
                PhaseOffset=RingInf.get(i).getOffset();
                FromEndOfPhase=RingInf.get(i).getGetMatchesOffsetWithEndOfPhase();
                break;
            }
        }

        double TimeControlPlanIsActivated;
        double TimeControlPlanHasActivated;
        double ActivatedDurationInOneCycle;
        double EndTimeOfLastCycle;
        double PhaseStartTime=0;
        double PhaseDuration=0;
        if(!IsCoordinated){// If it is not coordinated
            TimeControlPlanIsActivated=ControlPlanStartTime;
            TimeControlPlanHasActivated=CurrentTime-TimeControlPlanIsActivated;
            if(TimeControlPlanHasActivated<0) //If the plan is activated earlier than the scheduled one
            {
                int CyclesShiftedLeft=(int)Math.ceil(Math.abs(TimeControlPlanHasActivated)/aimsunControlPlanJunction.getCycle());
                TimeControlPlanIsActivated=TimeControlPlanIsActivated-CyclesShiftedLeft*aimsunControlPlanJunction.getCycle();// Shifted left
                TimeControlPlanHasActivated=CurrentTime-TimeControlPlanIsActivated;// Update the activation duration
            }
            ActivatedDurationInOneCycle=TimeControlPlanHasActivated % aimsunControlPlanJunction.getCycle(); // Get the modulus
            EndTimeOfLastCycle=CurrentTime-ActivatedDurationInOneCycle; // Update the end time of the last cycle
        }else{ // If it is coordinated
            for(int i=0;i<PhaseIDStartTimeDuration.length;i++){
                if(PhaseIDStartTimeDuration[i][0]==CoordinatedPhaseID){
                    PhaseStartTime=PhaseIDStartTimeDuration[i][1];
                    PhaseDuration=PhaseIDStartTimeDuration[i][2];
                }
            }
            double TimeElapse;
            if(FromEndOfPhase==1){ // Starting from the end of phase
                TimeElapse=PhaseStartTime+PhaseDuration;
            }else{
                TimeElapse=PhaseStartTime;
            }

            TimeControlPlanIsActivated=ControlPlanStartTime+PhaseOffset-TimeElapse; // Get the starting time of a plan with coordination
            TimeControlPlanHasActivated=CurrentTime-TimeControlPlanIsActivated;
            if(TimeControlPlanHasActivated<0) //If the plan is activated earlier than the scheduled one
            {
                int CyclesShiftedLeft=(int)Math.ceil(Math.abs(TimeControlPlanHasActivated)/aimsunControlPlanJunction.getCycle());
                TimeControlPlanIsActivated=TimeControlPlanIsActivated-CyclesShiftedLeft*aimsunControlPlanJunction.getCycle();// Shifted left
                TimeControlPlanHasActivated=CurrentTime-TimeControlPlanIsActivated;// Update the activation duration
            }
            ActivatedDurationInOneCycle=TimeControlPlanHasActivated % aimsunControlPlanJunction.getCycle(); // Get the modulus
            EndTimeOfLastCycle=CurrentTime-ActivatedDurationInOneCycle; // Update the end time of the last cycle
        }



        return new double []{EndTimeOfLastCycle,ActivatedDurationInOneCycle};
    }

    /**
     *
     * @param aimsunControlPlanJunction  AimsunControlPlanJunction class
     * @param aimsunTurning AimsunTurning class
     * @param ActivatedDurationInOneCycle Activated duration in one cycle (seconds)
     * @return SignalByMovement class
     */
    public static SignalByMovement DetermineSignalStateByMovementForGivenApproachAndTime(AimsunControlPlanJunction aimsunControlPlanJunction
            ,AimsunTurning aimsunTurning, double ActivatedDurationInOneCycle){
        // This function is used to determine the signal state for a given turning movement at given approach and time

        int TurnID=aimsunTurning.getTurnID();
        double Cycle=aimsunControlPlanJunction.getCycle();
        int NumSignals=aimsunControlPlanJunction.getNumSignals();

        // Check the signals associated with a given turning ID
        List<Integer> SignalInTurn=new ArrayList<Integer>();
        for (int i=0;i<NumSignals;i++){ // Loop for each signal
            int SignalID=aimsunControlPlanJunction.getSignals().get(i).getSignalID();
            int[] TurnInSignal=aimsunControlPlanJunction.getSignals().get(i).getTurningIDs();// Get the turn IDs with a given signal
            for(int j=0;j<TurnInSignal.length;j++){// Loop for each turn
                if(TurnInSignal[j]==TurnID){ // Find the right turn ID
                    SignalInTurn.add(SignalID);
                }
            }
        }
        if(SignalInTurn.size()==0){ // If no signals are found
            System.out.println("No signal movement is associated with the turn:"+TurnID
                    +" at Junction:" + aimsunControlPlanJunction.getJunctionID());
            System.exit(-1);
        }

        // Check the phases associated with the given turning ID
        int NumPhases=aimsunControlPlanJunction.getNumPhases();
        List<AimsunPhase> aimsunPhaseList=aimsunControlPlanJunction.getPhases();
        List<AimsunPhase> PhaseInTurn= new ArrayList<AimsunPhase>();
        for(int i=0;i<NumPhases;i++){ // For each phase
            for(int j=0;j<aimsunPhaseList.get(i).getNumSignalInPhase();j++){ // For each signal in a phase
                for(int k=0;k<SignalInTurn.size();k++){ // For each signal with a given turning ID
                    if(aimsunPhaseList.get(i).getSignalIDs()[j]==SignalInTurn.get(k)){ // Find the same signal ID
                        PhaseInTurn.add(aimsunPhaseList.get(i));
                        break;
                    }
                }
            }
        }

        // Get the total green and total red: it can work under the case when a turn is activated in multiple phases
        double TotalGreen=0;
        for(int i=0;i<PhaseInTurn.size();i++){
            TotalGreen=TotalGreen+PhaseInTurn.get(i).getDuration();
        }
        double TotalRed=aimsunControlPlanJunction.getCycle()-TotalGreen;
        if(TotalRed<0){
            System.out.println("Incorrect signal setting (TotalGreen+TotalRed>Cycle) for Turn:"+TurnID);
        }

        // Find whether it is in the green period
        int PhaseIndex=-1;
        for(int i=0;i<PhaseInTurn.size();i++){ // Loop for each phase
            if(PhaseInTurn.get(i).getStartingTime()<ActivatedDurationInOneCycle &&
                    PhaseInTurn.get(i).getStartingTime()+PhaseInTurn.get(i).getDuration()>ActivatedDurationInOneCycle){
                // Within the phase: Green; otherwise: Red
                PhaseIndex=i;
                break;
            }
        }
        String Status;
        double HasBeenActivated;
        if(PhaseIndex>=0){// Non-negative means it is in the green period
            Status="Green";
            HasBeenActivated=ActivatedDurationInOneCycle-PhaseInTurn.get(PhaseIndex).getStartingTime();
            for(int i=0;i<PhaseInTurn.size();i++){ // Search for the green time in the phases ahead
                if(i!=PhaseIndex &&
                        PhaseInTurn.get(PhaseIndex).getStartingTime()>(PhaseInTurn.get(i).getStartingTime()+PhaseInTurn.get(i).getDuration())){
                    HasBeenActivated=HasBeenActivated+PhaseInTurn.get(i).getDuration();
                }
            }
        }else{
            Status="Red";
            // Get the minimum distance between the end of phase and the end of a cycle
            double DistancePhaseEndToCycleEnd=aimsunControlPlanJunction.getCycle();
            for(int i=0;i<PhaseInTurn.size();i++){ // Loop for each phase
                double tmpTime=aimsunControlPlanJunction.getCycle()-(PhaseInTurn.get(i).getStartingTime()+PhaseInTurn.get(i).getDuration());
                if(DistancePhaseEndToCycleEnd>tmpTime){
                    DistancePhaseEndToCycleEnd=tmpTime;
                }
            }
            // Get the time duration that the Red status has been activated
            HasBeenActivated=ActivatedDurationInOneCycle+DistancePhaseEndToCycleEnd;
            double tmpHasBeenActivated=HasBeenActivated;
            double PhaseRedIndex=-1;
            for(int i=0;i<PhaseInTurn.size();i++){// Loop for each phase
                if(ActivatedDurationInOneCycle>PhaseInTurn.get(i).getStartingTime()+PhaseInTurn.get(i).getDuration()){
                    // Find the current time is greater than the end of the phase
                    double tmpDifference=ActivatedDurationInOneCycle-PhaseInTurn.get(i).getStartingTime()-PhaseInTurn.get(i).getDuration();
                    if(tmpDifference<tmpHasBeenActivated){// Get the minimum distance & save the index
                        tmpHasBeenActivated=tmpDifference;
                        PhaseRedIndex=i;
                    }
                }
            }
            if(PhaseRedIndex>=0){ // When there exist phases that have been activated before ActivatedDurationInOneCycle
                // Update the value: HasBeenActivated
                HasBeenActivated=ActivatedDurationInOneCycle;
                for(int i=0;i<PhaseInTurn.size();i++){ // Search for the phases ahead
                    if(i!=PhaseRedIndex &&
                            ActivatedDurationInOneCycle>(PhaseInTurn.get(i).getStartingTime()+PhaseInTurn.get(i).getDuration())){
                        HasBeenActivated=HasBeenActivated-PhaseInTurn.get(i).getDuration();// Subtract the green time
                    }
                }
            }
        }

        SignalByMovement signalByMovement=new SignalByMovement(aimsunTurning.getMovement(),aimsunControlPlanJunction.getCycle(),
                TotalGreen,TotalRed,Status,HasBeenActivated);
        return signalByMovement;
    }

}
