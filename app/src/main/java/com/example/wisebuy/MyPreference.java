package com.example.wisebuy;

import android.content.Context;
import android.content.SharedPreferences;



public class MyPreference {
    private final SharedPreferences preferences;

    public MyPreference(Context context) {
        String USER_PREFERENCE = "User";
        preferences = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

   public void saveUserDetailsToSharedPreferences(String username, String userId,String phoneNumber,String place) {

        if (preferences.getString("username", null) == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", username);
            editor.putString("userId", userId);
            editor.putString("userPhone", phoneNumber);
            editor.putString("userPlace", place);
            editor.apply();
        }
    }

   public void deleteUserFromSharedPreferences() {
       SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    public String getUsername() {
        return preferences.getString("username", null);
    }

    public String getUserId() {
        return preferences.getString("userId", null);

    }


    public String getUserPhone() {
        return preferences.getString("userPhone", null);

    }

    public String getUserPlace() {
        return preferences.getString("userPlace", null);

    }

    public  void updateValues(String updatedName, String updatedPlace){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", updatedName);
        editor.putString("userPlace", updatedPlace);
        editor.apply();
    }
    public  void updateDeliveryAddress(String place){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("deliveryAddress", place);
        editor.apply();
    }
    public String getDeliveryAddress() {
        return preferences.getString("deliveryAddress",getUserPlace());

    }


}
