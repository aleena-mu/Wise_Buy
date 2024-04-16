package com.example.wisebuy.models;

import java.util.List;

public class CartModel {

    private String imageUrl;
    private String title;


    private String productId;
    private int price;
    private int quantity;
    private int totalPriceofSingleItem;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPriceofSingleItem() {
        return totalPriceofSingleItem;
    }

    public void setTotalPriceofSingleItem(int totalPriceofSingleItem) {
        this.totalPriceofSingleItem = totalPriceofSingleItem;
    }
}
