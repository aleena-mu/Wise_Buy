package com.example.wisebuy.view.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.MainActivity;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.R;
import com.example.wisebuy.viewModels.LoginViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {
    private EditText usernameInput, placeInput;
    private LoginViewModel loginViewModel;

    private static final String PREF_NAME="my_preference";
    private static final String USERNAME="user_name";
    private static final String PHONE="user_phone";
    private static final String PLACE="user_place";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        usernameInput = findViewById(R.id.login_username);
        placeInput = findViewById(R.id.login_place);
        Button continueButton = findViewById(R.id.login_continue_btn);

        MyApplication myApplication = (MyApplication) getApplication();
        loginViewModel = myApplication.getAuthViewModel();
        String phoneNumber = loginViewModel.getPhoneNumber();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String userName = usernameInput.getText().toString();
                String userPlace = placeInput.getText().toString();
                loginViewModel.addUser( userName, userPlace);
                loginViewModel.getUserAddedStatus().observe(RegistrationActivity.this, userAdded->
                {
                    if(userAdded){
                        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(mainIntent);

                    }
                    else{
                        AndroidUtil.showToast(RegistrationActivity.this,"NO DATA");
                    }
                });


            }
        });

    }

}
