package com.example.wisebuy.models;

public class HomeScreenDeals {
    private final String imageURL;
    private final String title;
    private final String deal;

    private final String documentId;


    public  HomeScreenDeals(String title, String deal, String imageURL, String dealName,String documentId){
        this.title=title;
        this.deal=deal;
        this.imageURL=imageURL;

        this.documentId=documentId;

    }
    public String getImageURL() {
        return imageURL;
    }

    public String getDocumentId() {
        return documentId;
    }



    public String getTitle() {
        return title;
    }

    public String getDeal() {
        return deal;
    }
}
