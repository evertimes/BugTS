package com.evertimes.bugts.model.dto;

import java.time.LocalDateTime;

public class Project {
    private int projectId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime projectStart;

    public Project(int projectId, String projectName, String projectDescription, LocalDateTime projectStart) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStart = projectStart;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public LocalDateTime getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(LocalDateTime projectStart) {
        this.projectStart = projectStart;
    }
}
