package com.mabem.homebook.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;

public class Receipt {
    private String id = "";
    private String name = "";
    private String date;
    private ArrayList<Item> items;

    public Receipt(String id, String name) {
        this.id = id;
        this.name = name;
        Calendar calendar = Calendar.getInstance();
        this.date = DateFormat.getDateInstance().format(calendar.getTime());
        items = new ArrayList<Item>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Item> getItems() {
        return items;
    }


    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        Receipt r = (Receipt) o;
        return ( id.equals(r.id) && name.equals(r.name) && date.equals(r.date) );
    }
}
