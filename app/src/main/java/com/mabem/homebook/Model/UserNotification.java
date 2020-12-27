package com.mabem.homebook.Model;

public class UserNotification {

    // Types
    public static final String TYPE_ACCEPT = "accept";
    public static final String TYPE_DECLINE = "decline";


    private String homeName;
    private String typ;
    private String homeId;

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public UserNotification(String homeName, String typ, String homeId) {
        this.homeName = homeName;
        this.typ = typ;
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
}
