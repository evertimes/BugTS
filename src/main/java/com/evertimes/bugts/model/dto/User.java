package com.evertimes.bugts.model.dto;

public class User {
    private int userId;
    private String fullName;
    private String phone;
    private String homeAddress;
    private String role;

    public User(int userId, String fullName, String phone, String homeAddress, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.homeAddress = homeAddress;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
