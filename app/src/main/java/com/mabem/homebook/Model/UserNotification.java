package com.mabem.homebook.Model;

public class UserNotification {

    // Types
    public static final Boolean REQUEST_REJECTED = false;
    public static final Boolean REQUEST_ACCEPTED = true;


    private String homeName;
    private Boolean typ;

    public UserNotification(String homeName, Boolean typ) {
        this.homeName = homeName;
        this.typ = typ;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public Boolean getTyp() {
        return typ;
    }

    public void setTyp(Boolean typ) {
        this.typ = typ;
    }
}
