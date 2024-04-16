package com.example.wisebuy.models;

import android.util.Log;

public class CategoryModel {

    private String imageURL;
    private String title;

    public CategoryModel() {
        // Default constructor required for Firestore
    }

    public CategoryModel(String imageUrl, String title) {
        this.imageURL = imageUrl;
        this.title = title;

        Log.d("title", title);

    }

    public String getImageURL() {

        return imageURL;

    }

    public String getTitle() {
        Log.d("TitleTest", title);
        return title;
    }

}

