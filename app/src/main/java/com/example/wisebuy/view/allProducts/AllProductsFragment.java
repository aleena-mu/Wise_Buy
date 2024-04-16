package com.example.wisebuy.view.allProducts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.adapters.AllProductsAdapter;
import com.example.wisebuy.adapters.ProductSliderAdapter;
import com.example.wisebuy.databinding.FragmentAllProductsBinding;
import com.example.wisebuy.models.Products;
import com.example.wisebuy.models.WishList;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.view.auth.LoginActivity;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;
import com.example.wisebuy.viewModels.HomeViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Objects;

public class AllProductsFragment extends Fragment {

    private FragmentAllProductsBinding binding;
    private GridView allProductsView;
    private AllProductsAdapter updatedAdapter;
    private SearchView searchView;
    private AllProductsViewModel allProductsViewModel;
    private CartAndOrdersViewModel cartAndOrdersViewModel;
    private String categorySearch,topDealId;
    private ImageView wishlistIcon;
    private boolean isWishlistSelected;
    private MyPreference myPreference;
    private TextView logo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchView = root.findViewById(R.id.searchView);
        wishlistIcon=root.findViewById(R.id.wishListIcon);
        LinearLayout productDetailsView = root.findViewById(R.id.productDetails);
        productDetailsView.setVisibility(View.GONE);
        MyApplication myApplication = (MyApplication) requireActivity().getApplication();

        allProductsViewModel = myApplication.getAllProductsViewModel();
        cartAndOrdersViewModel = myApplication.getCartAndOrdersViewModel();
        myPreference = new MyPreference(requireContext());
        setupSearchView();

        Bundle bundle = getArguments();
        if (bundle != null) {
            categorySearch = bundle.getString("categoryTitle", null);
           topDealId=bundle.getString("dealId",null);
           Log.d("dealPro","id"+topDealId);
        }
             logo=root.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent=new Intent(requireContext(), MainActivity.class);
                startActivity(backIntent);
            }
        });
          allProductsView = root.findViewById(R.id.allProductsView);
          allProductsView.setOnItemClickListener((parent, view, position, id) -> {

              Products clickedProduct = (Products) parent.getItemAtPosition(position);
              String clickedProductId=clickedProduct.getDocumentId();
              Intent intent=new Intent(requireContext(),ProductDetails.class);
              intent.putExtra("ClickedProduct",clickedProductId);
              intent.putExtra("From","PRODUCTS");
              startActivity(intent);
       });
        HomeViewModel homeViewModel = myApplication.getHomeViewModel();

        AllProductsAdapter adapter = new AllProductsAdapter(requireContext(), new ArrayList<>());
        AllProductsAdapter categoryAdapter = new AllProductsAdapter(requireContext(), new ArrayList<>());
        AllProductsAdapter topDealsAdapter = new AllProductsAdapter(requireContext(), new ArrayList<>());
        homeViewModel.setTopDealAdapter(topDealsAdapter);

        allProductsViewModel.setAdapter(adapter);
        allProductsViewModel.setSearchAdapter(adapter);
        allProductsViewModel.loadProducts();

        if (categorySearch != null) {
            //Log.d("dealPro",categorySearch);
            allProductsViewModel.searchProducts(categorySearch);
            allProductsViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), productList -> {
                if (!productList.isEmpty()) {
                    categoryAdapter.setProductList(productList);
                    allProductsView.setAdapter(categoryAdapter);
                }
            });
        }

        else if(topDealId!=null){
            Log.d("dealPro","hello");
            homeViewModel.topDealItems(topDealId);
            homeViewModel.getTopDealsUpdatedLiveData().observe(getViewLifecycleOwner(),updated->{
                if(updated){
                    allProductsView.setAdapter(homeViewModel.getTopDealAdapter());
                }
            });
        }



        else {
           // Log.d("dealPro","out"+topDeal);
            allProductsViewModel.getAdapterUpdateLiveData().observe(getViewLifecycleOwner(), adapterUpdate -> {
                if (adapterUpdate) {
                    updatedAdapter = allProductsViewModel.getAdapter();
                    allProductsView.setAdapter(updatedAdapter);
                } else {
                   // Toast.makeText(requireContext(), "No such products", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return root;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                allProductsViewModel.searchProducts(query);
                allProductsViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), productsList -> {
                    if (!productsList.isEmpty()) {
                        updatedAdapter = allProductsViewModel.getSearchAdapter();
                        allProductsView.setAdapter(updatedAdapter);
                    }
                    else
                    {
                        Toast.makeText(requireContext(), "No such products", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                allProductsViewModel.searchProducts(newText);
                allProductsViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), productsList -> {
                    if (!productsList.isEmpty()) {
                        updatedAdapter = allProductsViewModel.getSearchAdapter();
                        allProductsView.setAdapter(updatedAdapter);
                    }
                    else
                    {
                       // Toast.makeText(requireContext(), "No such products", Toast.LENGTH_SHORT).show();
                    }
                });


                return true;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
