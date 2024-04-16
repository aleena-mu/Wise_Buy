package com.example.wisebuy.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import com.example.wisebuy.models.CartModel;
import com.example.wisebuy.repositories.CartAndOrdersRepository;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {


    private List<CartModel> cartList;
    private final Context context;


    private final CartAndOrdersRepository repository = new CartAndOrdersRepository();
    private final CartAndOrdersViewModel cartAndOrdersViewModel;

    public CartAdapter(Context context, List<CartModel> cartList, CartAndOrdersViewModel cartAndOrdersViewModel) {
        this.context = context;
        this.cartList = cartList;
        this.cartAndOrdersViewModel = cartAndOrdersViewModel;

    }

    @NonNull
    @Override
    public com.example.wisebuy.adapters.CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_design, parent, false);
        return new com.example.wisebuy.adapters.CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.wisebuy.adapters.CartAdapter.ViewHolder holder, int position) {
        CartModel cartModel = cartList.get(position);
        Glide.with(context)
                .load(cartModel.getImageUrl())
                .into(holder.cartPic);

        holder.cartTitle.setText(cartModel.getTitle());
        holder.cartItemPrice.setText(String.valueOf(cartModel.getPrice()));
        holder.itemQuantity.setText(String.valueOf(cartModel.getQuantity()));
        holder.cartItemTotal.setText(String.valueOf(cartModel.getTotalPriceofSingleItem()));
        holder.itemQuantity.setText(String.valueOf(cartModel.getQuantity()));


    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void renewItems(List<CartModel> cartModel) {
        this.cartList = cartModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cartPic;
        TextView cartTitle, cartItemPrice,cartItemTotal,itemQuantity,addItem,deleteItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartPic = itemView.findViewById(R.id.itemPic);
            cartTitle = itemView.findViewById(R.id.cartTitle);
            cartItemPrice = itemView.findViewById(R.id.singleItemCost);
            cartItemTotal = itemView.findViewById(R.id.totalCostOfItem);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            addItem=itemView.findViewById(R.id.addItem);
            deleteItem=itemView.findViewById(R.id.deleteItem);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        CartModel cartModel = cartList.get(currentPosition);
                        if(itemQuantity.getText().toString().equals("5")){
                            AndroidUtil.showToast(context,"Not more than 5 quantity can be add");
                        }
                        else{

                            cartAndOrdersViewModel.updateQuantity( 1, cartModel.getProductId());

                            NavController navController = Navigation.findNavController(itemView);
                            navController.popBackStack(R.id.navigation_home, true);
                            navController.navigate(R.id.navigation_cart);
//                            int currentQuantity=cartModel.getQuantity();
//                            itemQuantity.setText(String.valueOf(currentQuantity));
                            Log.d("CartId", cartModel.getProductId());
                        }
                    }
                }
            });


            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        CartModel cartModel = cartList.get(currentPosition);
                        if(itemQuantity.getText().toString().equals("1")){
                            AndroidUtil.showToast(context,"Item Deleted");
                            cartAndOrdersViewModel.updateQuantity(- 1, cartModel.getProductId());
                            NavController navController = Navigation.findNavController(itemView);
                           navController.popBackStack(R.id.navigation_home, true);
                            navController.navigate(R.id.navigation_home);
                        }
                        else {


                            cartAndOrdersViewModel.updateQuantity(- 1, cartModel.getProductId());


                            NavController navController = Navigation.findNavController(itemView);
                            navController.popBackStack(R.id.navigation_home, true);
                            navController.navigate(R.id.navigation_cart);
//                            int currentQuantity=cartModel.getQuantity();
//                            itemQuantity.setText(String.valueOf(currentQuantity));


                        }


                    }
                }
            });
        }
    }



}