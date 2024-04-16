package com.example.wisebuy.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wisebuy.models.CartModel;
import com.example.wisebuy.models.Orders;
import com.example.wisebuy.models.WishList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CartAndOrdersRepository {

    private static final FirebaseFirestore fireStore;
    public static String currentUserId;
    private static List<Orders> myOrdersList;
    private static List<WishList> myWishList;
    public static boolean wishListUpdated;




    static {
        fireStore = FirebaseFirestore.getInstance();
    }

    public static void addToCart(String userId, String productId) {

if(userId==null){
    userId=currentUserId;
}
else{
    currentUserId=userId;
}
        fireStore.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference productReference = fireStore.collection("Products").document(productId);
                productReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot productSnapshot) {
                                if (productSnapshot.exists()) {

                                    List<String> imageUrls = (List<String>) productSnapshot.get("imageURLs");
                                    String title = productSnapshot.getString("name");
                                    Double price = productSnapshot.getDouble("price");


                                    for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                        String documentId = userDocument.getId();
                                        if (Objects.equals(currentUserId, documentId)) {
                                            DocumentReference userReference = userDocument.getReference();
                                            CollectionReference cartReference = userReference.collection("Cart");


                                            Query query = cartReference.whereEqualTo("productId", productId);
                                            query.get().addOnCompleteListener(cartTask -> {
                                                if (cartTask.isSuccessful()) {
                                                    if (cartTask.getResult().isEmpty()) {
                                                        CartModel cartItem = new CartModel();

                                                        cartItem.setProductId(productId);
                                                        cartItem.setImageUrl(imageUrls.get(0));
                                                        cartItem.setTitle(title);
                                                        cartItem.setPrice(price.intValue());
                                                        cartItem.setQuantity(1);
                                                        cartItem.setTotalPriceofSingleItem(cartItem.getQuantity() * cartItem.getPrice());

                                                        cartReference.add(cartItem)
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Handle failure
                                                                    }
                                                                });
                                                    } else {
                                                        DocumentSnapshot cartItemSnapshot = cartTask.getResult().getDocuments().get(0);
                                                        CartModel existingCartItem = cartItemSnapshot.toObject(CartModel.class);

                                                        if (existingCartItem != null) {
                                                            int currentQuantity = existingCartItem.getQuantity();
                                                            int updatedQuantity = currentQuantity + 1;
                                                            Log.d("UPDATED QUANTITY", String.valueOf(updatedQuantity));
                                                            if(updatedQuantity<=5){
                                                                int updatedTotalPrice = updatedQuantity * price.intValue();


                                                                cartItemSnapshot.getReference().update(
                                                                        "quantity", updatedQuantity,
                                                                        "totalPriceofSingleItem", updatedTotalPrice
                                                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Cart item updated successfully
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Handle failure
                                                                    }
                                                                });

                                                            }


                                                        }

                                                    }



                                                } else {

                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

    public static void showCart(String userId, Consumer<Double> totalSum, Consumer<List<CartModel>> onSuccess, Consumer<Exception> onError) {
        currentUserId = userId;

        fireStore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot queryDocumentSnapshot = task.getResult();
                DocumentReference userReference = queryDocumentSnapshot.getReference();
                CollectionReference cartReference = userReference.collection("Cart");
                cartReference.get().addOnCompleteListener(taskCart -> {
                    if (taskCart.isSuccessful() && onSuccess != null) {
                        List<CartModel> cartModels = taskCart.getResult().toObjects(CartModel.class);


                        double sum = 0;
                        for (CartModel cartModel : cartModels) {

                            sum += cartModel.getTotalPriceofSingleItem();
                        }


                        if (totalSum != null) {
                            totalSum.accept(sum);
                        }


                        onSuccess.accept(cartModels);
                    } else if (onError != null) {
                        onError.accept(taskCart.getException());
                    }
                });
            }
        });
    }

    public static void updateQuantity(int quantity, String productId) {
        fireStore.collection("Users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocumentSnapshot = task.getResult();
                DocumentReference userReference = userDocumentSnapshot.getReference();

                CollectionReference cartCollectionReference = userReference.collection("Cart");
                Query query = cartCollectionReference.whereEqualTo("productId", productId);
                query.get().addOnCompleteListener(cartTask -> {
                    if (cartTask.isSuccessful()) {
                        DocumentSnapshot cartSnapShot = cartTask.getResult().getDocuments().get(0);
                        if (cartSnapShot.exists()) {
                            Double currentQuantity = cartSnapShot.getDouble("quantity");
                            Double updatedQuantity = currentQuantity + quantity;
                            if (updatedQuantity > 5) {
                                updatedQuantity = currentQuantity;
                            } else if (updatedQuantity < 1) {
                                cartSnapShot.getReference().delete();
                            }
                            Double price = cartSnapShot.getDouble("price");
                            if (price != null) {
                                int updatedTotalPrice = updatedQuantity.intValue() * price.intValue();
                                cartSnapShot.getReference().update("quantity", updatedQuantity.intValue(), "totalPriceofSingleItem", updatedTotalPrice)
                                        .addOnCompleteListener(taskUpdated -> {
                                            if (taskUpdated.isSuccessful()) {

                                            } else {

                                            }
                                        });
                            }


                        } else {

                        }


                    } else {
                    }
                });
            } else {
            }
        });
    }

    public static void placeOrder(Consumer<Void> onSuccess, Consumer<Exception> onError) {
        fireStore.collection("Users").document(currentUserId).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot userDocumentSnapshot = userTask.getResult();
                DocumentReference userReference = userDocumentSnapshot.getReference();

                CollectionReference cartCollectionReference = userReference.collection("Cart");
                CollectionReference ordersCollectionReference = userReference.collection("Orders");

                cartCollectionReference.get().addOnCompleteListener(cartTask -> {
                    if (cartTask.isSuccessful()) {
                        List<CartModel> cartModels = cartTask.getResult().toObjects(CartModel.class);

                        // Create orders in the "Orders" subcollection
                        for (CartModel cartModel : cartModels) {
                            Map<String, Object> orderData = new HashMap<>();
                            orderData.put("productId", cartModel.getProductId());
                            orderData.put("imageUrl", cartModel.getImageUrl());
                            orderData.put("title", cartModel.getTitle());
                            orderData.put("price", cartModel.getPrice());
                            orderData.put("quantity", cartModel.getQuantity());
                            orderData.put("totalPriceofSingleItem", cartModel.getTotalPriceofSingleItem());
                            orderData.put("orderDate", FieldValue.serverTimestamp());

                            ordersCollectionReference.add(orderData)
                                    .addOnSuccessListener(documentReference -> {
                                        // Successfully added order to "Orders" subcollection
                                    })
                                    .addOnFailureListener(onError::accept);
                        }


                        // Delete documents from the "Cart" subcollection
                        deleteCartDocuments(cartCollectionReference, cartModels, onSuccess, onError);
                    } else {
                        onError.accept(cartTask.getException());
                    }
                });
            } else {
                onError.accept(userTask.getException());
            }
        });
    }

    private static void deleteCartDocuments(CollectionReference cartReference, List<CartModel> cartModels, Consumer<Void> onSuccess, Consumer<Exception> onError) {

        cartReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                for (DocumentSnapshot document : documents) {
                    document.getReference().delete()
                            .addOnFailureListener(onError::accept);
                }

                onSuccess.accept(null);
            } else {
                onError.accept(task.getException());
            }
        });
    }

    public static void getMyOrders(String userId,Consumer<List<Orders>> onSuccess, Consumer<Exception> onError) {
        fireStore.collection("Users").document(userId).collection("Orders")
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful() && onSuccess != null) {
                        myOrdersList = new ArrayList<>();
                        for (QueryDocumentSnapshot orderSnapshot : orderTask.getResult()) {

                            Date orderDate = orderSnapshot.getDate("orderDate");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                            String date = dateFormat.format(orderDate);
                            String orderTitle = orderSnapshot.getString("title");
                            String imageUrl = orderSnapshot.getString("imageUrl");
                            Orders myOrders = new Orders(orderTitle, date, imageUrl);
                            myOrdersList.add(myOrders);
                        }
                        onSuccess.accept(myOrdersList);
                    } else if (onError != null) {
                        onError.accept(orderTask.getException());
                    }
                });
    }


    public static void showWishList(String userId,Consumer<List<WishList>> onSuccess, Consumer<Exception> onError) {
        if(userId==null){
            onError.accept(new Exception("User Id Null"));
            return;
        }

        currentUserId=userId;
        fireStore.collection("Users").document(userId).collection("WishList")
                .orderBy("wishListDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(wishListTask -> {
                    if (wishListTask.isSuccessful() && onSuccess != null) {
                        myWishList = new ArrayList<>();
                        for (QueryDocumentSnapshot wishListSnapshot : wishListTask.getResult()) {

                            String orderTitle = wishListSnapshot.getString("title");
                            String productId = wishListSnapshot.getString("productId");
                            String imageUrl = wishListSnapshot.getString("imageUrl");
                            Double price = wishListSnapshot.getDouble("price");
                            WishList wishList = new WishList(orderTitle, imageUrl, price,productId);
                            myWishList.add(wishList);
                        }
                        onSuccess.accept(myWishList);
                    } else if (!wishListTask.isSuccessful() && onError != null) {
                        onError.accept(wishListTask.getException());
                    }


                });
    }


    public static void wishListManagement(String productId,String userId) {
if(userId==null){
    userId=currentUserId;
}
        DocumentReference userDocumentRef = fireStore.collection("Users").document(userId);
        CollectionReference wishListCollectionRef = userDocumentRef.collection("WishList");

        wishListCollectionRef.whereEqualTo("productId", productId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            DocumentReference productDocumentRef = documentSnapshot.getReference();
                            productDocumentRef.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            // Product deleted successfully
                                        } else {
                                            // Handle the delete task failure if needed
                                        }
                                    });
                        } else {
                            DocumentReference productReference = fireStore.collection("Products").document(productId);
                            productReference.get()
                                    .addOnSuccessListener(productSnapshot -> {
                                        if (productSnapshot.exists()) {
                                            // Product details
                                            List<String> imageUrls = (List<String>) productSnapshot.get("imageURLs");
                                            String title = productSnapshot.getString("name");
                                            Double price = productSnapshot.getDouble("price");

                                            Map<String, Object> wishListData = new HashMap<>();
                                            wishListData.put("productId", productId);
                                            wishListData.put("wishListDate", FieldValue.serverTimestamp());
                                            wishListData.put("title", title);
                                            wishListData.put("price", price);
                                            wishListData.put("imageUrl", imageUrls.get(0));

                                            wishListCollectionRef.add(wishListData)
                                                    .addOnCompleteListener(addTask -> {
                                                        wishListUpdated= addTask.isSuccessful();
                                                    });
                                        }
                                    });
                        }
                    } else {
                        // Handle the task failure if needed
                    }
                });
    }


}