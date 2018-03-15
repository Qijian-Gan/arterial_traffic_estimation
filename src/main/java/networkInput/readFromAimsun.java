package networkInput;

import java.util.ArrayList;
import java.io.*;
import java.util.*;

import main.MainFunction;
/**
 * Created by Qijian-Gan on 10/11/2017.
 */
public class readFromAimsun {

    public static class AimsunNetwork{
        // This is the profile for Aimsun network
        public AimsunNetwork(List<AimsunControlPlanJunction> _aimsunControlPlanJunctionList,
                             List<AimsunJunction> _aimsunJunctionList,
                             List<AimsunSection> _aimsunSectionList,
                             List<AimsunDetector> _aimsunDetectorList,
                             List<AimsunMasterControlPlan> _aimsunMasterControlPlanList){
            this.aimsunControlPlanJunctionList=_aimsunControlPlanJunctionList;
            this.aimsunJunctionList=_aimsunJunctionList;
            this.aimsunSectionList=_aimsunSectionList;
            this.aimsunDetectorList=_aimsunDetectorList;
            this.aimsunMasterControlPlanList=_aimsunMasterControlPlanList;
        }
        public List<AimsunControlPlanJunction> aimsunControlPlanJunctionList; // Control plan information
        public List<AimsunJunction> aimsunJunctionList; // Junction information
        public List<AimsunSection> aimsunSectionList; // Section information
        public List<AimsunDetector> aimsunDetectorList; // Detector information
        public List<AimsunMasterControlPlan> aimsunMasterControlPlanList; // Master control plan
    }

    //*************************Signal Classes**************************************************
    public static class AimsunControlPlanJunction{
        // This is the profile of Aimsun control plan-junction
        public AimsunControlPlanJunction(int _PlanID,String _PlanExtID, String _PlanName, int _PlanOffset, int _JunctionID,
                                         String _JunctionName,String _ControlType, int _Offset, int _Cycle,
                                         int _NumBarriers, int _NumRings, int _NumPhases, int _NumSignals,
                                         List<AimsunPhase> _Phases, List<AimsunSignal> _Signals, List<AimsunRing> _Rings){
            this.PlanID=_PlanID;
            this.PlanExtID=_PlanExtID;
            this.PlanName=_PlanName;
            this.PlanOffset=_PlanOffset;
            this.JunctionID=_JunctionID;
            this.JunctionName=_JunctionName;
            this.ControlType=_ControlType;
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
        public int PlanID;
        public String PlanExtID;
        public String PlanName;
        public int PlanOffset;
        public int JunctionID;
        public String JunctionName;
        public String ControlType;
        public int Offset;
        public int Cycle;
        public int NumBarriers;
        public int NumRings;
        public int NumPhases;
        public int NumSignals;
        public List<AimsunPhase> Phases; // Phase information
        public List<AimsunSignal> Signals; // Signal information (in Aimsun): normally associated with traffic movements
        public List<AimsunRing> Rings; // Ring settings: dual rings or signal ring
        // Master control plans that associated with this control plan
        public List<AimsunMasterControlPlan> MasterControlPlan =new ArrayList<AimsunMasterControlPlan>();
    }

    public static class AimsunPhase{
        // This is the profile for Aimsun phase
        public AimsunPhase(int _PhaseID, int _RingID, double _StartingTime, double _Duration, String _IsInterphase,
                           double _PermissiveStartTime, double _PermissiveEndTime,int _NumSignalInPhase, int [] _SignalIDs){
            this.PhaseID=_PhaseID;
            this.RingID=_RingID;
            this.StartingTime=_StartingTime;
            this.Duration=_Duration;
            this.IsInterphase=_IsInterphase;
            this.PermissiveStartTime=_PermissiveStartTime;
            this.PermissiveEndTime=_PermissiveEndTime;
            this.NumSignalInPhase=_NumSignalInPhase;
            this.SignalIDs=_SignalIDs;
        }
        public int PhaseID;
        public int RingID;
        public double StartingTime;
        public double Duration;
        public String IsInterphase; // Yellow and all red period in Aimsun
        public double PermissiveStartTime; // Permissive movement
        public double PermissiveEndTime;
        public int NumSignalInPhase;
        public int [] SignalIDs; // Signals included in the phase
    }

    public static class AimsunSignal{
        // This if the profile for Aimsun signal
        public AimsunSignal(int _SignalID, int _NumTurnings, int [] _TurningIDs){
            this.SignalID=_SignalID;
            this.NumTurnings=_NumTurnings;
            this.TurningIDs=_TurningIDs;
        }
        public int SignalID;
        public int NumTurnings;
        public int [] TurningIDs; // Turn movements included in the signal
    }

    public static class AimsunRing{
        // This is the profile for Aimsun control rings
        public AimsunRing(int _RingID,int _CoordinatedPhase, double _Offset, int _GetMatchesOffsetWithEndOfPhase){
            this.RingID=_RingID;
            this.CoordinatedPhase=_CoordinatedPhase;
            this.Offset=_Offset;
            this.GetMatchesOffsetWithEndOfPhase=_GetMatchesOffsetWithEndOfPhase;
        }
        public int RingID;
        public int CoordinatedPhase; // Which phase is coordinated?
        public double Offset; // What is the offset?
        public int GetMatchesOffsetWithEndOfPhase; // The matching point is from the end of the phase or not?
    }

    public static class AimsunMasterControlPlan{
        // This is the profile for Aimsun master control plan
        public AimsunMasterControlPlan(int _MasterPlanID, String _Name, int _ControlPlanID, int _StartingTime,
                                       int _Duration,int _Zone){
            this.MasterPlanID=_MasterPlanID;
            this.Name=_Name;
            this.ControlPlanID=_ControlPlanID;
            this.StartingTime=_StartingTime;
            this.Duration=_Duration;
            this.Zone=_Zone;
        }
        public int MasterPlanID;
        public String Name;
        public int ControlPlanID; // Control plan ID included in the master control plan
        public int StartingTime;
        public int Duration;
        public int Zone; // The traffic zone (may be related to the "Intersection"?)
    }

    //*************************Detector Classes**************************************************
    public static class AimsunDetector{
        // This is the profile for Aimsun detector
        public AimsunDetector(int _DetectorID, String _ExternalID, int _SectionID, String _Movement,
                              int _NumOfLanes, int _FirstLane, int _LastLane,
                              double _InitialPosition, double _FinalPosition, double _Length){
            this.DetectorID=_DetectorID;
            this.ExternalID=_ExternalID;
            this.SectionID=_SectionID;
            this.Movement=_Movement;
            this.NumOfLanes=_NumOfLanes;
            this.FirstLane=_FirstLane;
            this.LastLane=_LastLane;
            this.InitialPosition=_InitialPosition;
            this.FinalPosition=_FinalPosition;
            this.Length=_Length;
        }
        public int DetectorID;
        public String ExternalID;
        public int SectionID;
        public String Movement;
        public int NumOfLanes;
        public int FirstLane; // Labelled from right to left
        public int LastLane; // Labelled from right to left
        public double InitialPosition; //The position as an offset from the entrance of the section.
        public double FinalPosition; //The position as an offset from the entrance of the section.
        public double Length;
    }

    //*************************Junction Classes**************************************************
    public static class AimsunJunction{
        // This is the profile for Aimsun junction
        public AimsunJunction(int _JunctionID, String _JunctionName, String _JunctionExtID, int _Signalized, int _NumEntranceSections,
                              int _NumExitSections, int _NumTurns, int[] _EntranceSections, int [] _ExitSections,
                              List<AimsunTurning> _Turnings, List<AimsunSectionTurning> _SectionTurningList){
            this.JunctionID=_JunctionID;
            this.JunctionName=_JunctionName;
            this.JunctionExtID=_JunctionExtID;
            this.Signalized=_Signalized;
            this.NumEntranceSections=_NumEntranceSections;
            this.NumExitSections=_NumExitSections;
            this.NumTurns=_NumTurns;
            this.EntranceSections=_EntranceSections;
            this.ExitSections=_ExitSections;
            this.Turnings=_Turnings;
            this.SectionTurningList=_SectionTurningList;
        }
        public int JunctionID;
        public String JunctionName;
        public String JunctionExtID;
        public int Signalized;
        public int NumEntranceSections; // Number of entrance sections
        public int NumExitSections; // Number of exit sections
        public int NumTurns; // Number of turns
        public int [] EntranceSections; // Entrance sections
        public int [] ExitSections; // Exit sections
        public List<AimsunTurning> Turnings; // Turnings
        // Turns included in a section; Turning movements ordered from left to right in a give section
        public List<AimsunSectionTurning> SectionTurningList;
    }

    public static class AimsunTurning{
        // This is the profile for Aimsun turning
        public AimsunTurning(int _TurnID, int _OrigSectionID, int _DestSectionID, int _OrigFromLane, int _OrigToLane,
                             int _DestFromLane, int _DestToLane, String _Movement, double _TurningSpeed, double _TurningAngle){
            this.TurnID=_TurnID;
            this.OrigSectionID=_OrigSectionID;
            this.DestSectionID=_DestSectionID;
            this.OrigFromLane=_OrigFromLane;
            this.OrigToLane=_OrigToLane;
            this.DestFromLane=_DestFromLane;
            this.DestToLane=_DestToLane;
            this.Movement=_Movement;
            this.TurningSpeed=_TurningSpeed;
            this.TurningAngle=_TurningAngle;
        }
        public int TurnID;
        public int OrigSectionID;
        public int DestSectionID;
        public int OrigFromLane; // Labelled from right to left
        public int OrigToLane; // Labelled from right to left
        public int DestFromLane; // Labelled from right to left
        public int DestToLane; // Labelled from right to left
        public String Movement;
        public double TurningSpeed;
        public double TurningAngle;

    }

    public static class AimsunSectionTurning{
        // This is the profile for Aimsun section-turning
        public AimsunSectionTurning(int _SectionID, int _NumTurns, int [] _TurnIDFromLeftToRight){
            this.SectionID=_SectionID;
            this.NumTurns=_NumTurns;
            this.TurnIDFromLeftToRight=_TurnIDFromLeftToRight;
        }
        public int SectionID;
        public int NumTurns;
        public int [] TurnIDFromLeftToRight; //Write the turn orders by section from left to right
    }

    //*************************Section Classes **************************************************
    public static class AimsunSection{
        // This is the profile for Aimsun section
        public AimsunSection(int _SectionID, String _SectionName, String _SectionExtID,int _NumLanes, int _NumPoints,
                             double [] _LaneLengths,double [] _InitialPoints, int [] _IsFullLane,double[][] _ShapePoints){
            this.SectionID=_SectionID;
            this.SectionName=_SectionName;
            this.SectionExtID=_SectionExtID;
            this.NumLanes=_NumLanes;
            this.NumPoints=_NumPoints;
            this.LaneLengths=_LaneLengths;
            this.InitialPoints=_InitialPoints;
            this.IsFullLane=_IsFullLane;
            this.ShapePoints=_ShapePoints;
        }
        public int SectionID;
        public String SectionName;
        public String SectionExtID;
        public int NumLanes; // Number of lanes
        public int NumPoints; //Number of shape points
        public double[] LaneLengths; // Lane Lengths
        public double[] InitialPoints; // Initial starting points
        public int [] IsFullLane; // Is a full lane or not
        public double[][] ShapePoints; // Shape points
        public AimsunJunction DownstreamJunction=null; // This is used to reconstruct the network
        public AimsunJunction UpstreamJunction=null; // This is used to reconstruct the network
    }


    //*************************Major Functions **************************************************
    public static AimsunNetwork readAimsunNetworkFiles(){
        // This is the main function to read the network files
        File folderLocation=new File(MainFunction.cBlock.AimsunFolder);

        // Control plan
        File controlPlanFile=new File(folderLocation,"ControlPlanInf.txt");
        if(!controlPlanFile.exists()){
            System.out.println("Missing the control plan file, extract the Aimsun network first!");
            System.exit(-1);
        }
        List<AimsunControlPlanJunction> aimsunControlPlanJunctionList=readControlPlanInformation(controlPlanFile);
        System.out.println("Number of Control Plan-Junction:"+aimsunControlPlanJunctionList.size());

        // Junction
        File junctionFile=new File(folderLocation,"JunctionInf.txt");
        if(!junctionFile.exists()){
            System.out.println("Missing the junction file, extract the Aimsun network first!");
            System.exit(-1);
        }
        List<AimsunJunction> aimsunJunctionList=readJunctionInformation(junctionFile);
        System.out.println("Number of Junctions:"+aimsunJunctionList.size());

        // Section
        File sectionFile=new File(folderLocation,"SectionInf.txt");
        if(!sectionFile.exists()){
            System.out.println("Missing the section file, extract the Aimsun network first!");
            System.exit(-1);
        }
        List<AimsunSection>  aimsunSectionList=readSectionInformation(sectionFile);
        System.out.println("Number of Sections:"+aimsunSectionList.size());

        // Detector
        File detectorFile=new File(folderLocation,"DetectorInf.csv");
        if(!detectorFile.exists()){
            System.out.println("Missing the detector file, extract the Aimsun network first!");
            System.exit(-1);
        }
        List<AimsunDetector> aimsunDetectorList=readDetectorInformation(detectorFile);
        System.out.println("Number of Detectors:"+aimsunDetectorList.size());

        // Master control plan
        File masterControlFile=new File(folderLocation,"MasterControlPlanInf.txt");
        if(!masterControlFile.exists()){
            System.out.println("Missing the master control plan file, extract the Aimsun network first!");
            System.exit(-1);
        }
        List<AimsunMasterControlPlan> aimsunMasterControlPlanList=readMasterControlPlanInformation(masterControlFile);
        System.out.println("Number of Master Control Plans:"+aimsunMasterControlPlanList.size());

        System.out.println("Done!");

        AimsunNetwork aimsunNetwork=new AimsunNetwork(aimsunControlPlanJunctionList,
                aimsunJunctionList,aimsunSectionList,aimsunDetectorList,aimsunMasterControlPlanList);
        return aimsunNetwork;
    }

    public static List<AimsunControlPlanJunction> readControlPlanInformation(File controlPlanFile){
        // This function is used to read the control plan information
        List<AimsunControlPlanJunction> aimsunControlPlanJunctionList=new ArrayList<AimsunControlPlanJunction>();

        System.out.println("Reading in the control plan file : " + controlPlanFile.getAbsoluteFile());
        BufferedReader br = null ;
        try {
            br = new BufferedReader(new FileReader(controlPlanFile));
        } catch (FileNotFoundException e) {
            System.out.println("The control plan file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if(line.equals("Plan ID, Plan ExtID, Plan Name, Number of control junction, Plan Offset:")){
                    // Reading the plan general information
                    line = br.readLine(); // Read the next line;
                    String[] tmpLine=line.split(",");
                    if(tmpLine.length!=5){
                        System.out.println("Wrong format:"+line);
                        System.exit(-1);
                    }
                    int PlanID=Integer.parseInt(tmpLine[0].trim());
                    String PlanExtID=tmpLine[1];
                    String PlanName=tmpLine[2];
                    int NumControlJunction=Integer.parseInt(tmpLine[3].trim());
                    int PlanOffset=Integer.parseInt(tmpLine[4].trim());

                    //Reading the junction information
                    AimsunControlPlanJunction tmpAimsunControlPlanJunction=null;
                    if(NumControlJunction>0) {
                        for (int i = 0; i < NumControlJunction; i++) {
                            line = br.readLine(); // Ignore the next line;
                            line = br.readLine(); // Get the next line;
                            tmpLine = line.split(",");
                            int JunctionID = Integer.parseInt(tmpLine[0].trim());
                            String JunctionName = tmpLine[1];
                            String ControlType = tmpLine[2];
                            int Offset = Integer.parseInt(tmpLine[3].trim());
                            int NumBarriers = Integer.parseInt(tmpLine[4].trim());
                            int Cycle = Integer.parseInt(tmpLine[5].trim());
                            int NumRings = Integer.parseInt(tmpLine[6].trim());
                            int NumPhases = Integer.parseInt(tmpLine[7].trim());
                            int NumSignals = Integer.parseInt(tmpLine[8].trim());

                            // Reading the phase information
                            line = br.readLine(); // Ignore the header
                            List<AimsunPhase> aimsunPhaseList = new ArrayList<AimsunPhase>();
                            for (int j = 0; j < NumPhases; j++) {
                                line = br.readLine(); //Read the next line
                                tmpLine = line.split(",");
                                int PhaseID = Integer.parseInt(tmpLine[0].trim());
                                int RingID = Integer.parseInt(tmpLine[1].trim());
                                double StartingTime = Double.parseDouble(tmpLine[2].trim());
                                double Duration = Double.parseDouble(tmpLine[3].trim());
                                String IsInterphase = tmpLine[4];
                                double PermissiveStartTime = Double.parseDouble(tmpLine[5].trim());
                                double PermissiveEndTime = Double.parseDouble(tmpLine[6].trim());
                                int NumSignalInPhase = Integer.parseInt(tmpLine[7].trim());

                                // Reading the signal in phase information
                                int[] SignalInPhase = new int[NumSignalInPhase];
                                for (int k = 0; k < NumSignalInPhase; k++) {
                                    SignalInPhase[k] = Integer.parseInt(tmpLine[8 + k].trim());
                                }
                                AimsunPhase tmpAimsunPhase = new AimsunPhase(PhaseID, RingID, StartingTime, Duration, IsInterphase,
                                        PermissiveStartTime, PermissiveEndTime, NumSignalInPhase, SignalInPhase);
                                aimsunPhaseList.add(tmpAimsunPhase);
                            }
                            // Reading the signal information
                            line = br.readLine(); // Ignore the next line
                            List<AimsunSignal> aimsunSignalList = new ArrayList<AimsunSignal>();
                            for (int j = 0; j < NumSignals; j++) {
                                line = br.readLine(); // Read the next line
                                tmpLine = line.split(",");
                                int SignalID = Integer.parseInt(tmpLine[0].trim());
                                int NumTurnings = Integer.parseInt(tmpLine[1].trim());
                                int[] TurningIDs = new int[NumTurnings];
                                for (int k = 0; k < NumTurnings; k++) {
                                    TurningIDs[k] = Integer.parseInt(tmpLine[2 + k].trim());
                                }
                                AimsunSignal tmpAimsunSignal = new AimsunSignal(SignalID, NumTurnings, TurningIDs);
                                aimsunSignalList.add(tmpAimsunSignal);
                            }
                            //Reading the ring information
                            line = br.readLine(); // Ignore the next line
                            List<AimsunRing> aimsunRingList = new ArrayList<AimsunRing>();
                            for (int j = 0; j < NumRings; j++) {
                                line = br.readLine(); // Read the next line
                                tmpLine = line.split(",");
                                int RingID = Integer.parseInt(tmpLine[0].trim());
                                int CoordinatedPhase = Integer.parseInt(tmpLine[1].trim());
                                double OffsetRing = Double.parseDouble(tmpLine[2].trim());
                                int GetMatchesOffsetWithEndOfPhase = Integer.parseInt(tmpLine[3].trim());
                                AimsunRing tmpAimsunRing = new AimsunRing
                                        (RingID, CoordinatedPhase, OffsetRing, GetMatchesOffsetWithEndOfPhase);
                                aimsunRingList.add(tmpAimsunRing);
                            }
                            tmpAimsunControlPlanJunction = new AimsunControlPlanJunction(PlanID, PlanExtID,
                                    PlanName, PlanOffset, JunctionID, JunctionName, ControlType, Offset, Cycle, NumBarriers,
                                    NumRings, NumPhases, NumSignals, aimsunPhaseList, aimsunSignalList, aimsunRingList);
                        }
                    }else{
                        tmpAimsunControlPlanJunction=new AimsunControlPlanJunction(PlanID,PlanExtID,
                                PlanName,PlanOffset,0,null,null,0,0,0,
                                0,0,0,null,null,null);
                    }
                    // Append the control plan-junction to the list
                    aimsunControlPlanJunctionList.add(tmpAimsunControlPlanJunction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aimsunControlPlanJunctionList;
    }

    public static List<AimsunJunction> readJunctionInformation(File junctionFile){
        // This function is used to read junction information
        List<AimsunJunction> aimsunJunctionList=new ArrayList<AimsunJunction>();

        System.out.println("Reading in the junction file : " + junctionFile.getAbsoluteFile());
        BufferedReader br = null ;
        try {
            br = new BufferedReader(new FileReader(junctionFile));
        } catch (FileNotFoundException e) {
            System.out.println("The junction file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if(line.equals("Junction ID,Name, External ID, Signalized,# " +
                                "of incoming sections,# of outgoing sections, # of turns")){
                    line=br.readLine();// Read the next line
                    String [] tmpLine=line.split(",");
                    int JunctionID=Integer.parseInt(tmpLine[0].trim());
                    String JunctionName=tmpLine[1];
                    String JunctionExtID=tmpLine[2];
                    int Signalized=Integer.parseInt(tmpLine[3].trim());
                    int NumEntranceSections=Integer.parseInt(tmpLine[4].trim());
                    int NumExitSections=Integer.parseInt(tmpLine[5].trim());
                    int NumTurns=Integer.parseInt(tmpLine[6].trim());
                    // Entrance links
                    line=br.readLine(); // Ignore this line
                    int [] EntranceSections=null;
                    if(NumEntranceSections>0){
                        line=br.readLine();
                        tmpLine=line.split(",");
                        EntranceSections=new int [tmpLine.length];
                        for (int i=0;i<tmpLine.length;i++){
                            EntranceSections[i] = Integer.parseInt(tmpLine[i].trim());
                        }
                    }
                    // Exit links
                    line=br.readLine(); // Ignore this line
                    int [] ExitSections=null;
                    if(NumExitSections>0){
                        line=br.readLine();
                        tmpLine=line.split(",");
                        ExitSections=new int [tmpLine.length];
                        for (int i=0;i<tmpLine.length;i++){
                            ExitSections[i] = Integer.parseInt(tmpLine[i].trim());
                        }
                    }
                    // Turnings
                    line=br.readLine(); // Ignore this line
                    List<AimsunTurning> Turnings= new ArrayList<AimsunTurning>();
                    if(NumTurns>0){
                        for(int i=0;i<NumTurns;i++){
                            line=br.readLine();
                            tmpLine=line.split(",");
                            int TurnID=Integer.parseInt(tmpLine[0].trim());
                            int OrigSectionID=Integer.parseInt(tmpLine[1].trim());
                            int DestSectionID=Integer.parseInt(tmpLine[2].trim());
                            int OrigFromLane=Integer.parseInt(tmpLine[3].trim());
                            int OrigToLane=Integer.parseInt(tmpLine[4].trim());
                            int DestFromLane=Integer.parseInt(tmpLine[5].trim());
                            int DestToLane=Integer.parseInt(tmpLine[6].trim());
                            String [] Description=tmpLine[7].split(":");
                            String Movement=Description[0];
                            double TurningSpeed=Double.parseDouble(tmpLine[8].trim());
                            double TurningAngle=Double.parseDouble(tmpLine[9].trim());
                            AimsunTurning aimsunTurning= new AimsunTurning(TurnID, OrigSectionID, DestSectionID, OrigFromLane, OrigToLane,
                                    DestFromLane, DestToLane, Movement , TurningSpeed ,TurningAngle);
                            Turnings.add(aimsunTurning);
                        }
                    }
                    // Turning from left to right
                    line = br.readLine();
                    List<AimsunSectionTurning> SectionTurningsList=new ArrayList<AimsunSectionTurning>();
                    if(NumEntranceSections>0){
                        for(int i=0;i<NumEntranceSections;i++) {
                            line = br.readLine();
                            tmpLine = line.split(",");
                            int SectionID=Integer.parseInt(tmpLine[0].trim());
                            int NumTurnsInSection=Integer.parseInt(tmpLine[1].trim());
                            int [] TurnsInSection=new int[NumTurnsInSection];
                            for(int j=0;j<NumTurnsInSection;j++){
                                TurnsInSection[j]=Integer.parseInt(tmpLine[2+j].trim());
                            }
                            AimsunSectionTurning aimsunSectionTurning=new
                                    AimsunSectionTurning(SectionID,NumTurnsInSection,TurnsInSection);
                            SectionTurningsList.add(aimsunSectionTurning);
                        }
                    }
                    AimsunJunction aimsunJunction=new AimsunJunction(JunctionID,JunctionName,JunctionExtID,
                            Signalized,NumEntranceSections,NumExitSections,NumTurns,EntranceSections,
                            ExitSections,Turnings,SectionTurningsList);
                    aimsunJunctionList.add(aimsunJunction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aimsunJunctionList;
    }

    public static List<AimsunSection> readSectionInformation(File sectionFile) {
        // This is the function to read section information
        List<AimsunSection> aimsunSectionList = new ArrayList<AimsunSection>();

        System.out.println("Reading in the section file : " + sectionFile.getAbsoluteFile());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(sectionFile));
        } catch (FileNotFoundException e) {
            System.out.println("The section file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.equals("Section ID,Name,External ID,# of lanes,# of points")) {
                    line = br.readLine();
                    String [] tmpLine=line.split(",");
                    int SectionID=Integer.parseInt(tmpLine[0].trim());
                    String SectionName=tmpLine[1];
                    String SectionExtID=tmpLine[2];
                    int NumLanes=Integer.parseInt(tmpLine[3].trim());
                    int NumPoints=Integer.parseInt(tmpLine[4].trim());
                    // Read lane length and is full lane or not
                    double [] LaneLengths=null;
                    double [] InitialPoints=null;
                    int [] IsFullLane=null;
                    if(NumLanes>0){
                        LaneLengths=new double[NumLanes];
                        InitialPoints=new double[NumLanes];
                        IsFullLane=new int[NumLanes];
                        // Lane Lengths
                        line = br.readLine();
                        line = br.readLine();
                        tmpLine=line.split(",");
                        for(int i=0;i<NumLanes;i++){
                            LaneLengths[i]=Double.parseDouble(tmpLine[i].trim());
                        }
                        // Initial Points
                        line = br.readLine();
                        line = br.readLine();
                        tmpLine=line.split(",");
                        for(int i=0;i<NumLanes;i++){
                            InitialPoints[i]=Double.parseDouble(tmpLine[i].trim());
                        }
                        // Full Lanes
                        line = br.readLine();
                        line = br.readLine();
                        tmpLine=line.split(",");
                        for(int i=0;i<NumLanes;i++){
                            IsFullLane[i]=Integer.parseInt(tmpLine[i].trim());
                        }
                    }
                    // Read lane length and is full lane or not
                    double [][] ShapePoints=null;
                    if(NumPoints>0){
                        ShapePoints=new double[NumPoints][2];
                        line = br.readLine();
                        line = br.readLine();
                        tmpLine=line.split(",");
                        for(int i=0;i<NumPoints;i++){
                            ShapePoints[i][0]=Double.parseDouble(tmpLine[2*i].trim());
                            ShapePoints[i][1]=Double.parseDouble(tmpLine[2*i+1].trim());
                        }
                    }

                    AimsunSection aimsunSection=new AimsunSection(SectionID, SectionName, SectionExtID,
                            NumLanes, NumPoints, LaneLengths,InitialPoints,IsFullLane,ShapePoints);
                    aimsunSectionList.add(aimsunSection);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aimsunSectionList;
    }

    public static List<AimsunDetector> readDetectorInformation(File detectorFile) {
        // This is the function to read detector information

        List<AimsunDetector> aimsunDetectorList = new ArrayList<AimsunDetector>();
        System.out.println("Reading in the detector file : " + detectorFile.getAbsoluteFile());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(detectorFile));
        } catch (FileNotFoundException e) {
            System.out.println("The detector file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                    String [] tmpLine=line.split(",");
                    int DetectorID=Integer.parseInt(tmpLine[0].trim());
                    String DetectorExtID=tmpLine[1];
                    int SectionID=Integer.parseInt(tmpLine[2].trim());
                    String Movement=tmpLine[3];
                    int FirstLane=Integer.parseInt(tmpLine[4].trim());
                    int LastLane=Integer.parseInt(tmpLine[5].trim());
                    double InitialPosition=Double.parseDouble(tmpLine[6].trim());
                    double FinalPosition=Double.parseDouble(tmpLine[7].trim());
                    int NumOfLanes=Math.abs(LastLane-FirstLane)+1;
                    double Length=Math.abs(FinalPosition-InitialPosition);

                AimsunDetector aimsunDetector=new AimsunDetector(DetectorID,DetectorExtID,SectionID,Movement,
                        NumOfLanes,FirstLane,LastLane,InitialPosition,FinalPosition,Length);
                    aimsunDetectorList.add(aimsunDetector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aimsunDetectorList;
    }

    public static List<AimsunMasterControlPlan> readMasterControlPlanInformation(File masterControlFile){
        // This function is used to read Aimsun master control plan information
        List<AimsunMasterControlPlan> aimsunMasterControlPlanList=new ArrayList<AimsunMasterControlPlan>();

        System.out.println("Reading in the master control plan file : " + masterControlFile.getAbsoluteFile());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(masterControlFile));
        } catch (FileNotFoundException e) {
            System.out.println("The master control plan file is not found!");
            System.exit(-1);
        }

        String line;
        try {
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String [] tmpLine=line.split(",");
                int MasterPlanID=Integer.parseInt(tmpLine[0].trim());
                String Name=tmpLine[1];
                int ControlPlanID=Integer.parseInt(tmpLine[2].trim());
                int StartingTime=Integer.parseInt(tmpLine[3].trim());
                int Duration=Integer.parseInt(tmpLine[4].trim());
                int Zone=Integer.parseInt(tmpLine[5].trim());

                AimsunMasterControlPlan aimsunMasterControlPlan=new AimsunMasterControlPlan
                        (MasterPlanID, Name, ControlPlanID, StartingTime,Duration,Zone);
                aimsunMasterControlPlanList.add(aimsunMasterControlPlan);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aimsunMasterControlPlanList;

    }
}
