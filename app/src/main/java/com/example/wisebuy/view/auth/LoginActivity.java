package com.example.wisebuy.view.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


import com.example.wisebuy.R;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.viewModels.LoginViewModel;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneInput;


    private String phoneNumber , receivedData;

    private CountryCodePicker countryCodePicker;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.phone_number);
        Button continueButton = findViewById(R.id.button_continue);
        MyApplication myApplication = (MyApplication) getApplication();
        loginViewModel = myApplication.getAuthViewModel();

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        continueButton.setOnClickListener((v) -> {

            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }
            phoneNumber = countryCodePicker.getFullNumberWithPlus();
            loginViewModel.setPhoneNumber(phoneNumber);


            Intent otpIntent = new Intent(LoginActivity.this, LoginOtpActivity.class);

            startActivity(otpIntent);
        });

    }


}
