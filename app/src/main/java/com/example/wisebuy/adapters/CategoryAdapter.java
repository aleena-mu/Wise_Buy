package com.example.wisebuy.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisebuy.R;
import com.example.wisebuy.models.CategoryModel;
import com.example.wisebuy.repositories.AllProductsRepository;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryList;
    private final Context context;
    public String categorySearch;


    private final AllProductsRepository repository = new AllProductsRepository();

    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_icons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        Glide.with(context)
                .load(category.getImageURL())
                .into(holder.categoryIcon);

        holder.categoryTitle.setText(category.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorySearch = category.getTitle();
                Bundle bundle = new Bundle();
                bundle.putString("categoryTitle", categorySearch);
                NavController navController = Navigation.findNavController(holder.itemView);
                navController.popBackStack(R.id.navigation_home, true);
                navController.navigate(R.id.navigation_all_products,bundle);

//                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_activity_main);
//                navController.navigate(R.id.action_navigation_home_to_navigation_all_products,bundle);

                // Pass the updated allProducts to AllProductsFragment
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void renewItems(List<CategoryModel> categoryModel) {
        this.categoryList = categoryModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
        }
    }

    private void navigateToAllProductsFragment(String categoryTitle) {
        // Pass the category title as an argument to the AllProductsFragment
//        Bundle bundle = new Bundle();
//        bundle.putString("categoryTitle", categoryTitle);
//        NavController navController = Navigation.findNavController(itemView);
//        navController.navigate(R.id.navigation_cart);
////
//        NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_activity_main);
//        navController.navigate(R.id.action_navigation_home_to_navigation_all_products,bundle);
    }
}
