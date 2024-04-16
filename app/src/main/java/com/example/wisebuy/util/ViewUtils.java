package com.example.wisebuy.util;

import android.content.Context;
import android.widget.Toast;

public class ViewUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
