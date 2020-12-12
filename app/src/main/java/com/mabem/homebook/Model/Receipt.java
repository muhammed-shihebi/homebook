package com.mabem.homebook.Model;

import java.util.ArrayList;
import java.util.Date;

public class Receipt {
    private String id = "";
    private String name = "";
    private Date date;
    private ArrayList<Item> items;
    private String memberName;

    public Receipt(String id, String name, Date date, String userName) {
        this.id = id;
        this.name = name;
//        Calendar calendar = Calendar.getInstance();
//        this.date = DateFormat.getDateInstance().format(calendar.getTime());
        this.date = date;
        items = new ArrayList<Item>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getMemberName() {
        return memberName;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Receipt r = (Receipt) o;
        return (id.equals(r.id) && name.equals(r.name) && date.equals(r.date));
    }
}
