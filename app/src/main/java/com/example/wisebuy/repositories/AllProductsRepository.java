package com.example.wisebuy.repositories;

import android.util.Log;

import com.example.wisebuy.models.Products;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AllProductsRepository {
    private static final FirebaseFirestore fireStore;
    private static List<Products> productList;

    static {
        fireStore = FirebaseFirestore.getInstance();
    }

    public static void getProducts(Consumer<List<Products>> onSuccess, Consumer<Exception> onError) {
        fireStore.collection("Products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && onSuccess != null) {
                productList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    List<String> imageUrls = (List<String>) document.get("imageURLs");
                    String title = document.getString("name");
                    Double price = document.getDouble("price");
                    String description = document.getString("description");
                    String type = document.getString("type");
                    String brand = document.getString("brand");
                    String details = document.getString("details");


                    Products product = new Products(documentId, imageUrls, title, price, description, type, brand, details);
                    productList.add(product);
                }

                onSuccess.accept(productList);
            } else if (onError != null) {
                onError.accept(task.getException());
            }
        });
    }

    public void searchProducts(String query, Consumer<List<Products>> onSuccess, Consumer<Exception> onError) {
        String lowerQuery = query.toLowerCase();
        Log.d("QueyinRep0", lowerQuery);
        List<Products> searchResults = new ArrayList<>();


        for (Products product : productList) {

            String lowerTitle = product.getTitle().toLowerCase();
            String lowerType = product.getType().toLowerCase();

            if (lowerTitle.contains(lowerQuery) || lowerType.contains(lowerQuery)) {
                Log.d("SearchProducts1", "Condition is true for: " + lowerTitle + " | " + lowerType + " | " + lowerQuery);
                searchResults.add(product);
            } else {
                Log.d("SearchProducts1", "Condition is false for: " + lowerTitle + " | " + lowerType + " | " + lowerQuery);
            }
        }

        Log.d("SearchProducts", "Product List Size: " + productList.size());
        Log.d("SearchProducts", "Search Results Size: " + searchResults.size());

        if (!searchResults.isEmpty()) {
            onSuccess.accept(searchResults);
        } else if (onError != null) {
            onError.accept(new Exception("No matching products found."));
        }
    }

    public void getProductDetails(String id, Consumer<Products> onSuccess, Consumer<Exception> onError) {

        Products searchResults = new Products();


        for (Products product : productList) {


            if (id.equals(product.getDocumentId())) {
                searchResults = product;
                break;
            }
        }

        if (onSuccess != null)
            onSuccess.accept(searchResults);
        else {
            onError.accept(new Exception("No matching products found."));
        }


    }

    public static void getTrendingProducts(Consumer<List<Products>> onSuccess, Consumer<Exception> onError) {
        FirebaseFirestore.getInstance().collection("Trending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Products> trendingProductsList = new ArrayList<>();
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                        for (QueryDocumentSnapshot trendingSnapshot : task.getResult()) {
                            DocumentReference productReference = trendingSnapshot.getDocumentReference("productReference");
                            Task<DocumentSnapshot> productTask = productReference.get();
                            tasks.add(productTask);
                        }

                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
                            for (Object obj : objects) {
                                if (obj instanceof DocumentSnapshot) {
                                    DocumentSnapshot productSnapshot = (DocumentSnapshot) obj;

                                    if (productSnapshot.exists()) {
                                        // Your existing logic for processing the product details
                                        String documentId = productSnapshot.getId();
                                        List<String> imageUrls = (List<String>) productSnapshot.get("imageURLs");
                                        String title = productSnapshot.getString("name");
                                        Double price = productSnapshot.getDouble("price");
                                        String description = productSnapshot.getString("description");
                                        String type = productSnapshot.getString("type");
                                        String brand = productSnapshot.getString("brand");
                                        String details = productSnapshot.getString("details");
                                        Products product = new Products(documentId, imageUrls, title, price, description, type, brand, details);
                                        trendingProductsList.add(product);
                                    }
                                }
                            }

                            onSuccess.accept(trendingProductsList);
                        }).addOnFailureListener(e -> {
                            if (onError != null) {
                                onError.accept(e);
                            }
                        });

                    }
                });

    }

}