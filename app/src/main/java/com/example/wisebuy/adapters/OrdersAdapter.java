package com.example.wisebuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisebuy.R;
import com.example.wisebuy.models.Orders;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{

   private  final Context context;
   private  List<Orders> ordersList;

    public  OrdersAdapter(Context context,List<Orders>ordersList){
        this.context=context;
        this.ordersList=ordersList;


    }


    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_design, parent, false);
        return new com.example.wisebuy.adapters.OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
     Orders ordersModel=ordersList.get(position);
     Glide.with(context)
             .load(ordersModel.getImageUrl())
             .into(holder.orderedItemPic);
     holder.orderedItemTitle.setText(ordersModel.getTitle());
     holder.orderedDate.setText("ordered On:"+ordersModel.getDate());
    }

    public int getItemCount() {
        return ordersList.size();
    }
    public void renewItems(List<Orders> orders) {
        this.ordersList = orders;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView orderedItemPic;
        TextView orderedItemTitle, orderedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderedItemPic = itemView.findViewById(R.id.itemPic);
            orderedItemTitle = itemView.findViewById(R.id.itemTitle);
            orderedDate = itemView.findViewById(R.id.orderedDate);
        }
    }


}
