package networkInput;

/**
 * Created by Qijian-Gan on 10/13/2017.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

import main.MainFunction;
import networkInput.readFromAimsun.*;
import sun.awt.image.ImageWatched;

public class reconstructNetwork {

    public static class AimsunNetworkByApproach{
        // This is the profile for Aimsun network by Approach
        public AimsunNetworkByApproach(AimsunNetwork _aimsunNetwork,List<AimsunApproach> _aimsunNetworkByApproach){
            this.aimsunNetwork=_aimsunNetwork;
            this.aimsunNetworkByApproach=_aimsunNetworkByApproach;
        }

        protected AimsunNetwork aimsunNetwork; // Aimsun network
        protected List<AimsunApproach> aimsunNetworkByApproach; // Reconstructed Aimsun network at the approach level

        public AimsunNetwork getAimsunNetwork() {
            return aimsunNetwork;
        }

        public List<AimsunApproach> getAimsunNetworkByApproach() {
            return aimsunNetworkByApproach;
        }
    }

    //*************************************Approach Property********************************
    public static class AimsunApproach{
        // This is the profile for Aimsun approach
        // Each approach will have complete information of the network

        public AimsunApproach(int _JunctionID, String _JunctionName, String _JunctionExtID, String _City, String _County,
                              int _Signalized, int _FirstSectionID, String _FirstSectionName, String _FirstSectionExtID,
                              SectionBelongToApproach _sectionBelongToApproach, TurningBelongToApproach _turningBelongToApproach,
                              List<LaneTurningProperty> _laneTurningProperty, GeoDesign _geoDesign, DetectorProperty _detectorProperty,
                              DefaultSignalSetting _defaultSignalSetting, MidlinkCountConfig _midlinkCountConfig,
                              List<AimsunControlPlanJunction> _controlPlanJunction, AimsunJunction _aimsunJunction){
            this.JunctionID=_JunctionID;
            this.JunctionName=_JunctionName;
            this.JunctionExtID=_JunctionExtID;
            this.City=_City;
            this.County=_County;
            this.Signalized=_Signalized;
            this.FirstSectionID=_FirstSectionID;
            this.FirstSectionName=_FirstSectionName;
            this.FirstSectionExtID=_FirstSectionExtID;
            this.sectionBelongToApproach=_sectionBelongToApproach;
            this.turningBelongToApproach=_turningBelongToApproach;
            this.laneTurningProperty=_laneTurningProperty;
            this.geoDesign=_geoDesign;
            this.detectorProperty=_detectorProperty;
            this.defaultSignalSetting=_defaultSignalSetting;
            this.midlinkCountConfig=_midlinkCountConfig;
            this.controlPlanJunction=_controlPlanJunction;
            this.aimsunJunction=_aimsunJunction;
        }
        protected int JunctionID;
        protected String JunctionName;
        protected String JunctionExtID;
        protected String City;
        protected String County;
        protected int Signalized;
        protected int FirstSectionID;
        protected String FirstSectionName;
        protected String FirstSectionExtID;
        protected SectionBelongToApproach sectionBelongToApproach; // Sections belonging to the approach
        protected TurningBelongToApproach turningBelongToApproach; // Turnings belonging to the approach
        protected List<LaneTurningProperty> laneTurningProperty;
        protected GeoDesign geoDesign; // Geometry information
        protected DetectorProperty detectorProperty;
        protected DefaultSignalSetting defaultSignalSetting;
        protected MidlinkCountConfig midlinkCountConfig; // It may be possible to get some information from midlink counts
        protected List<AimsunControlPlanJunction> controlPlanJunction; // Control plans associated with the junction
        // Aimsun junction information: include this metric just in case we need more information in the future
        protected AimsunJunction aimsunJunction;

        public int getJunctionID() {
            return JunctionID;
        }

        public String getJunctionName() {
            return JunctionName;
        }

        public int getFirstSectionID() {
            return FirstSectionID;
        }

        public GeoDesign getGeoDesign() {
            return geoDesign;
        }

        public DetectorProperty getDetectorProperty() {
            return detectorProperty;
        }

        public TurningBelongToApproach getTurningBelongToApproach() {
            return turningBelongToApproach;
        }

        public AimsunJunction getAimsunJunction() {
            return aimsunJunction;
        }

        public int getSignalized() {
            return Signalized;
        }

        public SectionBelongToApproach getSectionBelongToApproach() {
            return sectionBelongToApproach;
        }

        public String getFirstSectionName() {
            return FirstSectionName;
        }

        public List<LaneTurningProperty> getLaneTurningProperty() {
            return laneTurningProperty;
        }
    }

    //*************************************Detector Property********************************
    public static class DetectorProperty{
        // This is the profile for detector property at a given approach
        // It is categorized into four different types based on the topology:
        // -->exclusive left turn, exclusive right turn, advance, and general stopbar
        //For each category, it may consist of several combinations of detection movements
        // Exclusive left turn: left turn
        // Exclusive right turn: right turn
        // General stopbar: all movements, through, left-through, left-right, through-right
        // Advance: all movements, left/right/through only, left-through, left-right, through-right
        public DetectorProperty(List<DetectorMovementProperty> _ExclusiveLeftTurn,
                                List<DetectorMovementProperty> _ExclusiveRightTurn,
                                List<DetectorMovementProperty> _AdvanceDetectors,
                                List<DetectorMovementProperty> _GeneralStopbarDetectors){
            this.ExclusiveLeftTurn=_ExclusiveLeftTurn;
            this.ExclusiveRightTurn=_ExclusiveRightTurn;
            this.AdvanceDetectors=_AdvanceDetectors;
            this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
        }
        protected List<DetectorMovementProperty> ExclusiveLeftTurn; // Exclusive left turn (stop-bar) detectors
        protected List<DetectorMovementProperty> ExclusiveRightTurn; // Exclusive right turn (stop-bar) detectors
        protected List<DetectorMovementProperty> AdvanceDetectors; // Advance detectors (exclusive or combined movements)
        // General stop-bar detectors (through, or combined movements)
        protected List<DetectorMovementProperty> GeneralStopbarDetectors;

        public List<DetectorMovementProperty> getAdvanceDetectors() {
            return AdvanceDetectors;
        }

        public List<DetectorMovementProperty> getExclusiveLeftTurn() {
            return ExclusiveLeftTurn;
        }

        public List<DetectorMovementProperty> getExclusiveRightTurn() {
            return ExclusiveRightTurn;
        }

        public List<DetectorMovementProperty> getGeneralStopbarDetectors() {
            return GeneralStopbarDetectors;
        }
    }

    public static class DetectorMovementProperty{
        // This is the profile for detector-movement property
        public DetectorMovementProperty(String _Movement, List<Integer> _DetectorIDs, List<Double> _DetectorLengths,
                                        List<Double> _DistancesToStopbar,List<Integer> _NumberOfLanes){
            this.Movement=_Movement;
            this.DetectorIDs=_DetectorIDs;
            this.DetectorLengths=_DetectorLengths;
            this.DistancesToStopbar=_DistancesToStopbar;
            this.NumberOfLanes=_NumberOfLanes;
        }
        protected String Movement;
        protected List<Integer> DetectorIDs; // Detector IDs belonging to the same movement
        protected List<Double> DetectorLengths;
        protected List<Double> DistancesToStopbar;
        protected List<Integer> NumberOfLanes;

        public String getMovement() {
            return Movement;
        }

        public List<Integer> getNumberOfLanes() {
            return NumberOfLanes;
        }

        public List<Double> getDistancesToStopbar() {
            return DistancesToStopbar;
        }

        public List<Integer> getDetectorIDs() {
            return DetectorIDs;
        }

        public List<Double> getDetectorLengths() {
            return DetectorLengths;
        }
    }

    //*************************************Section Property********************************
    public static class SectionBelongToApproach{
        // This is the profile for sections belonging to the same approach
        public SectionBelongToApproach(List<Integer> _ListOfSections, List<AimsunSection> _Property){
            this.ListOfSections=_ListOfSections;
            this.Property=_Property;
        }
        protected List<Integer> ListOfSections; // The list of sections
        protected List<AimsunSection> Property; // Section properties inherited from Aimsun

        public List<Integer> getListOfSections() {
            return ListOfSections;
        }

        public List<AimsunSection> getProperty() {
            return Property;
        }
    }

    //*************************************Turning Property********************************
    public static class TurningBelongToApproach{
        // This is the profile for turnings belonging to the same approach (the first/downstream section)
        public TurningBelongToApproach(int [] _TurningAtFirstSectionFromLeftToRight, List<AimsunTurning> _TurningProperty){
            this.TurningAtFirstSectionFromLeftToRight=_TurningAtFirstSectionFromLeftToRight;
            this.TurningProperty=_TurningProperty;
        }
        protected int [] TurningAtFirstSectionFromLeftToRight; // List of turnings at the first section
        protected List<AimsunTurning> TurningProperty; // Turning property inherited from Aimsun

        public int[] getTurningAtFirstSectionFromLeftToRight() {
            return TurningAtFirstSectionFromLeftToRight;
        }

        public List<AimsunTurning> getTurningProperty() {
            return TurningProperty;
        }
    }

    //*************************************Lane-Turning Property********************************
    public static class LaneTurningProperty{
        // This is the profile for lane-turning property for a give section
        // Lane property(include turn info)-->section
        public LaneTurningProperty(int _SectionID, int _NumLanes, List<LaneProperty> _Lanes){
            this.SectionID=_SectionID;
            this.NumLanes=_NumLanes;
            this.Lanes=_Lanes;
        }
        protected int SectionID;
        protected int NumLanes;
        protected List<LaneProperty> Lanes; // Lane properties

        public List<LaneProperty> getLanes() {
            return Lanes;
        }

        public int getSectionID() {
            return SectionID;
        }
    }

    public static class LaneProperty{
        // This is the profile for lane property
        public LaneProperty(int _LaneID, int _IsExclusive, List<Integer> _TurningMovements, List<Double> _Proportions, double _Length){
            this.LaneID=_LaneID;
            this.IsExclusive=_IsExclusive;
            this.TurningMovements=_TurningMovements;
            this.Proportions=_Proportions;
            this.Length=_Length;
        }
        protected int LaneID;
        protected int IsExclusive; // Is an exclusive lane or not
        protected List<Integer> TurningMovements; // Turning movements on the lane
        protected List<Double> Proportions; // Proportions of the turning movements on the lane
        protected double Length;

        public int getLaneID() {
            return LaneID;
        }

        public int getIsExclusive() {
            return IsExclusive;
        }

        public List<Integer> getTurningMovements() {
            return TurningMovements;
        }
    }

    //*************************************Geometry Property********************************
    public static class GeoDesign{
        // This is the profile for geometry design
        public GeoDesign(double _LinkLength, int _NumOfUpstreamLanes, int _NumOfDownstreamLanes,
                         ExclusiveTurningProperty _ExclusiveLeftTurn, ExclusiveTurningProperty _ExclusiveRightTurn,
                         int [] _TurnIndicator){
            this.LinkLength=_LinkLength;
            this.NumOfUpstreamLanes=_NumOfUpstreamLanes;
            this.NumOfDownstreamLanes=_NumOfDownstreamLanes;
            this.ExclusiveLeftTurn=_ExclusiveLeftTurn;
            this.ExclusiveRightTurn=_ExclusiveRightTurn;
            this.TurnIndicator=_TurnIndicator;
        }
        protected double LinkLength;
        protected int NumOfUpstreamLanes; // Number of upstream lanes
        protected int NumOfDownstreamLanes; // Number of downstream lanes
        protected ExclusiveTurningProperty ExclusiveLeftTurn; // Left-turn pockets
        protected ExclusiveTurningProperty ExclusiveRightTurn; // Right-turn pockets
        protected int [] TurnIndicator;

        public int[] getTurnIndicator() {
            return TurnIndicator;
        }

        public ExclusiveTurningProperty getExclusiveLeftTurn() {
            return ExclusiveLeftTurn;
        }

        public ExclusiveTurningProperty getExclusiveRightTurn() {
            return ExclusiveRightTurn;
        }

        public int getNumOfUpstreamLanes() {
            return NumOfUpstreamLanes;
        }

        public int getNumOfDownstreamLanes() {
            return NumOfDownstreamLanes;
        }

        public double getLinkLength() {
            return LinkLength;
        }
    }

    public static class ExclusiveTurningProperty{
        // This is the profile for exclusive turning property
        public ExclusiveTurningProperty(int _NumLanes, double _Pocket ){
            this.NumLanes=_NumLanes;
            this.Pocket=_Pocket;
        }
        protected int NumLanes; // Number of exclusive lanes
        protected double Pocket; // Length of the turning pocket

        public double getPocket() {
            return Pocket;
        }

        public int getNumLanes() {
            return NumLanes;
        }
    }

    //*************************************Default Signal Property********************************
    public static class DefaultSignalSetting{
        // This is the profile for default signal settings
        public DefaultSignalSetting(int _CycleLength, int _LeftTurnGreen, int _ThroughGreen,
                                    int _RightTurnGreen, String _LeftTurnSetting){
            this.CycleLength=_CycleLength;
            this.LeftTurnGreen=_LeftTurnGreen;
            this.ThroughGreen=_ThroughGreen;
            this.RightTurnGreen=_RightTurnGreen;
            this.LeftTurnSetting=_LeftTurnSetting;
        }
        protected int CycleLength;
        protected int LeftTurnGreen; // Green time for left-turn
        protected int ThroughGreen; // Green time for through
        protected int RightTurnGreen; // Green time for right-turn
        protected String LeftTurnSetting; // Protected, Permitted, Protected-Permitted
    }

    //*************************************Midlink Count Configuration********************************
    // This part is currently not used in our estimation model
    // But information can be used to improve the calculation of turning proportions at the approach
    public static class MidlinkCountConfig{
        public MidlinkCountConfig(String _Location, String _Approach){
            this.Location=_Location;
            this.Approach=_Approach;
        }
        protected String Location; // Location of the files that stores the mid-link counts
        protected String Approach; // Which approach the mid-link counts belong to
    }

    //*************************************Control Plan Configuration********************************
    //ControlPlanBelongJunction: Directly inherited from the Aimsun inputs

    //*************************************Major Functions********************************
    public static AimsunNetworkByApproach reconstructAimsunNetwork(){
        // This function is used to reconstruct Aimsun Network

        AimsunNetwork aimsunNetwork=readFromAimsun.readAimsunNetworkFiles();

        aimsunNetwork.aimsunJunctionList=ReAdjustJunctionGeneratedByCentroidsInAimsun(aimsunNetwork.aimsunJunctionList);

        // Get Linear and NonLinear Junctions
        // Linear: 1*1
        // Nonlinear: M*N (>1)
        List<AimsunJunction> linearJunction=getLinearOrNonLinearJunction(aimsunNetwork.aimsunJunctionList,"Linear");
        List<AimsunJunction> nonLinearJunction=getLinearOrNonLinearJunction(aimsunNetwork.aimsunJunctionList,"NonLinear");

        List<AimsunApproach> aimsunApproachList=new ArrayList<AimsunApproach>();

        // Loop for each nonlinear junction
        for (int i=0;i<nonLinearJunction.size();i++){
            int JunctionID=nonLinearJunction.get(i).JunctionID;

            // Check the control plans for that junction
            List<AimsunControlPlanJunction> ControlPlanBelongToJunction=
                    getControlPlanBelongToJunction(JunctionID,aimsunNetwork.aimsunControlPlanJunctionList,aimsunNetwork.aimsunMasterControlPlanList);

            // Check each entrance sections
            for (int j=0;j<nonLinearJunction.get(i).NumEntranceSections;j++){
                // Store the junction information
                AimsunJunction aimsunJunction=nonLinearJunction.get(i);

                // Get the junction ID and Name
                JunctionID=nonLinearJunction.get(i).JunctionID;
                String JunctionName=nonLinearJunction.get(i).JunctionName;

                // Get the junction External ID, City, County
                String JunctionExtID,City,County;
                if(nonLinearJunction.get(i).JunctionExtID.equals("")){
                    JunctionExtID="N/A";
                    City="N/A";
                    County="N/A";
                }else{
                    List<String> stringList=findIDCityCounty(nonLinearJunction.get(i).JunctionExtID);
                    JunctionExtID=stringList.get(0);
                    City=stringList.get(1);
                    County=stringList.get(2);
                }

                // Get whether it is signalized or not
                int Signalized=nonLinearJunction.get(i).Signalized;
                // Get the section Information
                int FirstSectionID=nonLinearJunction.get(i).EntranceSections[j];
                System.out.println("Junction ID="+JunctionID+", Junction Name="+JunctionName+", First Section ID="+FirstSectionID);

                AimsunSection aimsunSection=findSectionInformation(FirstSectionID,aimsunNetwork.aimsunSectionList);
                if(aimsunSection.equals(null)){
                    System.out.println("Can not find the section information!");
                    System.exit(-1);
                }
                String FirstSectionName=aimsunSection.SectionName;
                String FirstSectionExtID=aimsunSection.SectionExtID;

                // Find the links belonging to the same approach
                List<Integer> ListOfSections=findUpstreamSections(linearJunction, FirstSectionID);
                List<AimsunSection> aimsunSectionByApproachList=getSectionProperties(aimsunNetwork.aimsunJunctionList,
                        aimsunNetwork.aimsunSectionList, ListOfSections);
                SectionBelongToApproach sectionBelongToApproach=new SectionBelongToApproach(ListOfSections,aimsunSectionByApproachList);

                // Get turning properties at the downstream section
                TurningBelongToApproach turningBelongToApproach=findTurningsAtFirstSection(nonLinearJunction.get(i), FirstSectionID);

                // Lane-Turning Properties
                List<LaneTurningProperty> laneTurningProperty=findLaneTurningProperty(sectionBelongToApproach, turningBelongToApproach);

                // Get the aggregated road geometry: lanes, length, and turning pockets
                GeoDesign geoDesign=getGeometryDesign(sectionBelongToApproach, turningBelongToApproach, laneTurningProperty);

                // Get the detector information
                DetectorProperty detectorProperty=getDetectorProperty(sectionBelongToApproach,aimsunNetwork.aimsunDetectorList);

                // Get default signal settings
                DefaultSignalSetting defaultSignalSetting=new DefaultSignalSetting(MainFunction.cBlock.CycleLength,
                        MainFunction.cBlock.LeftTurnGreen,MainFunction.cBlock.ThroughGreen,MainFunction.cBlock.RightTurnGreen,
                        MainFunction.cBlock.LeftTurnSetting);

                // Get midlink configuration files: This is used to get midlink counts
                // Currently not doing this.

                // Get control plans for each approach
                //ControlPlanBelongToJunction;

                // Add the tmpAimsunApproach to the end
                AimsunApproach tmpAimsunApproach=new AimsunApproach(JunctionID,JunctionName, JunctionExtID, City,
                        County, Signalized, FirstSectionID, FirstSectionName, FirstSectionExtID,
                        sectionBelongToApproach, turningBelongToApproach,laneTurningProperty, geoDesign,
                        detectorProperty,defaultSignalSetting, null,ControlPlanBelongToJunction,aimsunJunction);
                aimsunApproachList.add(tmpAimsunApproach);
            }
        }

        AimsunNetworkByApproach aimsunNetworkByApproach=new AimsunNetworkByApproach(aimsunNetwork,aimsunApproachList);
        System.out.println("Finish reconstructing the network!");
        return aimsunNetworkByApproach;
    }

    /**
     *
     * @param aimsunJunctionList List<AimsunJunction>
     * @return List<AimsunJunction>
     */
    public static List<AimsunJunction> ReAdjustJunctionGeneratedByCentroidsInAimsun(List<AimsunJunction> aimsunJunctionList){
        // This function is used to re-adjust junctions generated by centroids in Aimsun
        // E.g., 2 upstream links, 2 downstream links, but with 2 turns
        // Will add a new junction in order to separate this junction

        // First find the maximum ID
        int maxID=0;
        for(int i=0;i<aimsunJunctionList.size();i++){
            AimsunJunction aimsunJunction=aimsunJunctionList.get(i);
            // Check junction
            if(aimsunJunction.JunctionID>maxID){
                maxID=aimsunJunction.JunctionID;
            }
            // Check upstream sections
            for(int j=0;j<aimsunJunction.NumEntranceSections;j++){
                if(aimsunJunction.EntranceSections[j]>maxID){
                    maxID=aimsunJunction.EntranceSections[j];
                }
            }
            // Check downstream sections
            for(int j=0;j<aimsunJunction.NumExitSections;j++){
                if(aimsunJunction.ExitSections[j]>maxID){
                    maxID=aimsunJunction.ExitSections[j];
                }
            }
            // Check turnings
            for(int j=0;j<aimsunJunction.NumTurns;j++){
                if(aimsunJunction.Turnings.get(j).getTurnID()>maxID){
                    maxID=aimsunJunction.Turnings.get(j).getTurnID();
                }
            }
        }
        maxID=maxID+1; // Add one

        // Second, check those fake junctions generated by centroids
        List<AimsunJunction> aimsunJunctionListNew=new ArrayList<AimsunJunction>();
        for(int i=0;i<aimsunJunctionList.size();i++){
            AimsunJunction aimsunJunction=aimsunJunctionList.get(i);
            // Copy AimsunSectionTurning
            List<AimsunSectionTurning> aimsunSectionTurnings=new ArrayList<AimsunSectionTurning>();
            aimsunSectionTurnings.addAll(aimsunJunction.SectionTurningList);
            // Copy AimsunTurning
            List<AimsunTurning> aimsunTurningList=new ArrayList<AimsunTurning>();
            aimsunTurningList.addAll(aimsunJunction.Turnings);
            if(aimsunJunction.NumTurns==2 && aimsunJunction.NumExitSections==2 && aimsunJunction.NumEntranceSections==2){
                AimsunJunction aimsunJunctionNew=new AimsunJunction(aimsunJunction.JunctionID, aimsunJunction.JunctionName,
                        aimsunJunction.JunctionExtID, aimsunJunction.Signalized, aimsunJunction.NumEntranceSections,
                        aimsunJunction.NumExitSections, aimsunJunction.NumTurns, aimsunJunction.EntranceSections,
                        aimsunJunction.ExitSections, aimsunTurningList, aimsunSectionTurnings);
                int UpstreamSection1=aimsunJunction.EntranceSections[0];
                int SectionIdx1=0;
                int UpstreamSection2=aimsunJunction.EntranceSections[1];
                int SectionIdx2=0;
                int TurnIdx1=0;
                int TurnIdx2=0;
                for(int j=0;j<aimsunJunction.SectionTurningList.size();j++){
                    if(aimsunJunction.SectionTurningList.get(j).SectionID==UpstreamSection1){
                        if(aimsunJunction.SectionTurningList.get(j).NumTurns==1){
                            SectionIdx1=j;
                        }else{
                            System.out.println("Wrong setting of turnings; Wrong network connectivity!");
                            System.exit(-1);
                        }
                    }
                    if(aimsunJunction.SectionTurningList.get(j).SectionID==UpstreamSection2){
                        if(aimsunJunction.SectionTurningList.get(j).NumTurns==1){
                            SectionIdx2=j;
                        }else{
                            System.out.println("Wrong setting of turnings; Wrong network connectivity!");
                            System.exit(-1);
                        }
                    }
                }
                int DownstreamSection1=0;
                int DownstreamSection2=0;
                for(int j=0;j<aimsunJunction.Turnings.size();j++){
                    if(aimsunJunction.Turnings.get(j).OrigSectionID==UpstreamSection1){
                        DownstreamSection1=aimsunJunction.Turnings.get(j).DestSectionID;
                        TurnIdx1=j;
                    }
                    if(aimsunJunction.Turnings.get(j).OrigSectionID==UpstreamSection2){
                        DownstreamSection2=aimsunJunction.Turnings.get(j).DestSectionID;
                        TurnIdx2=j;
                    }
                }

                //Remove upstream section2 and turn2 from aimsunJunctionList.get(i)
                aimsunJunctionList.get(i).NumEntranceSections=1;
                aimsunJunctionList.get(i).EntranceSections=new int[]{UpstreamSection1};
                aimsunJunctionList.get(i).NumExitSections=1;
                aimsunJunctionList.get(i).ExitSections=new int[]{DownstreamSection1};
                aimsunJunctionList.get(i).NumTurns=1;
                aimsunJunctionList.get(i).Turnings.remove(TurnIdx2);
                aimsunJunctionList.get(i).SectionTurningList.remove(SectionIdx2);

                //Remove upstream section1 and turn1 from aimsunApproachNew
                maxID=maxID+1;
                aimsunJunctionNew.JunctionID=maxID;
                aimsunJunctionNew.NumEntranceSections=1;
                aimsunJunctionNew.EntranceSections=new int[]{UpstreamSection2};
                aimsunJunctionNew.NumExitSections=1;
                aimsunJunctionNew.ExitSections=new int[]{DownstreamSection2};
                aimsunJunctionNew.NumTurns=1;
                aimsunJunctionNew.Turnings.remove(TurnIdx1);
                aimsunJunctionNew.SectionTurningList.remove(SectionIdx1);
                // Add it to the temporary list
                aimsunJunctionListNew.add(aimsunJunctionNew);
            }
        }

        aimsunJunctionList.addAll(aimsunJunctionListNew);

        return aimsunJunctionList;
    }
    /**
     *
     * @param sectionBelongToApproach SectionBelongToApproach class
     * @param aimsunDetectorList List<AimsunDetector> class
     * @return DetectorProperty class
     */
    public static DetectorProperty getDetectorProperty(SectionBelongToApproach sectionBelongToApproach,
                                                       List<AimsunDetector> aimsunDetectorList){
        // This is the function to get the detector properties

        List<Integer> ListOfSections=sectionBelongToApproach.ListOfSections;
        List<AimsunSection> Property=sectionBelongToApproach.Property;

        // Get the detectors belonging to the sections
        List<AimsunDetector> selectedDetectorList=new ArrayList<AimsunDetector>();
        for(int j=0;j<aimsunDetectorList.size();j++){
            for(int i=0;i<ListOfSections.size();i++){
                if(ListOfSections.get(i)==aimsunDetectorList.get(j).SectionID){
                    selectedDetectorList.add(aimsunDetectorList.get(j));
                    break;
                }
            }
        }

        // Get the possible movements
        List<String> movementLeft=trafficMovementLibrary("Left");
        List<String> movementRight=trafficMovementLibrary("Right");
        List<String> movementAdvance=trafficMovementLibrary("Advance");
        List<String> movementGeneral=trafficMovementLibrary("General");

        // Initialize the movements belonging to different types
        List<DetectorMovementProperty> ExclusiveLeftTurn=new ArrayList<DetectorMovementProperty>();
        List<DetectorMovementProperty> ExclusiveRightTurn=new ArrayList<DetectorMovementProperty>();
        List<DetectorMovementProperty> AdvanceDetectors=new ArrayList<DetectorMovementProperty>();
        List<DetectorMovementProperty> GeneralStopbarDetectors=new ArrayList<DetectorMovementProperty>();

        // Check for exclusive left-turns
        for(int i=0;i<movementLeft.size();i++) {
            DetectorMovementProperty detectorMovementProperty = getDetectorMovementProperty
                    ("Stopbar",movementLeft.get(i), selectedDetectorList, Property);
            if(detectorMovementProperty!=null){
                ExclusiveLeftTurn.add(detectorMovementProperty);
            }
        }

        // Check for exclusive right-turns
        for(int i=0;i<movementRight.size();i++) {
            DetectorMovementProperty detectorMovementProperty = getDetectorMovementProperty
                    ("Stopbar",movementRight.get(i), selectedDetectorList, Property);
            if(detectorMovementProperty!=null){
                ExclusiveRightTurn.add(detectorMovementProperty);
            }
        }

        // Check for general detectors
        for(int i=0;i<movementGeneral.size();i++) {
            DetectorMovementProperty detectorMovementProperty = getDetectorMovementProperty
                    ("Stopbar",movementGeneral.get(i), selectedDetectorList, Property);
            if(detectorMovementProperty!=null){
                GeneralStopbarDetectors.add(detectorMovementProperty);
            }
        }

        // Check for Advance detectors
        for(int i=0;i<movementAdvance.size();i++) {
            DetectorMovementProperty detectorMovementProperty = getDetectorMovementProperty
                    ("Advance",movementAdvance.get(i), selectedDetectorList, Property);
            if(detectorMovementProperty!=null){
                AdvanceDetectors.add(detectorMovementProperty);
            }
        }

        DetectorProperty detectorProperty=new DetectorProperty(ExclusiveLeftTurn,ExclusiveRightTurn,
                AdvanceDetectors,GeneralStopbarDetectors);
        return detectorProperty;
    }

    /**
     *
     * @param Type String: Stopbar / Advance
     * @param MovementInput String: traffic movements in the function: trafficMovementLibrary(Type)
     * @param aimsunDetector List<AimsunDetector>  class
     * @param aimsunSection List<AimsunSection> class
     * @return DetectorMovementProperty class
     */
    public static DetectorMovementProperty getDetectorMovementProperty(String Type, String MovementInput,List<AimsunDetector> aimsunDetector,
                                                                       List<AimsunSection> aimsunSection){
        // This function is used to get the detector movement property
        DetectorMovementProperty detectorMovementProperty=null;

        List<Integer> DetectorIDs =new ArrayList<Integer>();
        List<Double> DetectorLengths =new ArrayList<Double>();
        List<Double> DistancesToStopbar =new ArrayList<Double>();
        List<Integer> NumberOfLanes =new ArrayList<Integer>();

        for(int i=0;i<aimsunDetector.size();i++){ // Loop for the selected detectors
            if(aimsunDetector.get(i).Movement.equals(MovementInput)){ // If it matches the movement input
                // Add the detector ID,length, and number of lanes
                DetectorIDs.add(Integer.parseInt(aimsunDetector.get(i).ExternalID.trim()));
                DetectorLengths.add(aimsunDetector.get(i).Length);
                NumberOfLanes.add(aimsunDetector.get(i).NumOfLanes);

                if(Type.equals("Stopbar")){ // For stopbar detectors, distanceToStopbar=0
                    DistancesToStopbar.add(0.0);
                }else if(Type.equals("Advance")){ // For Advance detectors, search
                    double distance=0;
                    for(int j=0;j<aimsunSection.size();j++){
                        // Search the links from downstream to upstream
                        // Note the sections are organized from downstream to upstream

                        // Get the link/section lane length
                        double maxLaneLength=0;
                        for(int k=0;k<aimsunSection.get(j).LaneLengths.length;k++){
                            if(aimsunSection.get(j).LaneLengths[k]>maxLaneLength){
                                maxLaneLength=aimsunSection.get(j).LaneLengths[k];
                            }
                        }
                        // If find the corresponding section
                        if(aimsunDetector.get(i).SectionID==aimsunSection.get(j).SectionID){
                            distance=distance+Math.max(0.0,maxLaneLength-
                                    (aimsunDetector.get(i).InitialPosition+aimsunDetector.get(i).FinalPosition)/2);
                            break;
                        }
                        else{// If not found
                            distance=distance+maxLaneLength;
                        }
                    }
                    DistancesToStopbar.add(distance);
                }
            }
        }
        // If it is not empty
        if(!DetectorIDs.isEmpty()){
            detectorMovementProperty=new DetectorMovementProperty(MovementInput, DetectorIDs, DetectorLengths,
                    DistancesToStopbar,NumberOfLanes);
        }
        return detectorMovementProperty;
    }

    /**
     *
     * @param Type Left, Right, Advance, General
     * @return List<String> possibleMovements
     */
    public static List<String> trafficMovementLibrary(String Type){
        // This function returns the traffic movements belonging to a certain type/category
        List<String> possibleMovements=null;
        if(Type.equals("Left")){
            possibleMovements=new ArrayList(Arrays.asList("Left Turn","Left Turn Queue"));
        }else if(Type.equals("Right")){
            possibleMovements=new ArrayList(Arrays.asList("Right Turn","Right Turn Queue"));
        }else if(Type.equals("Advance")){
            possibleMovements=new ArrayList(Arrays.asList("Advance","Advance Left Turn",
                    "Advance Right Turn","Advance Through","Advance Through and Right","Advance Left and Through",
                    "Advance Left and Right"));
        }else if(Type.equals("General")){
            possibleMovements=new ArrayList(Arrays.asList("All Movements","Through","Left and Right",
                    "Left and Through","Through and Right"));
        }else{
            System.out.println("Unknown movement type!");
            System.exit(-1);
        }
        return possibleMovements;
    }


    /**
     *
     * @param sectionBelongToApproach SectionBelongToApproach class
     * @param turningBelongToApproach TurningBelongToApproach class
     * @param laneTurningProperty List<LaneTurningProperty> class
     * @return GeoDesign class
     */
    public static GeoDesign getGeometryDesign(SectionBelongToApproach sectionBelongToApproach,TurningBelongToApproach turningBelongToApproach,
                                              List<LaneTurningProperty> laneTurningProperty) {
        // This function is used to get the geometry design

        GeoDesign geoDesign = new GeoDesign(0, 0, 0,
                null, null,null);

        // Get the total link length
        double LinkLength = 0;
        for (int i = 0; i < sectionBelongToApproach.ListOfSections.size(); i++) {
            double maxLength = 0;
            //Get the max length of a given link
            for (int j = 0; j < sectionBelongToApproach.Property.get(i).LaneLengths.length; j++) {
                if (sectionBelongToApproach.Property.get(i).LaneLengths[j] > maxLength) {
                    maxLength = sectionBelongToApproach.Property.get(i).LaneLengths[j];
                }
            }
            LinkLength = LinkLength + maxLength;
        }
        geoDesign.LinkLength = LinkLength;

        // Get the number of upstream lanes (at the last section)
        // Only Check full lanes (it is possible upstream lane is not full lane)
        int NumOfUpstreamLanes = 0;
        for (int i = 0; i < sectionBelongToApproach.Property.get(sectionBelongToApproach.Property.size() - 1).IsFullLane.length; i++) {
            if (sectionBelongToApproach.Property.get(sectionBelongToApproach.Property.size() - 1).IsFullLane[i] == 1) {
                NumOfUpstreamLanes = NumOfUpstreamLanes + 1;
            }
        }
        geoDesign.NumOfUpstreamLanes = NumOfUpstreamLanes;

        // Get the turning pockets
        ExclusiveTurningProperty exclusiveTurningPropertyLeft=new ExclusiveTurningProperty(0,0 );
        ExclusiveTurningProperty exclusiveTurningPropertyRight=new ExclusiveTurningProperty(0,0 );
        int NumOfDownstreamLanes=0;
        for(int i=0;i<laneTurningProperty.get(0).NumLanes;i++){// Loop for each lane
            if(laneTurningProperty.get(0).Lanes.get(i).IsExclusive==1 &&
                    laneTurningProperty.get(0).Lanes.get(i).TurningMovements.size()>0){ // If it is exclusive and has associated turns
                for(int j=0;j<turningBelongToApproach.TurningProperty.size();j++){
                    // Search the corresponding turning movement
                    if(laneTurningProperty.get(0).Lanes.get(i).TurningMovements.get(0)==
                            turningBelongToApproach.TurningProperty.get(j).TurnID){
                        // If it is an exclusive left-turn lane
                        if(turningBelongToApproach.TurningProperty.get(j).Movement.equals("Left Turn")){
                            if(exclusiveTurningPropertyLeft.NumLanes==0){
                                exclusiveTurningPropertyLeft.NumLanes=1;
                                exclusiveTurningPropertyLeft.Pocket=laneTurningProperty.get(0).Lanes.get(i).Length;
                            }else{
                                exclusiveTurningPropertyLeft.NumLanes=exclusiveTurningPropertyLeft.NumLanes+1;
                                exclusiveTurningPropertyLeft.Pocket=Math.min(exclusiveTurningPropertyLeft.Pocket,
                                        laneTurningProperty.get(0).Lanes.get(i).Length);
                            }
                        }
                        //If it is an exclusive right-turn lane
                        else if(turningBelongToApproach.TurningProperty.get(j).Movement.equals("Right Turn")) {
                            if(exclusiveTurningPropertyRight.NumLanes==0){
                                exclusiveTurningPropertyRight.NumLanes=1;
                                exclusiveTurningPropertyRight.Pocket=laneTurningProperty.get(0).Lanes.get(i).Length;
                            }else{
                                exclusiveTurningPropertyRight.NumLanes=exclusiveTurningPropertyRight.NumLanes+1;
                                exclusiveTurningPropertyRight.Pocket=Math.min(exclusiveTurningPropertyRight.Pocket,
                                        laneTurningProperty.get(0).Lanes.get(i).Length);
                            }
                        }
                        // For all other cases: through lane or no descriptions
                        else{
                            NumOfDownstreamLanes=NumOfDownstreamLanes+1;
                        }
                        break; // If found the corresponding turning movement, break.
                    }
                }
            }
            // If it is not an exclusive lane
            else
            {
                NumOfDownstreamLanes=NumOfDownstreamLanes+1;
            }
        }
        geoDesign.ExclusiveLeftTurn=exclusiveTurningPropertyLeft;
        geoDesign.ExclusiveRightTurn=exclusiveTurningPropertyRight;
        geoDesign.NumOfDownstreamLanes=NumOfDownstreamLanes;

        // Update turn indicator
        int [] TurnIndicator=new int[]{0,0,0};
        for (int i=0;i<turningBelongToApproach.TurningProperty.size();i++){
            if(turningBelongToApproach.TurningProperty.get(i).Movement.equals("Left Turn")){
                TurnIndicator[0]=1;
            }
            if(turningBelongToApproach.TurningProperty.get(i).Movement.equals("Through")){
                TurnIndicator[1]=1;
            }
            if(turningBelongToApproach.TurningProperty.get(i).Movement.equals("Right Turn")){
                TurnIndicator[2]=1;
            }
        }
        geoDesign.TurnIndicator=TurnIndicator;

        return geoDesign;
    }

    /**
     *
     * @param sectionBelongToApproach SectionBelongToApproach class
     * @param turningBelongToApproach TurningBelongToApproach class
     * @return List<LaneTurningProperty> class
     */
    public static List<LaneTurningProperty> findLaneTurningProperty(SectionBelongToApproach sectionBelongToApproach,
                                                                    TurningBelongToApproach turningBelongToApproach){
        // This function is used to find the list of lane-turning properties

        List<LaneTurningProperty> laneTurningPropertyList=new ArrayList<LaneTurningProperty>();

        for(int i=0;i<sectionBelongToApproach.ListOfSections.size();i++){
            // Loop for the list of sections
            int SectionID=sectionBelongToApproach.ListOfSections.get(i);
            int NumLanes=sectionBelongToApproach.Property.get(i).NumLanes;

            // Note: Lane ID=1:N from leftmost to rightmost
            //       Turn organized from leftmost to rightmost
            //       Each turn: fromLane (leftmost) to toLane (rightmost)
            //       Lane Length: from leftmost to rightmost
            List<LaneProperty> lanePropertyList=new ArrayList<LaneProperty>();
            for(int j=0;j<NumLanes;j++){
                int LaneID=j+1; // Get the lane ID; index starting from 0 in Java
                double Length=sectionBelongToApproach.Property.get(i).LaneLengths[j]; // Get the lane length

                // Get the turns using the current lane
                List<Integer> TurningMovements =new ArrayList<Integer>();
                for(int k=0;k<turningBelongToApproach.TurningProperty.size();k++){
                    if(turningBelongToApproach.TurningProperty.get(k).OrigFromLane<=LaneID &&
                            turningBelongToApproach.TurningProperty.get(k).OrigToLane>=LaneID)
                        TurningMovements.add(turningBelongToApproach.TurningProperty.get(k).TurnID);
                }

                // Get whether it is exclusive or not
                int IsExclusive=1;
                if(TurningMovements.size()>1) {
                    IsExclusive = 0;
                }

                // Get the number of lanes by Turns
                int [] numLaneByTurn= new int[TurningMovements.size()];
                int totLane=0;
                for(int k=0;k<TurningMovements.size();k++){
                    // Get the number of lanes associated to a given turn
                    for(int p=0;p<turningBelongToApproach.TurningProperty.size();p++){
                        if(turningBelongToApproach.TurningProperty.get(p).TurnID==TurningMovements.get(k)){
                            numLaneByTurn[k]=turningBelongToApproach.TurningProperty.get(p).OrigToLane-
                                    turningBelongToApproach.TurningProperty.get(p).OrigFromLane+1;
                            totLane=totLane+numLaneByTurn[k];
                            break;
                        }
                    }
                }
                // Get the weighted proportions of turns in a given lane according to their number of lanes
                // within the section
                List<Double> Proportions=new ArrayList<Double>();
                for(int k=0;k<TurningMovements.size();k++){
                    Proportions.add(numLaneByTurn[k]*1.0/totLane);
                }

                // Append the lane property
                LaneProperty tmpLaneProperty=new LaneProperty(LaneID, IsExclusive, TurningMovements,Proportions, Length);
                lanePropertyList.add(tmpLaneProperty);
            }
            // Append the lane-turning property
            LaneTurningProperty tmpLaneTurningProperty=new LaneTurningProperty(SectionID, NumLanes, lanePropertyList);
            laneTurningPropertyList.add(tmpLaneTurningProperty);
        }
        return laneTurningPropertyList;
    }


    /**
     *
     * @param aimsunJunction AimsunJunction class
     * @param FirstSectionID First section ID (int)
     * @return TurningBelongToApproach class
     */
    public static TurningBelongToApproach findTurningsAtFirstSection(AimsunJunction aimsunJunction,int FirstSectionID){
        // This function is used to find turnings belonging to the first section

        int [] ListOfTurns=null;
        List<AimsunTurning> aimsunTurningList=new ArrayList<readFromAimsun.AimsunTurning>();
        for(int i=0;i<aimsunJunction.SectionTurningList.size();i++){ // Loop for all section-turning components
            if(aimsunJunction.SectionTurningList.get(i).SectionID==FirstSectionID){ // Check the section ID, if found
                ListOfTurns=aimsunJunction.SectionTurningList.get(i).TurnIDFromLeftToRight; // Get the list of turns
                for(int j=0; j<ListOfTurns.length;j++){ // Loop for each turn
                    for(int k=0;k<aimsunJunction.Turnings.size();k++){ // Loop for all turns belonging to the intersection
                        if(ListOfTurns[j]==aimsunJunction.Turnings.get(k).TurnID){ // If the same turn ID found
                            aimsunTurningList.add(aimsunJunction.Turnings.get(k)); // Append the turning property
                            break;
                        }
                    }
                }
            }
        }
        if(ListOfTurns.equals(null)){
            System.out.println("Can not find the turns!");
            System.exit(-1);
        }
        TurningBelongToApproach turningBelongToApproach=new TurningBelongToApproach(ListOfTurns,aimsunTurningList);
        return turningBelongToApproach;
    }


    /**
     *
     * @param aimsunJunctionList List<AimsunJunction> class
     * @param aimsunSectionList List<AimsunSection> class
     * @param ListOfSections List<Integer>
     * @return List<AimsunSection> class
     */
    public static List<AimsunSection> getSectionProperties(List<AimsunJunction> aimsunJunctionList, List<AimsunSection> aimsunSectionList,
                                                           List<Integer> ListOfSections){
        // This function is used to get/update the section information belonging to a given approach
        List<AimsunSection> aimsunSectionByApproachList=new ArrayList<AimsunSection>();

        for (int i=0;i<ListOfSections.size();i++){
            AimsunSection tmpAimsunSection=null;
            // Loop for the corresponding section
            for (int j=0;j<aimsunSectionList.size();j++){
                if(aimsunSectionList.get(j).SectionID==ListOfSections.get(i)){
                    tmpAimsunSection=aimsunSectionList.get(j);
                    break;
                }
            }
            if(tmpAimsunSection==null){
                System.out.println("Can not file the section information!");
                System.exit(-1);
            }
            // Loop for the downstream and upstream junctions
            boolean findUpstreamJunction=false;
            boolean findDownstreamJunction=false;
            for (int j=0;j<aimsunJunctionList.size();j++) {
                if (findDownstreamJunction == false){
                    for (int k = 0; k < aimsunJunctionList.get(j).NumEntranceSections; k++) {
                        if (aimsunJunctionList.get(j).EntranceSections[k] == ListOfSections.get(i)) {
                            tmpAimsunSection.DownstreamJunction = aimsunJunctionList.get(j);
                            findDownstreamJunction=true;
                        }
                    }
                }
                if (findUpstreamJunction == false){
                    for (int k = 0; k < aimsunJunctionList.get(j).NumExitSections; k++) {
                        if (aimsunJunctionList.get(j).ExitSections[k] == ListOfSections.get(i)) {
                            tmpAimsunSection.UpstreamJunction = aimsunJunctionList.get(j);
                            findUpstreamJunction=true;
                        }
                    }
                }
            }
            aimsunSectionByApproachList.add(tmpAimsunSection);
        }
        return aimsunSectionByApproachList;
    }


    /**
     *
     * @param linearJunction List<AimsunJunction> class
     * @param SectionID Section ID (int)
     * @return List<Integer> ListOfSections
     */
    public static List<Integer>  findUpstreamSections(List<AimsunJunction> linearJunction, int SectionID){
        // This function is used to find upstream sections belonging to the same approach
        List<Integer> ListOfSections =new ArrayList<Integer>();

        // Find the upstream ones
        boolean findStatus=true;
        int ExitSection=SectionID;
        while(findStatus){
            int curAdd=0;
            for(int i=0;i<linearJunction.size();i++){ // Loop for all junctions
                curAdd=i;
                if(linearJunction.get(i).ExitSections[0]==ExitSection){ //Find the right one?
                    ListOfSections.add(linearJunction.get(i).ExitSections[0]);
                    ExitSection=linearJunction.get(i).EntranceSections[0];
                    break;
                }
            }
            if(curAdd>=linearJunction.size()-1){ // If can not find more, stop While()
                findStatus=false;
            }
        }

        ListOfSections.add(ExitSection); // Add the last section:ExitSection
        return ListOfSections;
    }

    /**
     *
     * @param SectionID Section ID (int)
     * @param aimsunSectionList List<AimsunSection> class
     * @return AimsunSection
     */
    public static AimsunSection findSectionInformation(int SectionID,List<AimsunSection> aimsunSectionList){
        // This function is used to find the information for a given section
        AimsunSection aimsunSection=null;
        for(int i=0;i<aimsunSectionList.size();i++){
            if(aimsunSectionList.get(i).SectionID==SectionID){
                aimsunSection=aimsunSectionList.get(i);
            }
        }
        return aimsunSection;
    }

    /**
     *
     * @param ExternalID External ID (String)
     * @return List<String>: ID, City, County
     */
    public static List<String> findIDCityCounty(String ExternalID){
        // This is the function to find Intersection ID, City, and County from the External ID
        List<String> stringList=new ArrayList<String>();
        String [] tmpLine=ExternalID.split(" ");

        if(tmpLine.length!=2){
            stringList.add("N/A"); //ID
            stringList.add("N/A"); //City
            stringList.add("N/A"); //County
        }else{
            stringList.add(tmpLine[1]);
            if(tmpLine[0].equals("AR")) {
                stringList.add("Arcadia");
                stringList.add("Los Angeles");
            }else if(tmpLine[0].equals("PA")){
                stringList.add("Pasadena");
                stringList.add("Los Angeles");
            }else{
                stringList.add(tmpLine[0]);
                stringList.add("N/A");
            }
        }
        return stringList;
    }

    /**
     *
     * @param JunctionID Junction ID
     * @param aimsunControlPlanJunction List<AimsunControlPlanJunction> class
     * @param aimsunMasterControlPlans List<AimsunMasterControlPlan> class
     * @return List<AimsunControlPlanJunction> class
     */
    public static List<AimsunControlPlanJunction> getControlPlanBelongToJunction(int JunctionID
            , List<AimsunControlPlanJunction> aimsunControlPlanJunction, List<AimsunMasterControlPlan> aimsunMasterControlPlans){
        // This function is to return the control plans belonging to the same junction ID

        List<AimsunControlPlanJunction> tmpAimsunControlPlanJunctionList=new ArrayList<AimsunControlPlanJunction>();
        List<Integer> PlanOffset=new ArrayList<Integer>();
        for (int i=0;i<aimsunControlPlanJunction.size();i++){ // Loop for each plan-junction
            if(aimsunControlPlanJunction.get(i).JunctionID==JunctionID){ // The same junction ID
                //Find the corresponding Master Control Plan
                AimsunControlPlanJunction tmpAimsunControlPlanJunction=getMasterControlPlanBelongToControlPlan
                        (aimsunControlPlanJunction.get(i),aimsunMasterControlPlans);

                tmpAimsunControlPlanJunctionList.add(tmpAimsunControlPlanJunction);
                PlanOffset.add(aimsunControlPlanJunction.get(i).PlanOffset);
            }
        }
        // Organize the control plans according to the Plan Offsets
        Collections.sort(PlanOffset); //Sort the Plan Offset
        if(tmpAimsunControlPlanJunctionList.size()<=1){ // If less than or equal to one plan-junction
            return tmpAimsunControlPlanJunctionList;
        }else{ // If more than one, sort the plan-junction according to the plan offsets
            List<AimsunControlPlanJunction> AimsunControlPlanJunctionList= new ArrayList<AimsunControlPlanJunction>();
            for (int i=0;i<PlanOffset.size();i++) { // Loop for each plan Offset
                for (int j = 0; j < tmpAimsunControlPlanJunctionList.size(); j++) { // Loop for each plan-junction
                    if (tmpAimsunControlPlanJunctionList.get(j).PlanOffset==PlanOffset.get(i)){
                        AimsunControlPlanJunctionList.add(tmpAimsunControlPlanJunctionList.get(j));
                        break;
                    }
                }
            }
            return AimsunControlPlanJunctionList;
        }
    }

    /**
     *
     * @param aimsunControlPlanJunction AimsunControlPlanJunction class
     * @param aimsunMasterControlPlans List<AimsunMasterControlPlan> class
     * @return AimsunControlPlanJunction class
     */
    public static AimsunControlPlanJunction getMasterControlPlanBelongToControlPlan
            (AimsunControlPlanJunction aimsunControlPlanJunction,List<AimsunMasterControlPlan> aimsunMasterControlPlans){
        // This function is used to get the corresponding master control plans belonging to the same control plan

        AimsunControlPlanJunction tmpAimsunControlPlanJunction=aimsunControlPlanJunction;
        for (int i=0;i<aimsunMasterControlPlans.size();i++){
            if(aimsunMasterControlPlans.get(i).ControlPlanID==tmpAimsunControlPlanJunction.PlanID){
                tmpAimsunControlPlanJunction.MasterControlPlan.add(aimsunMasterControlPlans.get(i));
            }
        }
        return tmpAimsunControlPlanJunction;
    }

    /**
     *
     * @param aimsunJunctionList List<AimsunJunction> class
     * @param Type String: Linear /NonLinear
     * @return List<AimsunJunction> class
     */
    public static List<AimsunJunction> getLinearOrNonLinearJunction(List<AimsunJunction> aimsunJunctionList, String Type){
        // This function is used to get the linear (1-by-1) or non-linear(n-by-m) junction
        List<AimsunJunction> aimsunJunctionSelected=new ArrayList<AimsunJunction>();

        for (int i=0;i<aimsunJunctionList.size();i++){
            if(Type.equals("Linear")){
                if(aimsunJunctionList.get(i).NumEntranceSections==1 &&
                        aimsunJunctionList.get(i).NumExitSections==1){ // If not signalized & linear
                    aimsunJunctionSelected.add(aimsunJunctionList.get(i));
                }
            }else if(Type.equals("NonLinear")){
                if(aimsunJunctionList.get(i).NumEntranceSections>=1 &&
                        aimsunJunctionList.get(i).NumExitSections>=1 &&
                        aimsunJunctionList.get(i).NumEntranceSections+
                                aimsunJunctionList.get(i).NumExitSections >2){
                    // If nonlinear
                    aimsunJunctionSelected.add(aimsunJunctionList.get(i));
                }
            }
            else{
                System.out.println("Unknown junction type!");
                System.exit(-1);
            }
        }
        return aimsunJunctionSelected;
    }

}
