package com.example.wisebuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisebuy.R;
import com.example.wisebuy.models.Orders;
import com.example.wisebuy.models.User;
import com.example.wisebuy.models.WishList;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    private  final Context context;
    private List<WishList> wishList;

    public WishListAdapter(Context context, List<WishList> wishList) {
        this.context = context;
        this.wishList=wishList;

    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_wish_list, parent, false);

        return new com.example.wisebuy.adapters.WishListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishList wishListModel=wishList.get(position);
        Glide.with(context)
                .load(wishListModel.getImageUrl())
                .into(holder.wishListItemPic);
        holder.wishListItemTitle.setText(wishListModel.getTitle());
        holder.wishListItemPrice.setText("₹"+ wishListModel.getPrice());
        holder.deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllProductsViewModel allProductsViewModel=new AllProductsViewModel();
                allProductsViewModel.wishListManagement(wishListModel.getProductId(), User.getInstance().getDocumentId());
                notifyDataSetChanged();
            }
        });
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartAndOrdersViewModel cartAndOrdersViewModel=new CartAndOrdersViewModel();
                cartAndOrdersViewModel.addToCart( User.getInstance().getDocumentId(),wishListModel.getProductId());
                AllProductsViewModel allProductsViewModel=new AllProductsViewModel();
                allProductsViewModel.wishListManagement(wishListModel.getProductId(), User.getInstance().getDocumentId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }
    public void renewItems(List<WishList> wishlist) {
        this.wishList = wishlist;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView wishListItemPic;
        TextView wishListItemTitle, wishListItemPrice,deleteText;
        Button addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wishListItemPic = itemView.findViewById(R.id.itemPic);
            wishListItemTitle = itemView.findViewById(R.id.itemTitle);
            wishListItemPrice = itemView.findViewById(R.id.wishListItemPrice);
            deleteText=itemView.findViewById(R.id.removeFromWishList);
            addToCart=itemView.findViewById(R.id.addToCart);

        }
    }

}
