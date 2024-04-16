package com.example.wisebuy.models;

public class Orders {
    private final String title;
    private final String date;
    private final String  imageUrl;



    public Orders(String title, String date,String imageUrl) {
        this.title = title;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }


}
