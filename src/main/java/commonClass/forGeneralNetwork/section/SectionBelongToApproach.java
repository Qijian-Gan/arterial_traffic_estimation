package commonClass.forGeneralNetwork.section;

import commonClass.forAimsunNetwork.section.AimsunSection;

import java.util.List;

public class SectionBelongToApproach {
    // This is the profile for sections belonging to the same approach
    public SectionBelongToApproach(List<Integer> _ListOfSections, List<AimsunSection> _Property){
        this.ListOfSections=_ListOfSections;
        this.Property=_Property;
    }
    private List<Integer> ListOfSections; // The list of sections
    private List<AimsunSection> Property; // Section properties inherited from Aimsun

    // Get functions
    public List<Integer> getListOfSections() {
        return ListOfSections;
    }

    public List<AimsunSection> getProperty() {
        return Property;
    }

    // Set functions
    public void setListOfSections(List<Integer> listOfSections) {
        ListOfSections = listOfSections;
    }

    public void setProperty(List<AimsunSection> property) {
        Property = property;
    }


}
