package commonClass.bluetoothData;

public class BluetoothTravelTime {

    private String BluetoothID;
    private String LocationA;
    private String LocationB;
    private int DateAtA;
    private int TimeAtA;
    private int DateAtB;
    private int TimeAtB;
    private int TravelTime;
    private int UnknownC1;
    private String ValidOrNot;
    private int UnknownC2;

    public BluetoothTravelTime(String _BluetoothID, String _LocationA, String _LocationB, int _DateAtA, int _TimeAtA, int _DateAtB,
                               int _TimeAtB, int _TravelTime, int _UnknownC1, String _ValidOrNot, int _UnknownC2){
        this.BluetoothID=_BluetoothID;
        this.LocationA=_LocationA;
        this.LocationB=_LocationB;
        this.DateAtA=_DateAtA;
        this.TimeAtA=_TimeAtA;
        this.DateAtB=_DateAtB;
        this.TimeAtB=_TimeAtB;
        this.TravelTime=_TravelTime;
        this.UnknownC1=_UnknownC1;
        this.ValidOrNot=_ValidOrNot;
        this.UnknownC2=_UnknownC2;
    }

    // Get functions
    public String getBluetoothID() {
        return BluetoothID;
    }

    public String getLocationA() {
        return LocationA;
    }

    public String getLocationB() {
        return LocationB;
    }

    public int getDateAtA() {
        return DateAtA;
    }

    public int getTimeAtA() {
        return TimeAtA;
    }

    public int getDateAtB() {
        return DateAtB;
    }

    public int getTimeAtB() {
        return TimeAtB;
    }

    public int getTravelTime() {
        return TravelTime;
    }

    public int getUnknownC1() {
        return UnknownC1;
    }

    public String getValidOrNot() {
        return ValidOrNot;
    }

    public int getUnknownC2() {
        return UnknownC2;
    }

    // Set functions
    public void setBluetoothID(String bluetoothID) {
        BluetoothID = bluetoothID;
    }

    public void setLocationA(String locationA) {
        LocationA = locationA;
    }

    public void setLocationB(String locationB) {
        LocationB = locationB;
    }

    public void setDateAtA(int dateAtA) {
        DateAtA = dateAtA;
    }

    public void setTimeAtA(int timeAtA) {
        TimeAtA = timeAtA;
    }

    public void setDateAtB(int dateAtB) {
        DateAtB = dateAtB;
    }

    public void setTimeAtB(int timeAtB) {
        TimeAtB = timeAtB;
    }

    public void setTravelTime(int travelTime) {
        TravelTime = travelTime;
    }

    public void setUnknownC1(int unknownC1) {
        UnknownC1 = unknownC1;
    }

    public void setValidOrNot(String validOrNot) {
        ValidOrNot = validOrNot;
    }

    public void setUnknownC2(int unknownC2) {
        UnknownC2 = unknownC2;
    }
}
