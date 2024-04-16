package com.example.wisebuy.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wisebuy.adapters.AllProductsAdapter;
import com.example.wisebuy.adapters.CategoryAdapter;
import com.example.wisebuy.adapters.HomeScreenDealsAdapter;
import com.example.wisebuy.repositories.CategoryRepository;
import com.example.wisebuy.repositories.HomeRepository;

public class HomeViewModel extends ViewModel {


    private final CategoryRepository repository = new CategoryRepository();
    private final MutableLiveData<Boolean> adapterUpdateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> topDealsUpdatedLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getTopDealsUpdatedLiveData() {
        return topDealsUpdatedLiveData;
    }

    private final MutableLiveData<Boolean> dealAdapterLiveData = new MutableLiveData<>();
    private CategoryAdapter adapter;
    private AllProductsAdapter topDealAdapter;

    public AllProductsAdapter getTopDealAdapter() {
        return topDealAdapter;
    }

    public void setTopDealAdapter(AllProductsAdapter topDealAdapter) {
        this.topDealAdapter = topDealAdapter;
    }

    private HomeScreenDealsAdapter dealsAdapter;




    public MutableLiveData<Boolean> getDealAdapterLiveData() {
        return dealAdapterLiveData;
    }

    public HomeScreenDealsAdapter getDealsAdapter() {
        return dealsAdapter;
    }

    public void setDealsAdapter(HomeScreenDealsAdapter dealsAdapter) {
        this.dealsAdapter = dealsAdapter;
    }

    public MutableLiveData<Boolean> getAdapterUpdateLiveData() {
        return adapterUpdateLiveData;
    }

    public CategoryAdapter getAdapter() {
        return adapter;
    }





    public void setAdapter(CategoryAdapter adapter) {
        this.adapter = adapter;
    }

    public void loadCategories() {
        CategoryRepository.getCategories(
                categoryModelList -> {
                    Log.d("HomeViewModel", "Categories loaded: " + categoryModelList.size());
                    if (adapter != null) {
                        adapter.renewItems(categoryModelList);
                        adapterUpdateLiveData.setValue(true);
                    }
                },
                exception -> {
                    Log.e("HomeViewModel", "Error loading categories", exception);
                    adapterUpdateLiveData.setValue(false);
                }
        );
    }
    public void loadTopDeals(){
        HomeRepository.getTopDeals(topDealList-> {
            if(dealsAdapter !=null){
                dealsAdapter.renewItems(topDealList);
                dealAdapterLiveData.postValue(true);

            }
            },
                e->{
                    Log.e("HomeViewModel", "Error loading categories", e);
                    dealAdapterLiveData.setValue(false);


        });

    }
    public void topDealItems(String documentId){
        Log.d("docId",documentId);
        HomeRepository.getTopDealItems(documentId,topDeals-> {
            if(topDealAdapter!=null){
                topDealAdapter.setProductList(topDeals);
                topDealsUpdatedLiveData.postValue(true);
            }
            else{
                topDealsUpdatedLiveData.postValue(false);
            }



        },
                e->{
                    topDealsUpdatedLiveData.postValue(false);


        });

    }

}