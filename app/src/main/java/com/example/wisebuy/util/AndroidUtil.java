package com.example.wisebuy.util;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtil {
private    String currentUserId;
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public void setCURRENT_USER_ID(String userId){
        this.currentUserId=userId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}
