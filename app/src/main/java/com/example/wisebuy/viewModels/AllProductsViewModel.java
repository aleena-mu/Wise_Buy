package com.example.wisebuy.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wisebuy.adapters.AllProductsAdapter;
import com.example.wisebuy.adapters.WishListAdapter;
import com.example.wisebuy.models.Products;
import com.example.wisebuy.models.WishList;
import com.example.wisebuy.repositories.AllProductsRepository;
import com.example.wisebuy.repositories.CartAndOrdersRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AllProductsViewModel extends ViewModel {
    private final AllProductsRepository repository = new AllProductsRepository();
    private final MutableLiveData<Boolean> adapterUpdateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> trendingAdapterUpdateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> wishListUpdatedLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<WishList>> wishListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Exception> wishListExceptionLiveData = new MutableLiveData<>();

    private AllProductsAdapter adapter;
    private AllProductsAdapter trendingAdapter;
    private WishListAdapter wishListAdapter;

    public AllProductsAdapter getTrendingAdapter() {
        return trendingAdapter;
    }

    public MutableLiveData<Boolean> getTrendingAdapterUpdateLiveData() {
        return trendingAdapterUpdateLiveData;
    }

    public void setTrendingAdapter(AllProductsAdapter trendingAdapter) {
        this.trendingAdapter = trendingAdapter;
    }

    public WishListAdapter getWishListAdapter() {
        return wishListAdapter;
    }

    public void setWishListAdapter(WishListAdapter wishListAdapter) {
        this.wishListAdapter = wishListAdapter;
    }
    public LiveData<Exception> getWishListExceptionLiveData() {
        return wishListExceptionLiveData;
    }
    public MutableLiveData<List<WishList>> getWishListLiveData() {
        return wishListLiveData;
    }

    private AllProductsAdapter searchAdapter;


    public MutableLiveData<Boolean> getWishListUpdatedLiveData() {
        return wishListUpdatedLiveData;
    }

    public AllProductsAdapter getSearchAdapter() {
        return searchAdapter;
    }

    public void setSearchAdapter(AllProductsAdapter searchAdapter) {
        this.searchAdapter = searchAdapter;
    }

    private final MutableLiveData<List<Products>> searchResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData <Products> productDetailsLiveData = new MutableLiveData<>();


    public MutableLiveData<Products> getProductDetailsLiveData() {
        return productDetailsLiveData;
    }

    public LiveData<List<Products>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public void setAdapter(AllProductsAdapter adapter) {
        this.adapter = adapter;
    }

    public AllProductsAdapter getAdapter() {
        return adapter;

    }

    public LiveData<Boolean> getAdapterUpdateLiveData() {
        return adapterUpdateLiveData;
    }

    public void loadProducts() {
        AllProductsRepository.getProducts(
                productList -> {
                    if (adapter != null) {
                        adapter.setProductList(productList);
                        adapterUpdateLiveData.setValue(true);

                    }
                    else{
                        Log.d("AdaptorNull","AdaptorNull");
                    }

                },
                exception -> {
                    adapterUpdateLiveData.setValue(false);
                    }
        );
    }

    public void searchProducts(String query) {
        Log.d("NewProductListUP",query);
        repository.searchProducts(query,
                products -> {
                    searchResultsLiveData.postValue(products);
                    searchAdapter.setProductList(products); // Update adapter with search results

                },
                e -> {
                    searchResultsLiveData.postValue(Collections.emptyList());


                });
        }

        public  void  getProductDetails(String id){

          repository.getProductDetails(id, productDetailsLiveData::postValue,

                  e -> {
                      searchResultsLiveData.postValue(Collections.emptyList());


                  });
        }

public void showWishList(String userId){
        CartAndOrdersRepository.showWishList(userId,wishList->{
            if(wishList !=null && !wishList.isEmpty() ) {
                wishListLiveData.postValue(wishList);
                if (wishListAdapter != null) {
                    wishListAdapter.renewItems(wishList);
                    wishListUpdatedLiveData.postValue(true);
                }
            }
            else{
                wishListLiveData.postValue(null);
                wishListUpdatedLiveData.postValue(false);
            }
                },
                e->{
                    if (Objects.equals(e.getMessage(), "User Id Null")) {
                        wishListExceptionLiveData.setValue(e);
                        wishListLiveData.postValue(null);

                        wishListUpdatedLiveData.postValue(false);
                    } else {

                        wishListLiveData.postValue(null);
                        wishListUpdatedLiveData.postValue(false);
                    }


        });
}
        public void wishListManagement(String productId,String userId){
        CartAndOrdersRepository.wishListManagement(productId,userId);
        wishListUpdatedLiveData.setValue(CartAndOrdersRepository.wishListUpdated);

        }
    public void loadTrendingProducts(){

        AllProductsRepository.getTrendingProducts(trendingProducts-> {
            if(trendingProducts!=null && !trendingProducts.isEmpty()){
                trendingAdapter.setProductList(trendingProducts);
                trendingAdapterUpdateLiveData.postValue(true);
            }
        },
                e->{
                    trendingAdapterUpdateLiveData.postValue(false);

        });


    }


    }

