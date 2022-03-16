package com.evertimes.bugts.model.dto.issue;

import java.time.LocalDateTime;

public class TesterIssue {
    private int issueID;
    private String projectName;
    private String statusName;
    private String priorityName;
    private LocalDateTime dateCreated;

    public TesterIssue(int issueID, String projectName, String statusName, String priorityName, LocalDateTime dateCreated) {
        this.issueID = issueID;
        this.projectName = projectName;
        this.statusName = statusName;
        this.priorityName = priorityName;
        this.dateCreated = dateCreated;
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
