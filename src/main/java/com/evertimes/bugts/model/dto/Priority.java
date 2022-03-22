package com.evertimes.bugts.model.dto;

public class Priority {
    private int priorityID;
    private String priorityName;

    public Priority(int priorityID, String priorityName) {
        this.priorityID = priorityID;
        this.priorityName = priorityName;
    }

    public int getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(int priorityID) {
        this.priorityID = priorityID;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    @Override
    public String toString() {
        return priorityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Priority priority = (Priority) o;

        return priorityName != null ? priorityName.equals(priority.priorityName) : priority.priorityName == null;
    }

    @Override
    public int hashCode() {
        return priorityName != null ? priorityName.hashCode() : 0;
    }
}
