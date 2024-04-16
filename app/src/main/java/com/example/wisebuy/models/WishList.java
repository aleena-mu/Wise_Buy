package com.example.wisebuy.models;

public class WishList {
    private final String title;
    private final String imageUrl;
    private final String productId;
private final int price;
    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public String getProductId() {
        return productId;
    }

    public WishList(String title, String imageUrl, Double price, String productId) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price.intValue();
        this.productId = productId;
    }
}
