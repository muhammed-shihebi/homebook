package com.mabem.homebook.Model;

import java.util.HashMap;

public class Member extends User {
    private HashMap<Home, Boolean> home_role = new HashMap<Home, Boolean>(); //Role: False -> Normal Member, True -> Admin


    public Member(String id, String name, String emailAddress, String password, String imageURL, HashMap<Home, Boolean> home_role) {
        super(id, name, emailAddress, password, imageURL);
        this.home_role = home_role;
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
