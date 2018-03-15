package networkInput;

/**
 * Created by Qijian-Gan on 10/13/2017.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.util.*;

import main.MainFunction;
import networkInput.readFromAimsun;
import sun.awt.image.ImageWatched;

public class reconstructNetwork {

    public static class AimsunNetworkByApproach{
        // This is the profile for Aimsun network by Approach
        public AimsunNetworkByApproach(readFromAimsun.AimsunNetwork _aimsunNetwork,List<AimsunApproach> _aimsunNetworkByApproach){
            this.aimsunNetwork=_aimsunNetwork;
            this.aimsunNetworkByApproach=_aimsunNetworkByApproach;
        }

        public readFromAimsun.AimsunNetwork aimsunNetwork; // Aimsun network
        public List<AimsunApproach> aimsunNetworkByApproach; // Reconstructed Aimsun network at the approach level
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
                              List<readFromAimsun.AimsunControlPlanJunction> _controlPlanJunction,
                              readFromAimsun.AimsunJunction _aimsunJunction){
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
        public int JunctionID;
        public String JunctionName;
        public String JunctionExtID;
        public String City;
        public String County;
        public int Signalized;
        public int FirstSectionID;
        public String FirstSectionName;
        public String FirstSectionExtID;
        public SectionBelongToApproach sectionBelongToApproach; // Sections belonging to the approach
        public TurningBelongToApproach turningBelongToApproach; // Turnings belongs to the approach
        public List<LaneTurningProperty> laneTurningProperty;
        public GeoDesign geoDesign; // Geometry information
        public DetectorProperty detectorProperty;
        public DefaultSignalSetting defaultSignalSetting;
        public MidlinkCountConfig midlinkCountConfig; // It may be possible to get some information from midlink counts
        public List<readFromAimsun.AimsunControlPlanJunction> controlPlanJunction; // Control plans associated with the junction
        // Aimsun junction information: include this metric just in case we need more information in the future
        public readFromAimsun.AimsunJunction aimsunJunction;
    }

    //*************************************Detector Property********************************
    public static class DetectorProperty{
        // This is the profile for detector property at a given approach
        public DetectorProperty(List<DetectorMovementProperty> _ExclusiveLeftTurn,
                                List<DetectorMovementProperty> _ExclusiveRightTurn,
                                List<DetectorMovementProperty> _AdvancedDetectors,
                                List<DetectorMovementProperty> _GeneralStopbarDetectors){
            this.ExclusiveLeftTurn=_ExclusiveLeftTurn;
            this.ExclusiveRightTurn=_ExclusiveRightTurn;
            this.AdvancedDetectors=_AdvancedDetectors;
            this.GeneralStopbarDetectors=_GeneralStopbarDetectors;
        }
        public List<DetectorMovementProperty> ExclusiveLeftTurn; // Exclusive left turn (stop-bar) detectors
        public List<DetectorMovementProperty> ExclusiveRightTurn; // Exclusive right turn (stop-bar) detectors
        public List<DetectorMovementProperty> AdvancedDetectors; // Advanced detectors
        // General stop-bar detectors (through, or combined movements)
        public List<DetectorMovementProperty> GeneralStopbarDetectors;
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
        public String Movement;
        public List<Integer> DetectorIDs; // Detector IDs belonging to the same movement
        public List<Double> DetectorLengths;
        public List<Double> DistancesToStopbar;
        public List<Integer> NumberOfLanes;
    }

    //*************************************Section Property********************************
    public static class SectionBelongToApproach{
        // This is the profile for sections belonging to the same approach
        public SectionBelongToApproach(List<Integer> _ListOfSections, List<readFromAimsun.AimsunSection> _Property){
            this.ListOfSections=_ListOfSections;
            this.Property=_Property;
        }
        public List<Integer> ListOfSections; // The list of sections
        public List<readFromAimsun.AimsunSection> Property; // Section properties inherited from Aimsun
    }

    //*************************************Turning Property********************************
    public static class TurningBelongToApproach{
        // This is the profile for turnings belonging to the same approach
        public TurningBelongToApproach(int [] _TurningAtFirstSectionFromLeftToRight, List<readFromAimsun.AimsunTurning> _TurningProperty){
            this.TurningAtFirstSectionFromLeftToRight=_TurningAtFirstSectionFromLeftToRight;
            this.TurningProperty=_TurningProperty;
        }
        public int [] TurningAtFirstSectionFromLeftToRight; // List of turnings at the first section
        public List<readFromAimsun.AimsunTurning> TurningProperty; // Turning property inherited from Aimsun
    }

    //*************************************Lane-Turning Property********************************
    public static class LaneTurningProperty{
        // This is the profile for lane-turning property
        public LaneTurningProperty(int _SectionID, int _NumLanes, List<LaneProperty> _Lanes){
            this.SectionID=_SectionID;
            this.NumLanes=_NumLanes;
            this.Lanes=_Lanes;
        }
        public int SectionID;
        public int NumLanes;
        public List<LaneProperty> Lanes; // Lane properties
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
        public int LaneID;
        public int IsExclusive;
        public List<Integer> TurningMovements; // Turning movements on the lane
        public List<Double> Proportions; // The proportions of the turning movements on the lane
        public double Length;
    }

    //*************************************Geometry Property********************************
    public static class GeoDesign{
        // This is the profile for geometry design
        public GeoDesign(double _LinkLength, int _NumOfUpstreamLanes, int _NumOfDownstreamLanes,
                         ExclusiveTurningProperty _ExclusiveLeftTurn, ExclusiveTurningProperty _ExclusiveRightTurn){
            this.LinkLength=_LinkLength;
            this.NumOfUpstreamLanes=_NumOfUpstreamLanes;
            this.NumOfDownstreamLanes=_NumOfDownstreamLanes;
            this.ExclusiveLeftTurn=_ExclusiveLeftTurn;
            this.ExclusiveRightTurn=_ExclusiveRightTurn;
        }
        public double LinkLength;
        public int NumOfUpstreamLanes; // Number of upstream lanes
        public int NumOfDownstreamLanes; // Number of downstream lanes
        public ExclusiveTurningProperty ExclusiveLeftTurn; // Left-turn pockets
        public ExclusiveTurningProperty ExclusiveRightTurn; // Right-turn pockets
    }

    public static class ExclusiveTurningProperty{
        // This is the profile for exclusive turning property
        public ExclusiveTurningProperty(int _NumLanes, double _Pocket ){
            this.NumLanes=_NumLanes;
            this.Pocket=_Pocket;
        }
        public int NumLanes;
        public double Pocket; // Length of the turning pocket
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
        public int CycleLength;
        public int LeftTurnGreen; // Green time for left-turn
        public int ThroughGreen; // Green time for through
        public int RightTurnGreen; // Green time for right-turn
        public String LeftTurnSetting; // Protected, Permitted, Protected-Permitted
    }

    //*************************************Midlink Count Configuration********************************
    public static class MidlinkCountConfig{
        public MidlinkCountConfig(String _Location, String _Approach){
            this.Location=_Location;
            this.Approach=_Approach;
        }
        public String Location; // Location of the files that stores the mid-link counts
        public String Approach; // Which approach the mid-link counts belong to
    }

    //*************************************Midlink Count Configuration********************************
    //ControlPlanBelongJunction: Directly inherited from the Aimsun inputs

    //*************************************Major Functions********************************
    public static AimsunNetworkByApproach reconstructAimsunNetwork(){
        // This function is used to reconstruct Aimsun Network

        readFromAimsun.AimsunNetwork aimsunNetwork=readFromAimsun.readAimsunNetworkFiles();

        // Get Linear and NonLinear Junctions
        List<readFromAimsun.AimsunJunction> linearJunction=getLinearOrNonLinearJunction
                (aimsunNetwork.aimsunJunctionList,"Linear");
        List<readFromAimsun.AimsunJunction> nonLinearJunction=getLinearOrNonLinearJunction
                (aimsunNetwork.aimsunJunctionList,"NonLinear");

        List<AimsunApproach> aimsunApproachList=new ArrayList<AimsunApproach>();

        // Loop for each nonlinear junction
        for (int i=0;i<nonLinearJunction.size();i++){
            int JunctionID=nonLinearJunction.get(i).JunctionID;

            // Check the control plans for that junction
            List<readFromAimsun.AimsunControlPlanJunction> ControlPlanBelongToJunction=
                    getControlPlanBelongToJunction(JunctionID,aimsunNetwork.aimsunControlPlanJunctionList,
                            aimsunNetwork.aimsunMasterControlPlanList);

            // Check each entrance sections
            for (int j=0;j<nonLinearJunction.get(i).NumEntranceSections;j++){
                AimsunApproach tmpAimsunApproach=new AimsunApproach(0,null, null, null,
                        null, 0, 0, null, null,
                        null, null,null, null,
                        null,null, null,null,null);

                // Store the junction information
                tmpAimsunApproach.aimsunJunction=nonLinearJunction.get(i);
                // Get the junction ID and Name
                tmpAimsunApproach.JunctionID=nonLinearJunction.get(i).JunctionID;
                tmpAimsunApproach.JunctionName=nonLinearJunction.get(i).JunctionName;
                // Get the junction External ID, City, County
                if(nonLinearJunction.get(i).JunctionExtID.equals("")){
                    tmpAimsunApproach.JunctionExtID="N/A";
                    tmpAimsunApproach.City="N/A";
                    tmpAimsunApproach.County="N/A";
                }else{
                    List<String> stringList=findIDCityCounty(nonLinearJunction.get(i).JunctionExtID);
                    tmpAimsunApproach.JunctionExtID=stringList.get(0);
                    tmpAimsunApproach.City=stringList.get(1);
                    tmpAimsunApproach.County=stringList.get(2);
                }
                // Get whether it is signalized or not
                tmpAimsunApproach.Signalized=nonLinearJunction.get(i).Signalized;
                // Get the section Information
                tmpAimsunApproach.FirstSectionID=nonLinearJunction.get(i).EntranceSections[j];

                System.out.println("Junction ID="+tmpAimsunApproach.JunctionID+", Junction Name="+
                        tmpAimsunApproach.JunctionName+", First Section ID="+tmpAimsunApproach.FirstSectionID);

                readFromAimsun.AimsunSection aimsunSection=findSectionInformation
                        (tmpAimsunApproach.FirstSectionID,aimsunNetwork.aimsunSectionList);
                if(aimsunSection.equals(null)){
                    System.out.println("Can not find the section information!");
                    System.exit(-1);
                }
                tmpAimsunApproach.FirstSectionName=aimsunSection.SectionName;
                tmpAimsunApproach.FirstSectionExtID=aimsunSection.SectionExtID;

                // Find the links belonging to the same approach
                List<Integer> ListOfSections=findUpstreamSections(linearJunction, tmpAimsunApproach.FirstSectionID);
                List<readFromAimsun.AimsunSection> aimsunSectionByApproachList=getSectionProperties
                        (aimsunNetwork.aimsunJunctionList,aimsunNetwork.aimsunSectionList, ListOfSections);
                tmpAimsunApproach.sectionBelongToApproach=new SectionBelongToApproach(ListOfSections,aimsunSectionByApproachList);
                //tmpAimsunApproach.sectionBelongToApproach.ListOfSections=ListOfSections;
                //tmpAimsunApproach.sectionBelongToApproach.Property=aimsunSectionByApproachList;

                // Get turning properties at the downstream section
                TurningBelongToApproach turningBelongToApproach=findTurningsAtFirstSection(nonLinearJunction.get(i),
                        tmpAimsunApproach.FirstSectionID);
                tmpAimsunApproach.turningBelongToApproach=turningBelongToApproach;

                // Lane-Turning Properties
                List<LaneTurningProperty> laneTurningProperty=findLaneTurningProperty(tmpAimsunApproach);
                tmpAimsunApproach.laneTurningProperty=laneTurningProperty;

                // Get the aggregated road geometry: lanes, length, and turning pockets
                GeoDesign geoDesign=getGeometryDesign(tmpAimsunApproach);
                tmpAimsunApproach.geoDesign=geoDesign;

                // Get the detector information
                DetectorProperty detectorProperty=getDetectorProperty(tmpAimsunApproach,aimsunNetwork.aimsunDetectorList);
                tmpAimsunApproach.detectorProperty=detectorProperty;

                // Get default signal settings
                DefaultSignalSetting defaultSignalSetting=new DefaultSignalSetting(MainFunction.cBlock.CycleLength,
                        MainFunction.cBlock.LeftTurnGreen,MainFunction.cBlock.ThroughGreen,MainFunction.cBlock.RightTurnGreen,
                        MainFunction.cBlock.LeftTurnSetting);
                tmpAimsunApproach.defaultSignalSetting=defaultSignalSetting;

                // Get midlink configuration files: This is used to get midlink counts
                // Currently not doing this.

                // Get control plans for each approach
                tmpAimsunApproach.controlPlanJunction=ControlPlanBelongToJunction;

                // Add the tmpAimsunApproach to the end
                aimsunApproachList.add(tmpAimsunApproach);
            }
        }

        AimsunNetworkByApproach aimsunNetworkByApproach=new AimsunNetworkByApproach(aimsunNetwork,aimsunApproachList);
        System.out.println("Finish reconstructing the network!");
        return aimsunNetworkByApproach;
    }

    public static DetectorProperty getDetectorProperty(AimsunApproach tmpAimsunApproach,
                                                       List<readFromAimsun.AimsunDetector> aimsunDetectorList){
        // This is the function to get the detector properties

        List<Integer> ListOfSections=tmpAimsunApproach.sectionBelongToApproach.ListOfSections;
        List<readFromAimsun.AimsunSection> Property=tmpAimsunApproach.sectionBelongToApproach.Property;

        // Get the detectors belonging to the sections
        List<readFromAimsun.AimsunDetector> selectedDetectorList=new ArrayList<readFromAimsun.AimsunDetector>();
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
        List<String> movementAdvanced=trafficMovementLibrary("Advanced");
        List<String> movementGeneral=trafficMovementLibrary("General");

        // Initialize the movements belonging to different types
        List<DetectorMovementProperty> ExclusiveLeftTurn=new ArrayList<DetectorMovementProperty>();
        List<DetectorMovementProperty> ExclusiveRightTurn=new ArrayList<DetectorMovementProperty>();
        List<DetectorMovementProperty> AdvancedDetectors=new ArrayList<DetectorMovementProperty>();
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

        // Check for advanced detectors
        for(int i=0;i<movementAdvanced.size();i++) {
            DetectorMovementProperty detectorMovementProperty = getDetectorMovementProperty
                    ("Advanced",movementAdvanced.get(i), selectedDetectorList, Property);
            if(detectorMovementProperty!=null){
                AdvancedDetectors.add(detectorMovementProperty);
            }
        }

        DetectorProperty detectorProperty=new DetectorProperty(ExclusiveLeftTurn,ExclusiveRightTurn,
                AdvancedDetectors,GeneralStopbarDetectors);
        return detectorProperty;
    }

    public static DetectorMovementProperty getDetectorMovementProperty(String Type,
                                                                       String MovementInput,
                                                                       List<readFromAimsun.AimsunDetector> aimsunDetector,
                                                                       List<readFromAimsun.AimsunSection> aimsunSection){
        // This function is used to get the detector movement property
        DetectorMovementProperty detectorMovementProperty=null;

        List<Integer> DetectorIDs =new ArrayList<Integer>();
        List<Double> DetectorLengths =new ArrayList<Double>();
        List<Double> DistancesToStopbar =new ArrayList<Double>();
        List<Integer> NumberOfLanes =new ArrayList<Integer>();

        for(int i=0;i<aimsunDetector.size();i++){ // Loop for the selected detectors
            if(aimsunDetector.get(i).Movement.equals(MovementInput)){ // If it matches the movement input
                detectorMovementProperty=new DetectorMovementProperty(null,
                        null, null,null,null);
                // Add the detector ID,length, and number of lanes
                DetectorIDs.add(Integer.parseInt(aimsunDetector.get(i).ExternalID.trim()));
                DetectorLengths.add(aimsunDetector.get(i).Length);
                NumberOfLanes.add(aimsunDetector.get(i).NumOfLanes);

                if(Type.equals("Stopbar")){ // For stopbar detectors, distanceToStopbar=0
                    DistancesToStopbar.add(0.0);
                }else if(Type.equals("Advanced")){ // For advanced detectors, search
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
                        // If not found
                        else{
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

    public static List<String> trafficMovementLibrary(String Type){
        // This function returns the traffic movements belonging to a certain type
        List<String> possibleMovements=null;
        if(Type.equals("Left")){
            possibleMovements=new ArrayList(Arrays.asList("Left Turn","Left Turn Queue"));
        }else if(Type.equals("Right")){
            possibleMovements=new ArrayList(Arrays.asList("Right Turn","Right Turn Queue"));
        }else if(Type.equals("Advanced")){
            possibleMovements=new ArrayList(Arrays.asList("Advanced","Advanced Left Turn",
                    "Advanced Right Turn","Advanced Through","Advanced Through and Right","Advanced Left and Through",
                    "Advanced Left and Right"));
        }else if(Type.equals("General")){
            possibleMovements=new ArrayList(Arrays.asList("All Movements","Through","Left and Right",
                    "Left and Through","Through and Right"));
        }else{
            System.out.println("Unknown movement type!");
            System.exit(-1);
        }
        return possibleMovements;
    }

    public static GeoDesign getGeometryDesign(AimsunApproach tmpAimsunApproach) {
        // This function is used to get the geometry design

        SectionBelongToApproach sectionBelongToApproach = tmpAimsunApproach.sectionBelongToApproach;
        TurningBelongToApproach turningBelongToApproach = tmpAimsunApproach.turningBelongToApproach;
        List<LaneTurningProperty> laneTurningProperty=tmpAimsunApproach.laneTurningProperty;

        GeoDesign geoDesign = new GeoDesign(0, 0, 0,
                null, null);

        // Get the total link length
        double LinkLength = 0;
        for (int i = 0; i < tmpAimsunApproach.sectionBelongToApproach.ListOfSections.size(); i++) {
            double maxLength = 0;
            //Get the max length of a given link
            for (int j = 0; j < tmpAimsunApproach.sectionBelongToApproach.Property.get(i).LaneLengths.length; j++) {
                if (tmpAimsunApproach.sectionBelongToApproach.Property.get(i).LaneLengths[j] > maxLength) {
                    maxLength = tmpAimsunApproach.sectionBelongToApproach.Property.get(i).LaneLengths[j];
                }
            }
            LinkLength = LinkLength + maxLength;
        }
        geoDesign.LinkLength = LinkLength;

        // Get the number of upstream lanes (at the last section)
        // Only Check full lanes (it is possible upstream lane is not full lane)
        int NumOfUpstreamLanes = 0;
        for (int i = 0; i < sectionBelongToApproach.Property.get
                (sectionBelongToApproach.Property.size() - 1).IsFullLane.length; i++) {
            if (sectionBelongToApproach.Property.get
                    (sectionBelongToApproach.Property.size() - 1).IsFullLane[i] == 1) {
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
                            if(exclusiveTurningPropertyLeft.equals(null)){
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
                            if(exclusiveTurningPropertyRight.equals(null)){
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

        return geoDesign;
    }

    public static List<LaneTurningProperty> findLaneTurningProperty(AimsunApproach tmpAimsunApproach){
        // This function is used to find the list of lane-turning properties

        List<LaneTurningProperty> laneTurningPropertyList=new ArrayList<LaneTurningProperty>();

        SectionBelongToApproach sectionBelongToApproach=tmpAimsunApproach.sectionBelongToApproach;
        TurningBelongToApproach turningBelongToApproach=tmpAimsunApproach.turningBelongToApproach;

        for(int i=0;i<sectionBelongToApproach.ListOfSections.size();i++){
            LaneTurningProperty tmpLaneTurningProperty=new LaneTurningProperty(0, 0, null);
            // Loop for the list of sections
            tmpLaneTurningProperty.SectionID=sectionBelongToApproach.ListOfSections.get(i);
            tmpLaneTurningProperty.NumLanes=sectionBelongToApproach.Property.get(i).NumLanes;

            // Note: Lane ID=1:N from rightmost to leftmost
            //       Turn organized from leftmost to rightmost
            //       Each turn: fromLane (rightmost) to toLane (Leftmost)
            //       Lane Length: from leftmost to rightmost
            List<LaneProperty> lanePropertyList=new ArrayList<LaneProperty>();
            for(int j=tmpLaneTurningProperty.NumLanes;j>0;j--){
                LaneProperty tmpLaneProperty=new LaneProperty(0, 0, null,
                        null, 0);
                tmpLaneProperty.LaneID=j; // Get the lane ID
                tmpLaneProperty.Length=sectionBelongToApproach.Property.get(i).LaneLengths
                        [tmpLaneTurningProperty.NumLanes-j]; // Get the lane length: index starting from 0 in Java

                // Get the turns using the current lane
                List<Integer> TurningMovements =new ArrayList<Integer>();
                for(int k=0;k<turningBelongToApproach.TurningProperty.size();k++){
                    if(turningBelongToApproach.TurningProperty.get(k).OrigFromLane<=j &&
                            turningBelongToApproach.TurningProperty.get(k).OrigToLane>=j)
                        TurningMovements.add(turningBelongToApproach.TurningProperty.get(k).TurnID);
                }
                tmpLaneProperty.TurningMovements=TurningMovements;

                // Get whether it is exclusive or not
                int IsExclusive=1;
                if(TurningMovements.size()>1) {
                    IsExclusive = 0;
                }
                tmpLaneProperty.IsExclusive=IsExclusive;

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
                tmpLaneProperty.Proportions=Proportions;

                // Append the lane property
                lanePropertyList.add(tmpLaneProperty);
            }

            tmpLaneTurningProperty.Lanes=lanePropertyList;

            // Append the lane-turning property
            laneTurningPropertyList.add(tmpLaneTurningProperty);
        }
        return laneTurningPropertyList;
    }


    public static TurningBelongToApproach findTurningsAtFirstSection(readFromAimsun.AimsunJunction aimsunJunction,int FirstSectionID){
        // This function is used to find turnings belonging to the first section

        int [] ListOfTurns=null;
        List<readFromAimsun.AimsunTurning> aimsunTurningList=new ArrayList<readFromAimsun.AimsunTurning>();
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


    public static List<readFromAimsun.AimsunSection> getSectionProperties(List<readFromAimsun.AimsunJunction> aimsunJunctionList,
                                                                          List<readFromAimsun.AimsunSection> aimsunSectionList,
                                                                          List<Integer> ListOfSections){
        // This function is used to get/update the section information belonging to a given approach
        List<readFromAimsun.AimsunSection> aimsunSectionByApproachList=new ArrayList<readFromAimsun.AimsunSection>();

        for (int i=0;i<ListOfSections.size();i++){
            readFromAimsun.AimsunSection tmpAimsunSection=null;
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


    public static List<Integer>  findUpstreamSections(List<readFromAimsun.AimsunJunction> linearJunction, int SectionID){
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

    public static readFromAimsun.AimsunSection findSectionInformation(int SectionID,
                                                                      List<readFromAimsun.AimsunSection> aimsunSectionList){
        // This function is used to find the information for a given section
        readFromAimsun.AimsunSection aimsunSection=null;
        for(int i=0;i<aimsunSectionList.size();i++){
            if(aimsunSectionList.get(i).SectionID==SectionID){
                aimsunSection=aimsunSectionList.get(i);
            }
        }
        return aimsunSection;
    }

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

    public static List<readFromAimsun.AimsunControlPlanJunction> getControlPlanBelongToJunction
            (int JunctionID, List<readFromAimsun.AimsunControlPlanJunction> aimsunControlPlanJunction,
             List<readFromAimsun.AimsunMasterControlPlan> aimsunMasterControlPlans){
        // This function is to return the control plans belonging to the same junction ID

        List<readFromAimsun.AimsunControlPlanJunction> tmpAimsunControlPlanJunctionList=
                new ArrayList<readFromAimsun.AimsunControlPlanJunction>();
        List<Integer> PlanOffset=new ArrayList<Integer>();
        for (int i=0;i<aimsunControlPlanJunction.size();i++){ // Loop for each plan-junction
            if(aimsunControlPlanJunction.get(i).JunctionID==JunctionID){ // The same junction ID
                //Find the corresponding Master Control Plan
                readFromAimsun.AimsunControlPlanJunction tmpAimsunControlPlanJunction=getMasterControlPlanBelongToControlPlan
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
            List<readFromAimsun.AimsunControlPlanJunction> AimsunControlPlanJunctionList=
                    new ArrayList<readFromAimsun.AimsunControlPlanJunction>();
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

    public static readFromAimsun.AimsunControlPlanJunction getMasterControlPlanBelongToControlPlan
            (readFromAimsun.AimsunControlPlanJunction aimsunControlPlanJunction,
             List<readFromAimsun.AimsunMasterControlPlan> aimsunMasterControlPlans){
                // This function is used to get the corresponding master control plans belonging to the same control plan

        readFromAimsun.AimsunControlPlanJunction tmpAimsunControlPlanJunction=aimsunControlPlanJunction;
        for (int i=0;i<aimsunMasterControlPlans.size();i++){
            if(aimsunMasterControlPlans.get(i).ControlPlanID==tmpAimsunControlPlanJunction.PlanID){
                tmpAimsunControlPlanJunction.MasterControlPlan.add(aimsunMasterControlPlans.get(i));
            }
        }
        return tmpAimsunControlPlanJunction;
    }

    public static List<readFromAimsun.AimsunJunction> getLinearOrNonLinearJunction(
            List<readFromAimsun.AimsunJunction> aimsunJunctionList, String Type){
        // This function is used to get the linear (1-by-1) or non-linear(n-by-m) junction
        List<readFromAimsun.AimsunJunction> aimsunJunctionSelected=
                new ArrayList<readFromAimsun.AimsunJunction>();

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
