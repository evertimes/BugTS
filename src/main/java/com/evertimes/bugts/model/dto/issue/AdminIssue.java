package com.evertimes.bugts.model.dto.issue;

import java.time.LocalDateTime;

public class AdminIssue {
    private int issueID;
    private String projectName;
    private String testerName;
    private String statusName;
    private String priorityName;
    private LocalDateTime dateRegistered;

    public AdminIssue(int issueID, String projectName, String testerName, String statusName, String priorityName, LocalDateTime dateRegistered) {
        this.issueID = issueID;
        this.projectName = projectName;
        this.testerName = testerName;
        this.statusName = statusName;
        this.priorityName = priorityName;
        this.dateRegistered = dateRegistered;
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

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
    }
}
