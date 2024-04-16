package com.example.wisebuy.view.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.adapters.AllProductsAdapter;
import com.example.wisebuy.adapters.CategoryAdapter;
import com.example.wisebuy.adapters.HomeScreenDealsAdapter;
import com.example.wisebuy.adapters.SliderAdapter;

import com.example.wisebuy.databinding.FragmentHomeBinding;
import com.example.wisebuy.models.Products;
import com.example.wisebuy.repositories.HomeRepository;
import com.example.wisebuy.view.allProducts.ProductDetails;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.HomeViewModel;
import com.example.wisebuy.viewModels.ProfileViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SliderAdapter sliderAdapter;
    private AllProductsAdapter trendingAdapter;
    private CategoryAdapter updatedAdapter;
    private HomeScreenDealsAdapter dealsAdapter;
    private RecyclerView categoryView, topDealsView;
    private HomeViewModel homeViewModel;
    GridView trendingView;
    private static Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
       categoryView = root.findViewById(R.id.categoryView);
        categoryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        topDealsView = root.findViewById(R.id.topDealsView);
        topDealsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        trendingView = root.findViewById(R.id.trendingView);
        context = getContext();

        MyApplication myApplication = (MyApplication) requireActivity().getApplication();
        homeViewModel = myApplication.getHomeViewModel();
        AllProductsViewModel allProductsViewModel = myApplication.getAllProductsViewModel();
        allProductsViewModel.loadProducts();
        trendingAdapter = new AllProductsAdapter(requireContext(), new ArrayList<>());
        allProductsViewModel.setTrendingAdapter(trendingAdapter);
        allProductsViewModel.loadTrendingProducts();
        allProductsViewModel.getTrendingAdapterUpdateLiveData().observe(getViewLifecycleOwner(), update -> {
            if (update) {
                trendingView.setAdapter(allProductsViewModel.getTrendingAdapter());
            } else {
                trendingView.setVisibility(View.GONE);
            }
        });
        trendingView.setOnItemClickListener((parent, view, position, id) -> {

                    Products clickedProduct = (Products) parent.getItemAtPosition(position);
                    String clickedProductId = clickedProduct.getDocumentId();
                    Intent intent = new Intent(requireContext(), ProductDetails.class);
                    intent.putExtra("ClickedProduct", clickedProductId);
                    startActivity(intent);
                });

        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<>());
        dealsAdapter = new HomeScreenDealsAdapter(requireContext(), new ArrayList<>());


        homeViewModel.setAdapter(categoryAdapter);
        homeViewModel.setDealsAdapter(dealsAdapter);
        homeViewModel.loadCategories();

        homeViewModel.getAdapterUpdateLiveData().observe(getViewLifecycleOwner(), adapterUpdate -> {
            if (adapterUpdate) {
                updatedAdapter = homeViewModel.getAdapter();
                categoryView.setAdapter(updatedAdapter);
            }
        });

        homeViewModel.loadTopDeals();

        homeViewModel.getDealAdapterLiveData().observe(getViewLifecycleOwner(), adapterUpdate -> {
            if (adapterUpdate) {
                dealsAdapter = homeViewModel.getDealsAdapter();
                topDealsView.setAdapter(dealsAdapter);


            }
        });


        sliderAdapter = new SliderAdapter(getContext());
        SliderView sliderView = root.findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        HomeRepository.getOffers(offers -> {
            sliderAdapter.renewItems(offers);

        }, e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        });
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), userDetails -> {

            if (userDetails != null)
            {
                MyPreference myPreference = new MyPreference(requireContext());
                myPreference.saveUserDetailsToSharedPreferences(userDetails.getName(), userDetails.getDocumentId(), userDetails.getPhoneNumber(), userDetails.getPlace());
            }
        });

        return root;
    }


    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
