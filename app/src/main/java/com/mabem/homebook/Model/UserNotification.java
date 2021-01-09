package com.mabem.homebook.Model;

public class UserNotification {

    // Types
    public static final String TYPE_ACCEPT = "accept";
    public static final String TYPE_DECLINE = "decline";


    private String homeName;
    private String type;
    private String homeId;

    public UserNotification(String homeName, String type, String homeId) {
        this.homeName = homeName;
        this.type = type;
        this.homeId = homeId;
    }

    public String getHomeId() {
        return homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getType() {
        return type;
    }
}