package commonClass.forGeneralNetwork.approach;

import commonClass.forGeneralNetwork.detector.*;
import commonClass.forGeneralNetwork.geometry.*;
import commonClass.forGeneralNetwork.section.*;
import commonClass.forGeneralNetwork.lane.*;
import commonClass.forGeneralNetwork.midlink.*;
import commonClass.forGeneralNetwork.turning.*;
import commonClass.forGeneralNetwork.defaultSettings.*;
import commonClass.forAimsunNetwork.signalControl.*;
import commonClass.forAimsunNetwork.junction.*;
import java.util.List;

public class AimsunApproach {
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
    private int JunctionID;
    private String JunctionName;
    private String JunctionExtID;
    private String City;
    private String County;
    private int Signalized;
    private int FirstSectionID;
    private String FirstSectionName;
    private String FirstSectionExtID;
    private SectionBelongToApproach sectionBelongToApproach; // Sections belonging to the approach
    private TurningBelongToApproach turningBelongToApproach; // Turnings belonging to the approach
    private List<LaneTurningProperty> laneTurningProperty;
    private GeoDesign geoDesign; // Geometry information
    private DetectorProperty detectorProperty;
    private DefaultSignalSetting defaultSignalSetting;
    private MidlinkCountConfig midlinkCountConfig; // It may be possible to get some information from midlink counts
    private List<AimsunControlPlanJunction> controlPlanJunction; // Control plans associated with the junction
    // Aimsun junction information: include this metric just in case we need more information in the future
    protected AimsunJunction aimsunJunction;

    // Get functions
    public int getJunctionID() {
        return JunctionID;
    }

    public String getJunctionName() {
        return JunctionName;
    }

    public String getJunctionExtID() {
        return JunctionExtID;
    }

    public String getCity() {
        return City;
    }

    public String getCounty() {
        return County;
    }

    public int getSignalized() {
        return Signalized;
    }

    public int getFirstSectionID() {
        return FirstSectionID;
    }

    public String getFirstSectionExtID() {
        return FirstSectionExtID;
    }

    public String getFirstSectionName() {
        return FirstSectionName;
    }

    public SectionBelongToApproach getSectionBelongToApproach() {
        return sectionBelongToApproach;
    }

    public TurningBelongToApproach getTurningBelongToApproach() {
        return turningBelongToApproach;
    }

    public List<LaneTurningProperty> getLaneTurningProperty() {
        return laneTurningProperty;
    }

    public GeoDesign getGeoDesign() {
        return geoDesign;
    }

    public DetectorProperty getDetectorProperty() {
        return detectorProperty;
    }

    public DefaultSignalSetting getDefaultSignalSetting() {
        return defaultSignalSetting;
    }

    public MidlinkCountConfig getMidlinkCountConfig() {
        return midlinkCountConfig;
    }

    public List<AimsunControlPlanJunction> getControlPlanJunction() {
        return controlPlanJunction;
    }

    public AimsunJunction getAimsunJunction() {
        return aimsunJunction;
    }

    // Set functions
    public void setJunctionID(int junctionID) {
        JunctionID = junctionID;
    }

    public void setJunctionName(String junctionName) {
        JunctionName = junctionName;
    }

    public void setJunctionExtID(String junctionExtID) {
        JunctionExtID = junctionExtID;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setCounty(String county) {
        County = county;
    }

    public void setSignalized(int signalized) {
        Signalized = signalized;
    }

    public void setFirstSectionID(int firstSectionID) {
        FirstSectionID = firstSectionID;
    }

    public void setFirstSectionExtID(String firstSectionExtID) {
        FirstSectionExtID = firstSectionExtID;
    }

    public void setFirstSectionName(String firstSectionName) {
        FirstSectionName = firstSectionName;
    }

    public void setSectionBelongToApproach(SectionBelongToApproach sectionBelongToApproach) {
        this.sectionBelongToApproach = sectionBelongToApproach;
    }

    public void setTurningBelongToApproach(TurningBelongToApproach turningBelongToApproach) {
        this.turningBelongToApproach = turningBelongToApproach;
    }

    public void setLaneTurningProperty(List<LaneTurningProperty> laneTurningProperty) {
        this.laneTurningProperty = laneTurningProperty;
    }

    public void setGeoDesign(GeoDesign geoDesign) {
        this.geoDesign = geoDesign;
    }

    public void setDetectorProperty(DetectorProperty detectorProperty) {
        this.detectorProperty = detectorProperty;
    }

    public void setDefaultSignalSetting(DefaultSignalSetting defaultSignalSetting) {
        this.defaultSignalSetting = defaultSignalSetting;
    }

    public void setMidlinkCountConfig(MidlinkCountConfig midlinkCountConfig) {
        this.midlinkCountConfig = midlinkCountConfig;
    }

    public void setControlPlanJunction(List<AimsunControlPlanJunction> controlPlanJunction) {
        this.controlPlanJunction = controlPlanJunction;
    }

    public void setAimsunJunction(AimsunJunction aimsunJunction) {
        this.aimsunJunction = aimsunJunction;
    }
}
