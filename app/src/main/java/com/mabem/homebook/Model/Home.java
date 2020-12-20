package com.mabem.homebook.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Home {
    private String id = "";
    private String name = "";
    private String code = "";
    private boolean visibility = true; //True -> Public, False -> Private
    private HashMap<Member, Boolean> member_role = new HashMap<Member, Boolean>(); //Role: False -> Normal Member, True -> Admin

    private ArrayList<Reminder> reminders;
    private ArrayList<Receipt> receipts;

    public Home(String id, String name){
        this.id = id;
        this.name = name;
    }

    public Home(String id, String name, String code, boolean visibility, ArrayList<Receipt> receipts){
        this.id = id;
        this.name = name;
        this.code = code;
        this.visibility = visibility;
        this.receipts = receipts;
    }

    public void setMember_role(HashMap<Member, Boolean> member_role) {
        this.member_role = member_role;
    }

    public void setReminders(ArrayList<Reminder> reminders) {
        this.reminders = reminders;
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

    public ArrayList<Reminder> getReminders() {
        return reminders;
    }

    public ArrayList<Receipt> getReceipts() {
        return receipts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return (id.equals(home.id) && code.equals(home.code));
    }

}
