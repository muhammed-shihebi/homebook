package com.mabem.homebook.Model;

public class AdminNotification {
    private String userId;
    private String homeId;
    private String homeName;
    private String userName;

    public AdminNotification(String userId, String homeId, String homeName, String userName) {
        this.userId = userId;
        this.homeId = homeId;
        this.homeName = homeName;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
