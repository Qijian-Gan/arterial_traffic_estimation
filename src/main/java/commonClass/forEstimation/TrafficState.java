package commonClass.forEstimation;

import commonClass.forGeneralNetwork.approach.AimsunApproach;
import commonClass.parameters.Parameters;
import commonClass.query.QueryMeasures;

public class TrafficState {
    // Traffic states with complete information
    public TrafficState(AimsunApproach _aimsunApproach, Parameters _parameters, QueryMeasures _queryMeasures
            , TrafficStateByApproach _trafficStateByApproach){
        this.aimsunApproach=_aimsunApproach;
        this.parameters=_parameters;
        this.queryMeasures=_queryMeasures;
        this.trafficStateByApproach=_trafficStateByApproach;
    }
    private AimsunApproach aimsunApproach;
    private Parameters parameters;
    private QueryMeasures queryMeasures;
    private TrafficStateByApproach trafficStateByApproach;

    // Get functions
    public AimsunApproach getAimsunApproach() {
        return aimsunApproach;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public QueryMeasures getQueryMeasures() {
        return queryMeasures;
    }

    public TrafficStateByApproach getTrafficStateByApproach() {
        return trafficStateByApproach;
    }

    // Set functions
    public void setAimsunApproach(AimsunApproach aimsunApproach) {
        this.aimsunApproach = aimsunApproach;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setQueryMeasures(QueryMeasures queryMeasures) {
        this.queryMeasures = queryMeasures;
    }

    public void setTrafficStateByApproach(TrafficStateByApproach trafficStateByApproach) {
        this.trafficStateByApproach = trafficStateByApproach;
    }
}
