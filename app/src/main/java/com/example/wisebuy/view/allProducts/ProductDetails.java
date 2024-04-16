package com.example.wisebuy.view.allProducts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.adapters.ProductSliderAdapter;
import com.example.wisebuy.models.WishList;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.view.auth.LoginActivity;
import com.example.wisebuy.view.cart.CartFragment;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.Objects;

public class ProductDetails extends AppCompatActivity {
    private AllProductsViewModel allProductsViewModel;
    private ImageView wishlistIcon,backButton;
    private MyPreference myPreference;
    private boolean isWishlistSelected;
    private CartAndOrdersViewModel cartAndOrdersViewModel;
    private String clickedProductId,from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        MyApplication myApplication = (MyApplication) getApplication();
        myPreference = new MyPreference(getApplicationContext());
        cartAndOrdersViewModel = myApplication.getCartAndOrdersViewModel();
        Intent intent=getIntent();
        if (intent != null) {
            clickedProductId = intent.getStringExtra("ClickedProduct");
            from = intent.getStringExtra("From");
        }

        allProductsViewModel = myApplication.getAllProductsViewModel();
        wishlistIcon=findViewById(R.id.wishListIcon);
        LinearLayout productDetailsView =findViewById(R.id.productDetails);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        SliderView productImageSlider =findViewById(R.id.productImageSlider);
        TextView titleText = findViewById(R.id.titleText);
        TextView descriptionText = findViewById(R.id.descriptionText);
        TextView priceText = findViewById(R.id.priceText);
        TextView brandText = findViewById(R.id.brandText);


        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked","clicked");
                Intent intent = new Intent(ProductDetails.this, MainActivity.class);
                if(from!=null){
                    intent.putExtra("navigateToProducts", true);


                }
                startActivity(intent);

            }
        });


        wishlistIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(myPreference.getUserId()==null){
                    Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                toggleWishlistIcon(clickedProductId);
            }
        });

        allProductsViewModel.getProductDetails(clickedProductId);
        allProductsViewModel.getProductDetailsLiveData().observe(ProductDetails.this, productDetails -> {
            if (productDetails != null) {
                setInitialWishlistIconColor(clickedProductId);

                ProductSliderAdapter sliderAdapter = new ProductSliderAdapter(getApplicationContext(), productDetails.getImageUrls());
                productImageSlider.setSliderAdapter(sliderAdapter);
                productImageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                productImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                productImageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                productImageSlider.setScrollTimeInSec(3);
                productImageSlider.setSliderAdapter(sliderAdapter);
                titleText.setText(productDetails.getTitle());
                descriptionText.setText(productDetails.getDetails());
                priceText.setText("Price: ₹" + productDetails.getPrice());
                brandText.setText("Brand: " + productDetails.getBrand());

                addToCartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myPreference.getUserId() != null) {
                            cartAndOrdersViewModel.addToCart(myPreference.getUserId(), clickedProductId);

                            AndroidUtil.showToast(getApplicationContext(), "Product Added To Cart");
                            addToCartButton.setBackgroundColor(getColor(R.color.white));
                            addToCartButton.setTextColor(getColor(R.color.black));
                            addToCartButton.setText("Go To Cart");
                            addToCartButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProductDetails.this, MainActivity.class);
                                    intent.putExtra("navigateToCart", true);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Login To Explore Wise Buy", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                });
            }
            else {
                Toast.makeText(getApplicationContext(), "NoContent", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void toggleWishlistIcon(String productId) {
        if (isWishlistSelected) {
            allProductsViewModel.wishListManagement(productId,myPreference.getUserId());
            wishlistIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.black));

        } else {
            allProductsViewModel.wishListManagement(productId,myPreference.getUserId());
            wishlistIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
        }

        isWishlistSelected = !isWishlistSelected;
    }
    private void setInitialWishlistIconColor(String productId) {
        allProductsViewModel.showWishList(myPreference.getUserId());
        allProductsViewModel.getWishListLiveData().observe(ProductDetails.this,wishList->{
            if(wishList!=null ){

                for(WishList wishListItems :wishList){
                    String itemProductId=wishListItems.getProductId();
                    if(Objects.equals(productId, itemProductId)){
                        wishlistIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
                        isWishlistSelected=true;
                        return;
                    }


                }
            }
            else {
                // Handle the case where the wishList is null
                allProductsViewModel.getWishListExceptionLiveData().observe(ProductDetails.this, exception -> {
                    if (exception != null && exception.getMessage().equals("User Id Null")) {
                        isWishlistSelected=false;
                        wishlistIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.black));

                    }
                });
            }

            isWishlistSelected=false;
            wishlistIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.black));



        });


    }


}