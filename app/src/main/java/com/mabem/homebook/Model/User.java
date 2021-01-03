package com.mabem.homebook.Model;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String id = "";
    private String name = "";
    private String emailAddress = "";
    private String password = "";
    private Uri imageURI;

    public User(String id, String name, String emailAddress, String password, Uri imageURI) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.password = password;
        this.imageURI = imageURI;
    }

    public User(String id, String name, String emailAddress, Uri imageURI) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.imageURI = imageURI;
    }

    public User (String name, String id){
        this.name = name;
        this.id = id;
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

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User u = (User) o;
        return id.equals(u.id);
    }
}
