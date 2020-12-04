package com.mabem.homebook.Model;

public class User {
    private String id = "";
    private String name = "";
    private String emailAddress = "";
    private String password = "";
    private String imageURL = "";

    public User(String id, String name, String emailAddress, String password, String imageURL) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.password = password;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        User u = (User) o;
        return id.equals(u.id);
    }
}
