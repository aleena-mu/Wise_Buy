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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.R;
import com.example.wisebuy.models.HomeScreenDeals;
import com.example.wisebuy.view.home.HomeFragment;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.HomeViewModel;
import com.example.wisebuy.viewModels.ProfileViewModel;


import java.util.List;

public class HomeScreenDealsAdapter extends RecyclerView.Adapter<HomeScreenDealsAdapter.ViewHolder> {

    private List<HomeScreenDeals> dealsList;
    private final Context context;
    private String dealSearch;

    public HomeScreenDealsAdapter(Context context, List<HomeScreenDeals> dealsList) {
        this.context = context;
        this.dealsList = dealsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_deals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeScreenDeals topDeals = dealsList.get(position);
        Glide.with(context)
                .load(topDeals.getImageURL())
                .into(holder.dealIcon);

        holder.dealCategory.setText(topDeals.getTitle());
        holder.dealDetail.setText(topDeals.getDeal());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dealId=topDeals.getDocumentId();
                Bundle bundle = new Bundle();
                bundle.putString("dealId", dealId);
                NavController navController = Navigation.findNavController(holder.itemView);
                navController.popBackStack(R.id.navigation_home, true);
                navController.navigate(R.id.navigation_all_products,bundle);


            }
        });
    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }
    public void renewItems(List<HomeScreenDeals> topDeals) {
        this.dealsList = topDeals;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dealIcon;
        TextView dealCategory;
        TextView dealDetail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dealIcon = itemView.findViewById(R.id.productImage);
            dealCategory = itemView.findViewById(R.id.dealCategory);
            dealDetail = itemView.findViewById(R.id.topDealDetail);

        }
    }

}
