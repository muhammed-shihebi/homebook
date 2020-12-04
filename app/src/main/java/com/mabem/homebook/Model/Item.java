package com.mabem.homebook.Model;

public class Item {
    private String id = "";
    private String name = "";
    private float price = 0f;
    private Receipt receipt;

    public Item(String id, String name, float price, Receipt receipt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.receipt = receipt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        Item i = (Item) o;
        return ( id.equals(i.id) && name.equals(i.name) && price == i.price);
    }
}
