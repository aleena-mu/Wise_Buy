package com.example.wisebuy.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.wisebuy.repositories.NetworkStateRepository;

public class NetworkReceiver extends BroadcastReceiver {

    private final NetworkStateRepository networkStateRepository;

    public NetworkReceiver(NetworkStateRepository networkStateRepository) {
        this.networkStateRepository = networkStateRepository;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean isConnected = isNetworkConnected(context);
            networkStateRepository.updateNetworkState(isConnected);
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
