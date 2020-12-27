package com.mabem.homebook.Model;

public class AdminNotification {
    private String userEmail;
    private String homeId;
    private String homeName;
    private String userName;

    public AdminNotification(String userEmail, String homeId, String homeName, String userName) {
        this.userEmail = userEmail;
        this.homeId = homeId;
        this.homeName = homeName;
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
