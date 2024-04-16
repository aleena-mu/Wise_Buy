package com.example.wisebuy.repositories;

import android.util.Log;

import com.example.wisebuy.models.HomeScreenDeals;
import com.example.wisebuy.models.Offer;
import com.example.wisebuy.models.Products;
import com.example.wisebuy.models.Video;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HomeRepository {
    private static final FirebaseFirestore fireStore;

    static {
        fireStore = FirebaseFirestore.getInstance();
    }

    public static void getOffers(Consumer<List<Offer>> onSuccess, Consumer<Exception> onError) {


        fireStore.collection("Offers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && onSuccess != null) {
                onSuccess.accept(task.getResult().toObjects(Offer.class));
            } else if (onError != null) {
                onError.accept(task.getException());

            }
        });
    }

    public static void getTopDeals(Consumer<List<HomeScreenDeals>> onSuccess, Consumer<Exception> onError) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("TopDeals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<HomeScreenDeals> dealsList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dealName = document.getString("dealName");

                    // Get the subcollection "Products" for each document
                    firestore.collection("TopDeals").document(document.getId())
                            .collection("Products").get().addOnCompleteListener(productsTask -> {
                                if (productsTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot productDocument : productsTask.getResult()) {

                                        String deal = productDocument.getString("deal");
                                        String id = productDocument.getId();

                                        DocumentReference categoryRef = productDocument.getDocumentReference("dealCategory");
                                        //DocumentReference productReference = productDocument.getDocumentReference("products");
                                        categoryRef.get().addOnCompleteListener(categoryTask -> {
                                            if (categoryTask.isSuccessful()) {
                                                DocumentSnapshot categoryDocument = categoryTask.getResult();
                                                if (categoryDocument.exists()) {
//                                                    String documentId = productDocument.getId();
                                                    String imageURL = categoryDocument.getString("imageURL");
                                                    String title = categoryDocument.getString("title");
                                                    HomeScreenDeals homeScreenDeals = new HomeScreenDeals(title, deal, imageURL, dealName, id);
                                                    dealsList.add(homeScreenDeals);
                                                    if (dealsList.size() == productsTask.getResult().size()) {
                                                        onSuccess.accept(dealsList);
                                                    }
                                                }
                                            } else if (onError != null) {
                                                onError.accept(categoryTask.getException());
                                            }
                                        });
                                    }
                                } else if (onError != null) {
                                    onError.accept(productsTask.getException());
                                }
                            });
                }
            } else if (onError != null) {
                onError.accept(task.getException());
            }
        });
    }


    public static void getTopDealItems(String id, Consumer<List<Products>> onSuccess, Consumer<Exception> onError) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<Products> topDealsProductList = new ArrayList<>();
        firestore.collection("TopDeals").document("topDeals").collection("Products").document(id).collection("DealProducts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = (DocumentReference) document.get("productReference");
                    firestore.collection("Products").get().addOnCompleteListener(taskProducts -> {
                        if (taskProducts.isSuccessful()) {
                            for (QueryDocumentSnapshot referredProduct : taskProducts.getResult()) {
                                DocumentReference reference = (DocumentReference) referredProduct.get(referredProduct.getId());
                                assert productReference != null;
                                if (productReference.equals(referredProduct.getReference())) {
                                    String documentId = referredProduct.getId();
                                    List<String> imageUrls = (List<String>) referredProduct.get("imageURLs");
                                    String title = referredProduct.getString("name");
                                    Double price = referredProduct.getDouble("price");
                                    String description = referredProduct.getString("description");
                                    String type = referredProduct.getString("type");
                                    String brand = referredProduct.getString("brand");
                                    String details = referredProduct.getString("details");
                                    Products product = new Products(documentId, imageUrls, title, price, description, type, brand, details);
                                    topDealsProductList.add(product);
                                }
                            }
                            onSuccess.accept(topDealsProductList);
                        }

                    });

                }


            } else {
                onError.accept(task.getException());
            }

        });
    }


}



