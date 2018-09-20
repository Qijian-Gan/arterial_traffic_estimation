package commonClass.forGeneralNetwork.midlink;

public class MidlinkCountConfig {
    // This part is currently not used in our estimation model
    // But information can be used to improve the calculation of turning proportions at the approach
    public MidlinkCountConfig(String _Location, String _Approach){
        this.Location=_Location;
        this.Approach=_Approach;
    }
    private String Location; // Location of the files that stores the mid-link counts
    private String Approach; // Which approach the mid-link counts belong to

    // Get functions
    public String getLocation() {
        return Location;
    }

    public String getApproach() {
        return Approach;
    }

    // Set function
    public void setLocation(String location) {
        Location = location;
    }

    public void setApproach(String approach) {
        Approach = approach;
    }
}
