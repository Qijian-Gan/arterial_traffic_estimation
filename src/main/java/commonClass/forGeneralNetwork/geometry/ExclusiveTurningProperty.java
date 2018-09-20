package commonClass.forGeneralNetwork.geometry;

public class ExclusiveTurningProperty {
    // This is the profile for exclusive turning property
    public ExclusiveTurningProperty(int _NumLanes, double _Pocket ){
        this.NumLanes=_NumLanes;
        this.Pocket=_Pocket;
    }
    private int NumLanes; // Number of exclusive lanes
    private double Pocket; // Length of the turning pocket

    // Get functions
    public int getNumLanes() {
        return NumLanes;
    }

    public double getPocket() {
        return Pocket;
    }

    // Get functions
    public void setNumLanes(int numLanes) {
        NumLanes = numLanes;
    }

    public void setPocket(double pocket) {
        Pocket = pocket;
    }
}
