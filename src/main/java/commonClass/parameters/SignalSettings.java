package commonClass.parameters;

public class SignalSettings {
    // Signal settings
    public SignalSettings(int _CycleLength,int _LeftTurnGreen,int _ThroughGreen,int _RightTurnGreen,String _LeftTurnSetting){
        this.CycleLength=_CycleLength;
        this.LeftTurnGreen=_LeftTurnGreen;
        this.ThroughGreen=_ThroughGreen;
        this.RightTurnGreen=_RightTurnGreen;
        this.LeftTurnSetting=_LeftTurnSetting;
    }
    private int CycleLength;
    private int LeftTurnGreen;
    private int ThroughGreen;
    private int RightTurnGreen;
    private String LeftTurnSetting;

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
