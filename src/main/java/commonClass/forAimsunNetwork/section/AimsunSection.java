package commonClass.forAimsunNetwork.section;

import commonClass.forAimsunNetwork.junction.*;

public class AimsunSection {
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
    private int SectionID;
    private String SectionName;
    private String SectionExtID;
    private int NumLanes; // Number of lanes
    private int NumPoints; //Number of shape points
    private double[] LaneLengths; // Lane Lengths
    private double[] InitialPoints; // Initial starting points
    private int [] IsFullLane; // Is a full lane or not
    private double[][] ShapePoints; // Shape points
    private AimsunJunction DownstreamJunction=null; // This is used to reconstruct the network
    private AimsunJunction UpstreamJunction=null; // This is used to reconstruct the network

    // Get functions
    public int getSectionID() {
        return SectionID;
    }

    public String getSectionName() {
        return SectionName;
    }

    public String getSectionExtID() {
        return SectionExtID;
    }

    public int getNumLanes() {
        return NumLanes;
    }

    public int getNumPoints() {
        return NumPoints;
    }

    public double[] getLaneLengths() {
        return LaneLengths;
    }

    public double[] getInitialPoints() {
        return InitialPoints;
    }

    public int[] getIsFullLane() {
        return IsFullLane;
    }

    public double[][] getShapePoints() {
        return ShapePoints;
    }

    // Set functions
    public void setSectionID(int sectionID) {
        SectionID = sectionID;
    }

    public void setSectionName(String sectionName) {
        SectionName = sectionName;
    }

    public void setSectionExtID(String sectionExtID) {
        SectionExtID = sectionExtID;
    }

    public void setNumLanes(int numLanes) {
        NumLanes = numLanes;
    }

    public void setNumPoints(int numPoints) {
        NumPoints = numPoints;
    }

    public void setLaneLengths(double[] laneLengths) {
        LaneLengths = laneLengths;
    }

    public void setInitialPoints(double[] initialPoints) {
        InitialPoints = initialPoints;
    }

    public void setIsFullLane(int[] isFullLane) {
        IsFullLane = isFullLane;
    }

    public void setShapePoints(double[][] shapePoints) {
        ShapePoints = shapePoints;
    }

    public void setUpstreamJunction(AimsunJunction upstreamJunction) {
        UpstreamJunction = upstreamJunction;
    }

    public void setDownstreamJunction(AimsunJunction downstreamJunction) {
        DownstreamJunction = downstreamJunction;
    }
}
