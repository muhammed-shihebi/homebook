package com.mabem.homebook.Model;

import java.util.ArrayList;
import java.util.Date;

public class Receipt {
    private String id;
    private String name;
    private Date date;
    private Double total;
    private ArrayList<Item> items;
    private String memberName;
    private String memberId;

    public Receipt(){}


    public Receipt(String id, String name, Date date, Double total, String memberName, String memberId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.total = total;
        this.items = new ArrayList<Item>();
        this.memberId = memberId;
        this.memberName = memberName;
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

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public Double getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Receipt r = (Receipt) o;
        return (id.equals(r.id) && name.equals(r.name) && date.equals(r.date));
    }
}
