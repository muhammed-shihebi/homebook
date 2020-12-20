package com.mabem.homebook.Model;

public class Notification {
    private String homeName;
    private String userName;
    private String url;

    public Notification(String homeName, String userName, String url) {
        this.homeName = homeName;
        this.userName = userName;
        this.url = url;
    }

    public Notification(String homeName, String userName) {
        this.homeName = homeName;
        this.userName = userName;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
