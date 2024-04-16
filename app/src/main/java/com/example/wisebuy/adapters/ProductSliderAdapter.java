package com.example.wisebuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wisebuy.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductSliderAdapter extends SliderViewAdapter<ProductSliderAdapter.SliderAdapterVH> {

    private List<String> imageUrls;

    public ProductSliderAdapter(Context context, List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_details, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {


        Glide.with(viewHolder.itemView)
                .load(imageUrls.get(position))
                .fitCenter()
                .into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slidingProductImage);
            this.itemView = itemView;
        }
    }
}
