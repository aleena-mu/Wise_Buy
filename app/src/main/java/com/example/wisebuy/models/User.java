package com.example.wisebuy.models;

public class User {
    private String phoneNumber;
    private String name;
    private String place;
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    private static User instance;

    private User() {}

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
