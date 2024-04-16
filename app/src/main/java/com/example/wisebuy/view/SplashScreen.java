package com.example.wisebuy.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.viewModels.AllProductsViewModel;
import com.example.wisebuy.viewModels.HomeViewModel;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        MyApplication myApplication = (MyApplication) getApplication();

        HomeViewModel homeViewModel = myApplication.getHomeViewModel();


        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Animation botAnim = AnimationUtils.loadAnimation(this, R.anim.bot_anim);


        ImageView logo = findViewById(R.id.logo);


        logo.setAnimation(botAnim);
        logo.setAnimation(topAnim);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

     homeViewModel.loadTopDeals();
     new Handler().postDelayed(() -> {
         loadingProgressBar.setVisibility(View.GONE);


            Intent dsp = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(dsp);
            finish();
        }, 10000);
   }

}






