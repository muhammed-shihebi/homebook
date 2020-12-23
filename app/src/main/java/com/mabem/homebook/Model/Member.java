package com.mabem.homebook.Model;

import android.net.Uri;

import java.util.HashMap;

public class Member extends User {
    private HashMap<Home, Boolean> home_role = new HashMap<Home, Boolean>(); //Role: False -> Normal Member, True -> Admin


    public Member(String id, String name, String emailAddress, String imageURI, HashMap<Home, Boolean> home_role) {
        super(id, name, emailAddress, imageURI);
        this.home_role = home_role;
    }

    public Member(User user,  HashMap<Home, Boolean> home_role){
        super(user.getId(), user.getName(), user.getEmailAddress(), user.getImageURI());
        this.home_role = home_role;
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
}
