package com.example.wisebuy;

import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.wisebuy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (findViewById(R.id.container) != null) {
            if (savedInstanceState == null) {
                // Checking if there's an intent to navigate to the CartFragment
                if (getIntent().getBooleanExtra("navigateToCart", false)) {
                    navController.popBackStack(R.id.navigation_home, true);
                    navController.navigate(R.id.navigation_cart);
                }
                else if(getIntent().getBooleanExtra("navigateToProducts", false)){
                    navController.popBackStack(R.id.navigation_home, true);
                    navController.navigate(R.id.navigation_all_products);
                }

                    NavigationUI.setupWithNavController(binding.navView, navController);


            }
        }

    }

}