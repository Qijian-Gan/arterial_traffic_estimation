package networkInput;

import java.util.ArrayList;
import java.io.*;
import java.util.*;
import commonClass.forAimsunNetwork.*;
import commonClass.forAimsunNetwork.signalControl.*;
import commonClass.forAimsunNetwork.detector.*;
import commonClass.forAimsunNetwork.section.*;
import commonClass.forAimsunNetwork.junction.*;

import main.MainFunction;
/**
 * Created by Qijian-Gan on 10/11/2017.
 */
public class readFromAimsun {

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

    /**
     *
     * @param controlPlanFile File name of control plan
     * @return List<AimsunControlPlanJunction> class
     */
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
                                PlanName,PlanOffset,-1,null,null,0,0,0,
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

    /**
     *
     * @param junctionFile File name of junction information
     * @return List<AimsunJunction> class
     */
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
                if(line.equals("Junction ID,Name, External ID, Signalized,# of incoming sections,# of outgoing sections, # of turns")){
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

    /**
     *
     * @param sectionFile File name of section information
     * @return List<AimsunSection> class
     */
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

    /**
     *
     * @param detectorFile File name of detector information
     * @return List<AimsunDetector> class
     */
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

    /**
     *
     * @param masterControlFile File name of master control information
     * @return List<AimsunMasterControlPlan> class
     */
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
