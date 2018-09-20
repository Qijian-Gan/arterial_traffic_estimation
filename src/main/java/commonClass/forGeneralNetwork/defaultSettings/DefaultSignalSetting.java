package commonClass.forGeneralNetwork.defaultSettings;

public class DefaultSignalSetting {
    // This is the profile for default signal settings
    public DefaultSignalSetting(int _CycleLength, int _LeftTurnGreen, int _ThroughGreen,
                                int _RightTurnGreen, String _LeftTurnSetting){
        this.CycleLength=_CycleLength;
        this.LeftTurnGreen=_LeftTurnGreen;
        this.ThroughGreen=_ThroughGreen;
        this.RightTurnGreen=_RightTurnGreen;
        this.LeftTurnSetting=_LeftTurnSetting;
    }
    private int CycleLength;
    private int LeftTurnGreen; // Green time for left-turn
    private int ThroughGreen; // Green time for through
    private int RightTurnGreen; // Green time for right-turn
    private String LeftTurnSetting; // Protected, Permitted, Protected-Permitted

    // Get functions
    public int getCycleLength() {
        return CycleLength;
    }

    public int getLeftTurnGreen() {
        return LeftTurnGreen;
    }

    public int getThroughGreen() {
        return ThroughGreen;
    }

    public int getRightTurnGreen() {
        return RightTurnGreen;
    }

    public String getLeftTurnSetting() {
        return LeftTurnSetting;
    }

    // Set functions
    public void setCycleLength(int cycleLength) {
        CycleLength = cycleLength;
    }

    public void setLeftTurnGreen(int leftTurnGreen) {
        LeftTurnGreen = leftTurnGreen;
    }

    public void setThroughGreen(int throughGreen) {
        ThroughGreen = throughGreen;
    }

    public void setRightTurnGreen(int rightTurnGreen) {
        RightTurnGreen = rightTurnGreen;
    }

    public void setLeftTurnSetting(String leftTurnSetting) {
        LeftTurnSetting = leftTurnSetting;
    }
}


