package com.example.wisebuy.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkStateRepository {

    private final MutableLiveData<Boolean> isNetworkConnected = new MutableLiveData<>();

    public LiveData<Boolean> getNetworkState() {
        return isNetworkConnected;
    }

    public void updateNetworkState(boolean isConnected) {
        isNetworkConnected.postValue(isConnected);
    }
}
