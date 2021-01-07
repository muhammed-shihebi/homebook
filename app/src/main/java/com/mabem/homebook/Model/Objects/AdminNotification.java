package com.mabem.homebook.Model.Objects;

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

    public String getHomeId() {
        return homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getUserName() {
        return userName;
    }
}
