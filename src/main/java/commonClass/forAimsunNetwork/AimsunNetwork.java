package commonClass.forAimsunNetwork;

import commonClass.forAimsunNetwork.junction.*;
import commonClass.forAimsunNetwork.section.*;
import commonClass.forAimsunNetwork.detector.*;
import commonClass.forAimsunNetwork.signalControl.*;
import java.util.List;

public class AimsunNetwork {
    // This is the profile for Aimsun network
    // It consists of five components: control plans, junctions, sections, detectors, and master control plans
    public AimsunNetwork(List<AimsunControlPlanJunction> _aimsunControlPlanJunctionList,
                         List<AimsunJunction> _aimsunJunctionList,
                         List<AimsunSection> _aimsunSectionList,
                         List<AimsunDetector> _aimsunDetectorList,
                         List<AimsunMasterControlPlan> _aimsunMasterControlPlanList){
        this.aimsunControlPlanJunctionList=_aimsunControlPlanJunctionList;
        this.aimsunJunctionList=_aimsunJunctionList;
        this.aimsunSectionList=_aimsunSectionList;
        this.aimsunDetectorList=_aimsunDetectorList;
        this.aimsunMasterControlPlanList=_aimsunMasterControlPlanList;
    }
    private List<AimsunControlPlanJunction> aimsunControlPlanJunctionList; // Control plan information
    private List<AimsunJunction> aimsunJunctionList; // Junction information
    private List<AimsunSection> aimsunSectionList; // Section information
    private List<AimsunDetector> aimsunDetectorList; // Detector information
    private List<AimsunMasterControlPlan> aimsunMasterControlPlanList; // Master control plan

    // Get functions
    public List<AimsunControlPlanJunction> getAimsunControlPlanJunctionList() {
        return aimsunControlPlanJunctionList;
    }

    public List<AimsunJunction> getAimsunJunctionList() {
        return aimsunJunctionList;
    }

    public List<AimsunSection> getAimsunSectionList() {
        return aimsunSectionList;
    }

    public List<AimsunDetector> getAimsunDetectorList() {
        return aimsunDetectorList;
    }

    public List<AimsunMasterControlPlan> getAimsunMasterControlPlanList() {
        return aimsunMasterControlPlanList;
    }

    // Set functions
    public void setAimsunControlPlanJunctionList(List<AimsunControlPlanJunction> aimsunControlPlanJunctionList) {
        this.aimsunControlPlanJunctionList = aimsunControlPlanJunctionList;
    }

    public void setAimsunJunctionList(List<AimsunJunction> aimsunJunctionList) {
        this.aimsunJunctionList = aimsunJunctionList;
    }

    public void setAimsunSectionList(List<AimsunSection> aimsunSectionList) {
        this.aimsunSectionList = aimsunSectionList;
    }

    public void setAimsunDetectorList(List<AimsunDetector> aimsunDetectorList) {
        this.aimsunDetectorList = aimsunDetectorList;
    }

    public void setAimsunMasterControlPlanList(List<AimsunMasterControlPlan> aimsunMasterControlPlanList) {
        this.aimsunMasterControlPlanList = aimsunMasterControlPlanList;
    }
}
