package com.example.codeyardcontacts;

import java.util.ArrayList;

public class Contact {
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String imageURL;

    public Contact(String name, String email, String address,String phoneNumber,String imageURL) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {return email;}

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageURL() {
        return imageURL;
    }
}
