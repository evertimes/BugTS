package com.evertimes.bugts.model.dto;

public class Status {
    private int statusID;
    private String statusName;

    public Status(int statusID, String statusName) {
        this.statusID = statusID;
        this.statusName = statusName;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return statusName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        return statusName != null ? statusName.equals(status.statusName) : status.statusName == null;
    }

    @Override
    public int hashCode() {
        return statusName != null ? statusName.hashCode() : 0;
    }
}
