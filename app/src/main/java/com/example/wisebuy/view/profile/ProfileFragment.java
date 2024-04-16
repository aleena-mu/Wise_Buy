// ProfileFragment.java
package com.example.wisebuy.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.adapters.OrdersAdapter;
import com.example.wisebuy.adapters.WishListAdapter;
import com.example.wisebuy.repositories.CartAndOrdersRepository;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.view.auth.LoginActivity;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;
import com.example.wisebuy.viewModels.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private TextView nameText, logoutText,headingText;
    private Button loginButton;
    private ConstraintLayout profileLayout,myOrdersLayout;
    private ConstraintLayout signInLayout;
    private ProfileViewModel profileViewModel;
    private CartAndOrdersViewModel cartAndOrdersViewModel;
    private AllProductsViewModel allProductsViewModel;
    private OrdersAdapter ordersAdapter;
    private WishListAdapter wishListAdapter;
    private LinearLayout noWishList;
    private final String USER_PREFERENCE = "User";

    private FirebaseAuth auth;
private ImageView myOrders,myProfile,myWishList;
    private FirebaseFirestore firestore;
  private   MyPreference myPreference ;
   private RecyclerView myOrdersView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        nameText = root.findViewById(R.id.nameText);
        signInLayout = root.findViewById(R.id.sigininlayout);
        profileLayout = root.findViewById(R.id.profilelayout);
        myOrdersLayout=root.findViewById(R.id.ordersAndWishListLayout);
        headingText=root.findViewById(R.id.headingText);
        loginButton = root.findViewById(R.id.loginButton);
        logoutText = root.findViewById(R.id.logout);
        myOrders=root.findViewById(R.id.myOrders);
        myOrdersView=root.findViewById(R.id.ordersAndWishListView);
        myProfile=root.findViewById(R.id.myProfile);
        myWishList=root.findViewById(R.id.wishList);
        noWishList=root.findViewById(R.id.noWishList);
        wishListAdapter=new WishListAdapter(requireContext(),new ArrayList<>());

        MyApplication myApplication = (MyApplication) requireActivity().getApplication();
        cartAndOrdersViewModel = myApplication.getCartAndOrdersViewModel();
        allProductsViewModel=myApplication.getAllProductsViewModel();
        myOrdersView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myOrdersLayout.setVisibility(View.GONE);
        noWishList.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       myPreference = new MyPreference(requireContext());
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), userDetails -> {

            if (userDetails != null) {
                signInLayout.setVisibility(View.GONE);

                myPreference.saveUserDetailsToSharedPreferences(userDetails.getName(),userDetails.getDocumentId(),userDetails.getPhoneNumber(),userDetails.getPlace());
                CartAndOrdersRepository.currentUserId=myPreference.getUserId();
                profileLayout.setVisibility(View.VISIBLE);

                String userName = myPreference.getUsername();
                String formattedName = userName != null ? userName : "";
                nameText.setText(formattedName);
            } else {
                signInLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
            }
        });
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileViewModel.logout();
               myPreference.deleteUserFromSharedPreferences();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileLayout.setVisibility(View.GONE);
                myOrdersLayout.setVisibility(View.VISIBLE);
                headingText.setText(getResources().getText(R.string.my_orders));
                ordersAdapter=new OrdersAdapter(requireContext(),new ArrayList<>());
                cartAndOrdersViewModel.setOrdersAdapter(ordersAdapter);
                cartAndOrdersViewModel.showMyOrders(myPreference.getUserId());
                cartAndOrdersViewModel.getOrdersAdapterUpdatedLiveData().observe(getViewLifecycleOwner(),updated->{
                    if(updated){
                        myOrdersView.setAdapter(cartAndOrdersViewModel.getOrdersAdapter());
                    }
                });

            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(requireContext(),MyProfile.class);
                startActivity(intent);

            }
        });
        myWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileLayout.setVisibility(View.GONE);
                myOrdersLayout.setVisibility(View.VISIBLE);
                headingText.setText(getResources().getText(R.string.my_wishList));

                allProductsViewModel.setWishListAdapter(wishListAdapter);
                allProductsViewModel.showWishList(myPreference.getUserId());
                allProductsViewModel.getWishListUpdatedLiveData().observe(getViewLifecycleOwner(),updated->{
                    if(updated){
                        noWishList.setVisibility(View.GONE);
                        myOrdersView.setAdapter(allProductsViewModel.getWishListAdapter());
                    }
                    else{
                       noWishList.setVisibility(View.VISIBLE);
                    }

                });


            }
        });

    }


}
