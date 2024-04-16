package com.example.wisebuy.models;

import java.util.List;

public class Products {
    private List<String> imageUrls;
    private String title;
    private int price;
   private  String description;
   private  String type;
   private  String documentId;
   private  String details;
   private  String brand;

    public String getDetails() {
        return details;
    }

    public String getBrand() {
        return brand;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Products() {
        // Default constructor required for Firestore
    }

    public Products(String documentId, List<String> imageUrls, String title, double price, String description, String type, String brand, String details) {
        this.imageUrls = imageUrls;
        this.title = title;
        this.price = (int)price;
        this.description=description;
        this.type=type;
        this.documentId=documentId;
        this.brand=brand;
        this.details=details;
    }

    public String getDescription() {
        return description;
    }


    public String getType() {
        return type;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    // Use the 0th image URL for simplicity


    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }



}
