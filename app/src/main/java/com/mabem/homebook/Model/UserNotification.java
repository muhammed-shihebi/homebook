package com.mabem.homebook.Model;

public class UserNotification {

    // Types
    public static final String TYPE_ACCEPT = "accept";
    public static final String TYPE_DECLINE = "decline";
    public static final String TYPE_INVITATION = "invitation";


    private String homeName;
    private String typ;

    public UserNotification(String homeName, String typ) {
        this.homeName = homeName;
        this.typ = typ;
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
