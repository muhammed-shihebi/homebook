package com.mabem.homebook.Model;

public class Item {
    private String id = "";
    private String name = "";
    private Double price = 0.0;

    public Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item i = (Item) o;
        return (id.equals(i.id) && name.equals(i.name) && price.equals(i.price));
    }
}
