package com.example.wisebuy.repositories;


import com.example.wisebuy.models.CategoryModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.function.Consumer;

public class CategoryRepository {
    private static final FirebaseFirestore fireStore;


    static {
        fireStore = FirebaseFirestore.getInstance();
    }

    public static void getCategories(Consumer<List<CategoryModel>> onSuccess, Consumer<Exception> onError) {
        fireStore.collection("Category").get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && onSuccess!=null){
                onSuccess.accept(task.getResult().toObjects(CategoryModel.class));
            }
            else if( onError!=null) {
                onError.accept(task.getException());

            }
        });
    }
}
