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

    @Override
    public String toString() {
        return labelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (labelID != label.labelID) return false;
        if (labelName != null ? !labelName.equals(label.labelName) : label.labelName != null) return false;
        return labelDescription != null ? labelDescription.equals(label.labelDescription) : label.labelDescription == null;
    }

    @Override
    public int hashCode() {
        int result = labelID;
        result = 31 * result + (labelName != null ? labelName.hashCode() : 0);
        result = 31 * result + (labelDescription != null ? labelDescription.hashCode() : 0);
        return result;
    }
}
