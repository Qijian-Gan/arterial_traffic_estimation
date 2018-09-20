package commonClass.forGeneralNetwork.geometry;

public class GeoDesign {
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
    private double LinkLength;
    private int NumOfUpstreamLanes; // Number of upstream lanes
    private int NumOfDownstreamLanes; // Number of downstream lanes
    private ExclusiveTurningProperty ExclusiveLeftTurn; // Left-turn pockets
    private ExclusiveTurningProperty ExclusiveRightTurn; // Right-turn pockets
    private int [] TurnIndicator;

    // Get functions
    public double getLinkLength() {
        return LinkLength;
    }

    public int getNumOfUpstreamLanes() {
        return NumOfUpstreamLanes;
    }

    public int getNumOfDownstreamLanes() {
        return NumOfDownstreamLanes;
    }

    public ExclusiveTurningProperty getExclusiveLeftTurn() {
        return ExclusiveLeftTurn;
    }

    public ExclusiveTurningProperty getExclusiveRightTurn() {
        return ExclusiveRightTurn;
    }

    public int[] getTurnIndicator() {
        return TurnIndicator;
    }

    // Set functions
    public void setLinkLength(double linkLength) {
        LinkLength = linkLength;
    }

    public void setNumOfUpstreamLanes(int numOfUpstreamLanes) {
        NumOfUpstreamLanes = numOfUpstreamLanes;
    }

    public void setNumOfDownstreamLanes(int numOfDownstreamLanes) {
        NumOfDownstreamLanes = numOfDownstreamLanes;
    }

    public void setExclusiveLeftTurn(ExclusiveTurningProperty exclusiveLeftTurn) {
        ExclusiveLeftTurn = exclusiveLeftTurn;
    }

    public void setExclusiveRightTurn(ExclusiveTurningProperty exclusiveRightTurn) {
        ExclusiveRightTurn = exclusiveRightTurn;
    }

    public void setTurnIndicator(int[] turnIndicator) {
        TurnIndicator = turnIndicator;
    }
}
