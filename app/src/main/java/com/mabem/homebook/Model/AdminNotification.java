package com.mabem.homebook.Model;

public class AdminNotification {
    private String UserId;
    private String HomeId;
    private String HomeName;
    private String UserName;

    public AdminNotification(String userId, String homeId, String homeName, String userName) {
        UserId = userId;
        HomeId = homeId;
        HomeName = homeName;
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getHomeId() {
        return HomeId;
    }

    public void setHomeId(String homeId) {
        HomeId = homeId;
    }

    public String getHomeName() {
        return HomeName;
    }

    public void setHomeName(String homeName) {
        HomeName = homeName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
