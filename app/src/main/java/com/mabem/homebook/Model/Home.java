package com.mabem.homebook.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Home {
    private String id = "";
    private String name = "";
    private String code = "";
    private boolean visibility = true; //True -> Public, False -> Private
    private HashMap<Member, Boolean> member_role = new HashMap<Member, Boolean>(); //Role: False -> Normal Member, True -> Admin

    public Home(String id, String name, String code, boolean visibility, HashMap<Member, Boolean> member_role) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.visibility = visibility;
        this.member_role = member_role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public HashMap<Member, Boolean> getMember_role() {
        return member_role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return ( id.equals(home.id) && code.equals(home.code));
    }

}
