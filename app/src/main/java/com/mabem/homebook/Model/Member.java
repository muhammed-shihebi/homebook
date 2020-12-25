package com.mabem.homebook.Model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class Member extends User {

    public static final Boolean ADMIN_ROLE = true;
    public static final Boolean MEMBER_ROLE = false;

    private HashMap<Home, Boolean> home_role = new HashMap<Home, Boolean>(); //Role: False -> Normal Member, True -> Admin
    private ArrayList<AdminNotification> adminNotifications = new ArrayList<>();
    private ArrayList<UserNotification> userNotifications = new ArrayList<>();

    public Member(String id, String name, String emailAddress, String password, Uri imageURI, HashMap<Home, Boolean> home_role, ArrayList<AdminNotification> adminNotifications, ArrayList<UserNotification> userNotifications) {
        super(id, name, emailAddress, password, imageURI);
        this.home_role = home_role;
        this.adminNotifications = adminNotifications;
        this.userNotifications = userNotifications;
    }

    public Member(String id, String name, String emailAddress, Uri imageURI, HashMap<Home, Boolean> home_role) {
        super(id, name, emailAddress, imageURI);
        this.home_role = home_role;
    }

    public Member(User user,  HashMap<Home, Boolean> home_role, ArrayList<AdminNotification> adminNotifications, ArrayList<UserNotification> userNotifications){
        super(user.getId(), user.getName(), user.getEmailAddress(), user.getImageURI());
        this.home_role = home_role;
        this.adminNotifications = adminNotifications;
        this.userNotifications = userNotifications;
    }

    public Member(String name, String id){
        super(name, id);
    }

    public HashMap<Home, Boolean> getHome_role() {
        return home_role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member m = (Member) o;
        return (this.getId().equals(m.getId()) && getHome_role().equals(m.getHome_role()));
    }

    public void setHomeRole(HashMap<Home, Boolean> home_role) {
        this.home_role = home_role;
    }

    public void setAdminNotifications(ArrayList<AdminNotification> adminNotifications) {
        this.adminNotifications = adminNotifications;
    }

    public void setUserNotifications(ArrayList<UserNotification> userNotifications) {
        this.userNotifications = userNotifications;
    }


    public Boolean isThisMemberAdmin(Home currentHome) {
        for(Home h:  home_role.keySet()){
            if(h.getId().equals(currentHome.getId()) && home_role.get(h)){
                return true;
            }
        }
        return false;
    }


}
