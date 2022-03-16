package com.evertimes.bugts.model.dto.issue;

import java.time.LocalDateTime;

public class DeveloperIssue {
    private int issueID;
    private String projectName;
    private String testerName;
    private String statusName;
    private String priorityName;
    private LocalDateTime dateAssigned;

    public DeveloperIssue(int issueID, String projectName, String testerName, String statusName, String priorityName, LocalDateTime dateAssigned) {
        this.issueID = issueID;
        this.projectName = projectName;
        this.testerName = testerName;
        this.statusName = statusName;
        this.priorityName = priorityName;
        this.dateAssigned = dateAssigned;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTesterName() {
        return testerName;
    }

    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public LocalDateTime getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    @Override
    public String toString() {
        return "DeveloperIssue{" +
                "issueID=" + issueID +
                ", projectName='" + projectName + '\'' +
                ", testerName='" + testerName + '\'' +
                ", statusName='" + statusName + '\'' +
                ", priorityName='" + priorityName + '\'' +
                ", dateAssigned=" + dateAssigned +
                '}';
    }
}
