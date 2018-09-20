package commonClass.forGeneralNetwork;

import commonClass.forAimsunNetwork.*;
import commonClass.forGeneralNetwork.approach.*;
import java.util.List;

public class AimsunNetworkByApproach {
    // This is the profile for Aimsun network by Approach
    public AimsunNetworkByApproach(AimsunNetwork _aimsunNetwork,List<AimsunApproach> _aimsunNetworkByApproach){
        this.aimsunNetwork=_aimsunNetwork;
        this.aimsunNetworkByApproach=_aimsunNetworkByApproach;
    }

    private AimsunNetwork aimsunNetwork; // Aimsun network
    private List<AimsunApproach> aimsunNetworkByApproach; // Reconstructed Aimsun network at the approach level

    // Get functions
    public AimsunNetwork getAimsunNetwork() {
        return aimsunNetwork;
    }

    public List<AimsunApproach> getAimsunNetworkByApproach() {
        return aimsunNetworkByApproach;
    }

    // Set functions
    public void setAimsunNetwork(AimsunNetwork aimsunNetwork) {
        this.aimsunNetwork = aimsunNetwork;
    }

    public void setAimsunNetworkByApproach(List<AimsunApproach> aimsunNetworkByApproach) {
        this.aimsunNetworkByApproach = aimsunNetworkByApproach;
    }
}
