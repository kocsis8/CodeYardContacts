package com.example.codeyardcontacts;

import java.util.ArrayList;

public class Contact {
    private String name;
    private ArrayList<String> emails = new ArrayList<>() ;
    private String adress;
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private String imageURL;

    public Contact(String name, ArrayList<String> emails, String adress, ArrayList<String> phoneNumbers,String imageURL) {
        this.name = name;
        this.emails = emails;
        this.adress = adress;
        this.phoneNumbers = phoneNumbers;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public String getAdress() {
        return adress;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getImageURL() {
        return imageURL;
    }
}
