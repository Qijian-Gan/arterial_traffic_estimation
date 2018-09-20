package commonClass.forGeneralNetwork.turning;

import java.util.List;
import commonClass.forAimsunNetwork.junction.*;

public class TurningBelongToApproach {
    // This is the profile for turnings belonging to the same approach (the first/downstream section)
    public TurningBelongToApproach(int [] _TurningAtFirstSectionFromLeftToRight, List<AimsunTurning> _TurningProperty){
        this.TurningAtFirstSectionFromLeftToRight=_TurningAtFirstSectionFromLeftToRight;
        this.TurningProperty=_TurningProperty;
    }
    private int [] TurningAtFirstSectionFromLeftToRight; // List of turnings at the first section
    private List<AimsunTurning> TurningProperty; // Turning property inherited from Aimsun

    // Get functions
    public int[] getTurningAtFirstSectionFromLeftToRight() {
        return TurningAtFirstSectionFromLeftToRight;
    }

    public List<AimsunTurning> getTurningProperty() {
        return TurningProperty;
    }

    // Set functions
    public void setTurningAtFirstSectionFromLeftToRight(int[] turningAtFirstSectionFromLeftToRight) {
        TurningAtFirstSectionFromLeftToRight = turningAtFirstSectionFromLeftToRight;
    }

    public void setTurningProperty(List<AimsunTurning> turningProperty) {
        TurningProperty = turningProperty;
    }
}
