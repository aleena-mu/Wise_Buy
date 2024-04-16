package com.example.wisebuy.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wisebuy.adapters.CartAdapter;
import com.example.wisebuy.adapters.OrdersAdapter;
import com.example.wisebuy.repositories.CartAndOrdersRepository;

public class CartAndOrdersViewModel extends ViewModel {


    private final MutableLiveData<Boolean> cartAdapterUpdateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ordersAdapterUpdatedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> subTotalLivedata = new MutableLiveData<>();

    public MutableLiveData<Boolean> getOrdersAdapterUpdatedLiveData() {
        return ordersAdapterUpdatedLiveData;
    }

    private CartAdapter cartAdapter;
    private OrdersAdapter ordersAdapter;

 public final MutableLiveData<String> formattedSubTotalLiveData = new MutableLiveData<>();
 public final MutableLiveData<Boolean> orderPlaced = new MutableLiveData<>();

    public LiveData<String> getFormattedSubTotalLiveData() {
        return formattedSubTotalLiveData;
    }

    public void setFormattedSubTotal(int subTotal) {
        String formattedSubTotal = "₹" + subTotal;
        formattedSubTotalLiveData.postValue(formattedSubTotal);
    }
    public MutableLiveData<Integer> getSubTotalLivedata() {
        return subTotalLivedata;
    }

    public MutableLiveData<Boolean> getCartAdapterUpdateLiveData() {
        return cartAdapterUpdateLiveData;
    }

    public CartAdapter getCartAdapter() {
        return cartAdapter;
    }
    public OrdersAdapter getOrdersAdapter() {
        return ordersAdapter;
    }

    public void setCartAdapter(CartAdapter cartAdapter) {
        this.cartAdapter = cartAdapter;
    }
    public void setOrdersAdapter(OrdersAdapter ordersAdapter) {
        this.ordersAdapter = ordersAdapter;
    }

    public void addToCart(String userId, String productId){
        CartAndOrdersRepository.addToCart(userId,productId);

    }
    public  void  showCart(String userId){
        CartAndOrdersRepository.showCart(userId,
                totalSum-> {
                    subTotalLivedata.postValue(totalSum.intValue());
                    setFormattedSubTotal(totalSum.intValue());
                },
                taskCart->{
                    if (!taskCart.isEmpty()) {
                        cartAdapter.renewItems(taskCart);
                        cartAdapterUpdateLiveData.postValue(true);
                    }
                    else{
                        cartAdapterUpdateLiveData.postValue(false);

                    }
                },
                exception-> {
            if(exception==null){
                cartAdapterUpdateLiveData.postValue(false);
            }
                    cartAdapterUpdateLiveData.postValue(false);

                });


    }

    public void updateQuantity(int quantity,String cartId){
        CartAndOrdersRepository.updateQuantity(quantity,cartId);
    }

    public MutableLiveData<Boolean> getOrderPlaced() {
        return orderPlaced;
    }

    public void placeOrder(){

        CartAndOrdersRepository.placeOrder(task-> {
                   orderPlaced.postValue(true);

        },
                e->{


        });

    }

    public  void showMyOrders(String userId){
        CartAndOrdersRepository.getMyOrders(userId,myOrdersList-> {
         ordersAdapter.renewItems(myOrdersList);
         ordersAdapterUpdatedLiveData.postValue(true);

        }, exception->
        {
            ordersAdapterUpdatedLiveData.postValue(false);

        });

    }
}