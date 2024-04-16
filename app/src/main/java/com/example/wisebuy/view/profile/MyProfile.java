package com.example.wisebuy.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.wisebuy.MyApplication;
import com.example.wisebuy.MyPreference;
import com.example.wisebuy.R;
import com.example.wisebuy.models.User;
import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.viewModels.ProfileViewModel;

import org.checkerframework.checker.guieffect.qual.UI;

public class MyProfile extends AppCompatActivity {

  private   EditText userName,phoneNumber,place;
   private MyPreference myPreference;
    private ProfileViewModel profileViewModel;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        userName=findViewById(R.id.profile_username);
        phoneNumber=findViewById(R.id.profile_phone);
        place=findViewById(R.id.profile_place);
        myPreference = new MyPreference(getApplicationContext());
        userName.setText(myPreference.getUsername());
        phoneNumber.setText(myPreference.getUserPhone());
        place.setText(myPreference.getUserPlace());
        progressBar=findViewById(R.id.updateProgress);
        setInProgress(false);

        Button updateButton=findViewById(R.id.profile_update_btn);

        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setInProgress(true);
                profileViewModel = new ViewModelProvider(MyProfile.this).get(ProfileViewModel.class);
                String updatedUserName=userName.getText().toString();
                String updatedPlace=place.getText().toString();
                profileViewModel.updateProfile(updatedUserName,updatedPlace,myPreference.getUserPhone());
                profileViewModel.getUserDataUpdated().observe(MyProfile.this,updated->{
                    if(updated){
                        setInProgress(false);
                        userName.setText(User.getInstance().getName());
                        place.setText(User.getInstance().getPlace());
                        AndroidUtil.showToast(getApplicationContext(),"Profile Updated Successfully");
                        myPreference.updateValues(User.getInstance().getName(),User.getInstance().getPlace());

                    }
                    else{
                        setInProgress(false);
                        AndroidUtil.showToast(getApplicationContext(),"Profile is not updated");
                    }
                });
            }
        });


    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);

        }else{
            progressBar.setVisibility(View.GONE);

        }
    }
}