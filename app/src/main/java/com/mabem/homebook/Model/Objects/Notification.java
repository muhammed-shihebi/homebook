package com.mabem.homebook.Model.Objects;

import java.util.ArrayList;

public class Notification {
    private ArrayList<AdminNotification> adminNotifications = new ArrayList<>();
    private ArrayList<UserNotification> userNotifications = new ArrayList<>();

    public Notification(ArrayList<AdminNotification> adminNotifications, ArrayList<UserNotification> userNotifications) {
        this.adminNotifications = adminNotifications;
        this.userNotifications = userNotifications;
    }

    public Notification() {
    }

    public ArrayList<AdminNotification> getAdminNotifications() {
        return adminNotifications;
    }

    public void setAdminNotifications(ArrayList<AdminNotification> adminNotifications) {
        this.adminNotifications = adminNotifications;
    }

    public ArrayList<UserNotification> getUserNotifications() {
        return userNotifications;
    }

    public void setUserNotifications(ArrayList<UserNotification> userNotifications) {
        this.userNotifications = userNotifications;
    }
}
