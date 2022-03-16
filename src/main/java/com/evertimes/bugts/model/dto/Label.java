package com.evertimes.bugts.model.dto;

public class Label {
    private int labelID;
    private String labelName;
    private String labelDescription;

    public Label(int labelID, String labelName, String labelDescription) {
        this.labelID = labelID;
        this.labelName = labelName;
        this.labelDescription = labelDescription;
    }

    public int getLabelID() {
        return labelID;
    }

    public void setLabelID(int labelID) {
        this.labelID = labelID;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelDescription() {
        return labelDescription;
    }

    public void setLabelDescription(String labelDescription) {
        this.labelDescription = labelDescription;
    }
}
