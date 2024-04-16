package com.example.wisebuy.view.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.adapters.CartAdapter;
import com.example.wisebuy.databinding.FragmentCartBinding;
import com.example.wisebuy.models.User;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.view.auth.LoginActivity;
import com.example.wisebuy.viewModels.CartAndOrdersViewModel;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartFragment extends Fragment {


    private TextView subTotalTextView, deliveryFeeTextView, deliveryAddress, totalTextView;
    private RecyclerView cartView;
    private CartAndOrdersViewModel cartAndOrdersViewModel;
    private CartAdapter cartAdapter;
   private LinearLayout  cartEmptyView;
   private ScrollView cartItemsView;
    int deliveryFee;
   private Button orderNow;
   private ImageView changePlace;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyApplication myApplication = (MyApplication) requireActivity().getApplication();
        cartAndOrdersViewModel = myApplication.getCartAndOrdersViewModel();

        com.example.wisebuy.databinding.FragmentCartBinding binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        cartView = root.findViewById(R.id.cartView);
        cartView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cartItemsView = root.findViewById(R.id.cartScroller);
        cartEmptyView = root.findViewById(R.id.cartEmpty);
        subTotalTextView = root.findViewById(R.id.subTotalText);
        deliveryFeeTextView = root.findViewById(R.id.deliveryFeeText);
        deliveryAddress=root.findViewById(R.id.deliveryAddress);

        totalTextView = root.findViewById(R.id.totalText);
     changePlace=root.findViewById(R.id.changePlace);


       orderNow=root.findViewById(R.id.orderButton);

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        MyPreference myPreference = new MyPreference(requireContext());
        cartAdapter = new CartAdapter(requireContext(), new ArrayList<>(), cartAndOrdersViewModel);
        cartAndOrdersViewModel.setCartAdapter(cartAdapter);
        String userId= myPreference.getUserId();
        if(userId==null){
            Intent intent=new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        cartAndOrdersViewModel.showCart(userId);
        cartAndOrdersViewModel.getCartAdapterUpdateLiveData().observe(getViewLifecycleOwner(), cartItems -> {

            if (!cartItems) {
                cartItemsView.setVisibility(View.GONE);
                cartEmptyView.setVisibility(View.VISIBLE);

            } else {
                cartEmptyView.setVisibility(View.GONE);
                cartAdapter = cartAndOrdersViewModel.getCartAdapter();
                cartView.setAdapter(cartAdapter);
                cartAndOrdersViewModel.getSubTotalLivedata().observe(getViewLifecycleOwner(), subtotal -> {
                    deliveryAddress.setText(myPreference.getDeliveryAddress());
                    subTotalTextView.setText("₹" + subtotal);
                    if (subtotal < 5000) {
                        deliveryFee = 40;
                    } else {
                        deliveryFee = 0;
                    }
                    String deliveryFeeText = "₹" + deliveryFee;
                    deliveryFeeTextView.setText(deliveryFeeText);

                    int grandTotal = (int) (subtotal + deliveryFee );
                    String totalText = "₹" + grandTotal;
                    totalTextView.setText(totalText);


                });
            }
        });
        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to place the order?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK, place the order
                        cartAndOrdersViewModel.placeOrder();
                        cartAndOrdersViewModel.getOrderPlaced().observe(getViewLifecycleOwner(), placed -> {
                            if (placed) {
                                cartEmptyView.setVisibility(View.VISIBLE);
                                AndroidUtil.showToast(requireContext(), "Order Placed Successfully");
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AndroidUtil.showToast(requireContext(), "Order Placement Cancelled");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        changePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Change Delivery Address");


                final EditText input = new EditText(requireContext());

                input.setHint("Enter Delivery Place");
                input.setHintTextColor( getResources().getColor(R.color.grey));
                builder.setView(input);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPlace = input.getText().toString().trim();

                        if (!newPlace.isEmpty()) {
                           deliveryAddress.setText(newPlace);
                           myPreference.updateDeliveryAddress(newPlace);
                            AndroidUtil.showToast(requireContext(), "Delivery Address Updated");
                        } else {
                            AndroidUtil.showToast(requireContext(), "Please enter a valid place");
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }
}
