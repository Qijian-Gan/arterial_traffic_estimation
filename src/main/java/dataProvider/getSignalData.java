package dataProvider;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import estimation.trafficStateEstimation.*;
import networkInput.readFromAimsun.*;
import networkInput.reconstructNetwork.*;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qijian-Gan on 12/8/2017.
 */
public class getSignalData {

    public static List<AimsunControlPlanJunction> GetActiveControlPlansForGivenDayAndTimeFromAimsun
            (List<AimsunMasterControlPlan> aimsunMasterControlPlanList, List<AimsunControlPlanJunction>
                    aimsunControlPlanJunctionList, int SelectedDayID, int Time){
        // This function is used to get active control plans from Aimsun
        // SelectedDayID: 0:All, 1-7: Sunday-Saturday, 8: weekday, 9: weekend

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
                if(aimsunMasterControlPlanList.get(i).Name.equals(MasterPlanName)){ // Loop for each master control plan
                    if(aimsunMasterControlPlanList.get(i).StartingTime<=Time &&
                            (aimsunMasterControlPlanList.get(i).StartingTime+aimsunMasterControlPlanList.get(i).Duration)>=Time) {
                        // Within the time intervals
                        ActiveControlPlanID.add(aimsunMasterControlPlanList.get(i).ControlPlanID);
                    }
                }
            }
            // Obtain detailed information of active control plans
            if(ActiveControlPlanID.size()==0){
                System.out.println("No active control plans!");
            }else{
                for(int i=0;i<ActiveControlPlanID.size();i++){ // Loop for each active control plan ID
                    for(int j=0;j<aimsunControlPlanJunctionList.size();j++){// Loop for each existing control plan ID
                        if(aimsunControlPlanJunctionList.get(j).PlanID==ActiveControlPlanID.get(i)
                                &&aimsunControlPlanJunctionList.get(j).Cycle!=0){
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

    public static SignalSettings GetGreenTimesForApproachFromSignalPlansInAimsun
            (List<AimsunControlPlanJunction> aimsunControlPlanJunctionList,AimsunApproach aimsunApproach){
        // This function is used to get the green times for approach from signal plans in Aimsun

        List<AimsunControlPlanJunction> SelectedControlPlans=new ArrayList<AimsunControlPlanJunction>();
        for(int i=0; i<aimsunControlPlanJunctionList.size();i++){// Loop for each active control plan
            if(aimsunApproach.JunctionID==aimsunControlPlanJunctionList.get(i).JunctionID){
                // Find the same junction ID
                if(!aimsunControlPlanJunctionList.get(i).ControlType.equals("Unspecified")){
                    // Ignore the "unspecified" control plan
                    SelectedControlPlans.add(aimsunControlPlanJunctionList.get(i));
                }
            }
        }

        if(SelectedControlPlans.size()==0){
            System.out.println("No active control plans for junction:"+aimsunApproach.JunctionID);
            return null; // No active control plans
        }else if(SelectedControlPlans.size()>1){
            System.out.println("Too many active control plans for junction:"+aimsunApproach.JunctionID);
            return null; // Can not decide which one to use
        }else{
            // Cycle length
            int CycleLength=SelectedControlPlans.get(0).Cycle;
            // Green time for left turn
            int LeftTurnGreen=FindGreenTimeByMovement(aimsunApproach,"Left Turn",SelectedControlPlans.get(0));
            // Green time for through
            int ThroughGreen=FindGreenTimeByMovement(aimsunApproach,"Through",SelectedControlPlans.get(0));
            // Green time for right turn
            int RightTurnGreen=FindGreenTimeByMovement(aimsunApproach,"Right Turn",SelectedControlPlans.get(0));
            String LeftTurnSetting=CheckLeftTurnSettings(aimsunApproach, SelectedControlPlans.get(0));


            SignalSettings signalSettings=new SignalSettings(CycleLength,LeftTurnGreen,ThroughGreen,
                    RightTurnGreen,LeftTurnSetting);
            return signalSettings;
        }
    }

    public static int FindGreenTimeByMovement(AimsunApproach aimsunApproach, String Movement,AimsunControlPlanJunction
            CurrentControlPlan){
        // This function is used to find green time by traffic movement

        // Get the turning information
        TurningBelongToApproach aimsunTurningInf=aimsunApproach.turningBelongToApproach;

        int MaxGreen=0;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.TurningProperty;
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            // There may be multiple turnings with the same type, e.g., left turn
            if(aimsunTurningList.get(i).Movement.equals(Movement)){
                //If find the corresponding turning movement
                int TurnID=aimsunTurningList.get(i).TurnID;
                // Check the signals in which the turn is involved
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.Signals.size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.Signals.get(j).NumTurnings;k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.Signals.get(j).TurningIDs[k]==TurnID){
                            //Find the associated turn
                            SignalByTurn.add(CurrentControlPlan.Signals.get(j).SignalID); // Append the signal ID
                            break;
                        }
                    }
                }
                // Check the total green time for the given turn
                int TotalGreen=0;
                for(int j=0;j<SignalByTurn.size();j++){// Loop for each involved signal
                    int SignalID=SignalByTurn.get(j);
                    for(int k=0;k<CurrentControlPlan.Phases.size();k++){ // Loop for each phase
                        for(int t=0;t<CurrentControlPlan.Phases.get(k).NumSignalInPhase;t++){
                            // Check whether the signal belongs to the phase or not
                            if(CurrentControlPlan.Phases.get(k).SignalIDs[t]==SignalID){
                                // If yes, add up the green durations
                                TotalGreen=TotalGreen+(int)CurrentControlPlan.Phases.get(k).Duration;
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


    public static String CheckLeftTurnSettings(AimsunApproach aimsunApproach,
                                            AimsunControlPlanJunction CurrentControlPlan){
        // This function is used to check the left-turn settings
        // A left-turn is protected when: no other conflicting movements exist
        // Two steps: (i) Check the turns with a phase to see whether there exist conflicting through movements
        // (ii) Check the conflicting turns in other phases which share a portion of activation time.

        // Get the turning information
        TurningBelongToApproach aimsunTurningInf=aimsunApproach.turningBelongToApproach;
        List<AimsunTurning> aimsunTurningInJunction=aimsunApproach.aimsunJunction.Turnings;

        boolean[] Status=CheckConflictingMovementInPhaseForLeftTurn(aimsunTurningInf, aimsunTurningInJunction,
                CurrentControlPlan);
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

    public static boolean[] CheckConflictingMovementInPhaseForLeftTurn(TurningBelongToApproach aimsunTurningInf,
            List<AimsunTurning> aimsunTurningInJunction,AimsunControlPlanJunction CurrentControlPlan){
        // Check whether there exist conflicting movements in the same phase for left turn

        boolean IsProtected;
        boolean IsPermitted=false;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.TurningProperty;
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            int FirstSectionID=aimsunTurningList.get(i).OrigSectionID;
            if(aimsunTurningList.get(i).Movement.equals("Left Turn")){
                //If find the corresponding turning movement
                // Get the involved signals: movements within the same signals are OK
                int TurnID=aimsunTurningList.get(i).TurnID;
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.Signals.size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.Signals.get(j).NumTurnings;k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.Signals.get(j).TurningIDs[k]==TurnID){
                            // Find the corresponding Signal movement
                            SignalByTurn.add(CurrentControlPlan.Signals.get(j).SignalID);
                            break;
                        }
                    }
                }
                // Check other signals within the same phase
                // It is possible to have conflicting movements within a phase, e.g., permitted left-turn & Through movements
                List<Integer> OtherSignal=new ArrayList<Integer>();
                for(int j=0;j<SignalByTurn.size();j++){
                    for(int k=0;k<CurrentControlPlan.Phases.size();k++){
                        for(int p=0;p<CurrentControlPlan.Phases.get(k).NumSignalInPhase;p++){
                            if(CurrentControlPlan.Phases.get(k).SignalIDs[p]==SignalByTurn.get(j)){
                                // If find the signal ID
                                for(int t=0;t<CurrentControlPlan.Phases.get(k).NumSignalInPhase;t++){
                                    if(t!=p){// Add other signals
                                        OtherSignal.add(CurrentControlPlan.Phases.get(k).SignalIDs[t]);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                // Check other turning movements in these signals
                List<Integer> OtherTurns=new ArrayList<Integer>();
                for(int j=0;j<OtherSignal.size();j++){
                    for(int k=0;k<CurrentControlPlan.Signals.size();k++){
                        if(CurrentControlPlan.Signals.get(k).SignalID==OtherSignal.get(j)){
                            // Find the same signal ID
                            for(int p=0;p<CurrentControlPlan.Signals.get(k).NumTurnings;p++){
                                OtherTurns.add(CurrentControlPlan.Signals.get(k).TurningIDs[p]);
                            }
                            break;
                        }
                    }
                }
                // Check conflicting turning movements: especially for through movements
                int NumThroughMovements=0;
                for(int j=0;j<OtherTurns.size();j++){ // Loop for other turns
                    for(int k=0;k<aimsunTurningInJunction.size();k++){ // Loop for all turns inside the junction
                        if(OtherTurns.get(j)==aimsunTurningInJunction.get(k).TurnID){// Find the turn ID
                            if(aimsunTurningInJunction.get(k).OrigSectionID!=FirstSectionID
                                    && aimsunTurningInJunction.get(k).Movement.equals("Through")){
                                // Not the same section and is a through movement
                                NumThroughMovements=NumThroughMovements+1;
                            }
                        }
                    }
                }
                // If it has conflicting through movements with a phase
                if(NumThroughMovements>0)
                    IsPermitted=true;
            }
        }
        // Return the results
        // If the left-turn is permitted with a phase, it should not be protected
        if(IsPermitted)
            IsProtected=false;
        else
            IsProtected=true;
        return new boolean[]{IsProtected,IsPermitted};
    }

    public static boolean[] CheckConflictingMovementOtherPhaseForLeftTurn(
            TurningBelongToApproach aimsunTurningInf,List<AimsunTurning> aimsunTurningInJunction,
            AimsunControlPlanJunction CurrentControlPlan){
        // This function is used to check conflicting traffic movements in other phases for left turn

        boolean IsProtected=false;
        boolean IsPermitted=false;
        // Check the corresponding turning movements
        List<AimsunTurning> aimsunTurningList=aimsunTurningInf.TurningProperty;
        for(int i=0;i<aimsunTurningList.size();i++){
            // Loop for each turn for that given approach
            int FirstSectionID=aimsunTurningList.get(i).OrigSectionID;
            if(aimsunTurningList.get(i).Movement.equals("Left Turn")){
                //If find the corresponding turning movement
                // Get the involved signals: movements within the same signals are OK
                int TurnID=aimsunTurningList.get(i).TurnID;
                List<Integer> SignalByTurn=new ArrayList<Integer>();
                for(int j=0;j<CurrentControlPlan.Signals.size();j++){ // Loop for each signal
                    for(int k=0;k<CurrentControlPlan.Signals.get(j).NumTurnings;k++){
                        // Loop for the turns belonging to the signal
                        if(CurrentControlPlan.Signals.get(j).TurningIDs[k]==TurnID){
                            // Find the corresponding Signal movement
                            SignalByTurn.add(CurrentControlPlan.Signals.get(j).SignalID);
                            break;
                        }
                    }
                }
                // Check the phase ID, start time, and end time
                List<int []> PhaseIDAndTime=new ArrayList<int[]>();
                for(int j=0;j<SignalByTurn.size();j++){
                    for(int k=0;k<CurrentControlPlan.Phases.size();k++){
                        for(int p=0;p<CurrentControlPlan.Phases.get(k).NumSignalInPhase;p++){
                            if(CurrentControlPlan.Phases.get(k).SignalIDs[p]==SignalByTurn.get(j)){
                                // If find the signal ID
                                int PhaseID=CurrentControlPlan.Phases.get(k).PhaseID;
                                int StartTime=(int)CurrentControlPlan.Phases.get(k).StartingTime;
                                int EndTime= StartTime+(int)(CurrentControlPlan.Phases.get(k).Duration);
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
                    for(int k=0;k<CurrentControlPlan.Phases.size();k++){
                        if(CurrentControlPlan.Phases.get(k).PhaseID!=PhaseIDAndTime.get(j)[0]){
                            int tmpStartTime=(int)CurrentControlPlan.Phases.get(k).StartingTime;
                            int tmpEndTime=tmpStartTime+(int) CurrentControlPlan.Phases.get(k).Duration;
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
                                for(int p=0; p<CurrentControlPlan.Phases.get(k).NumSignalInPhase;p++){
                                    // Loop for the signals within the phase
                                    int tmpSignalID=CurrentControlPlan.Phases.get(k).SignalIDs[p];
                                    for(int t=0; t<CurrentControlPlan.NumSignals;t++){
                                        if(CurrentControlPlan.Signals.get(t).SignalID==tmpSignalID){
                                            // Add all turns associated to the phase
                                            for(int s=0; s<CurrentControlPlan.Signals.get(t).NumTurnings;s++){
                                                TurnInPhase.add(CurrentControlPlan.Signals.get(t).TurningIDs[s]);
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
                                if (OtherTurns.get(j).get(p) == aimsunTurningInJunction.get(k).TurnID) {
                                    // Find the turn ID
                                    if (aimsunTurningInJunction.get(k).OrigSectionID != FirstSectionID
                                            && aimsunTurningInJunction.get(k).Movement.equals("Through")) {
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
}
