
package com.example.wisebuy.view.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wisebuy.util.AndroidUtil;
import com.example.wisebuy.MainActivity;
import com.example.wisebuy.R;
import com.example.wisebuy.MyApplication;
import com.example.wisebuy.viewModels.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;

public class LoginOtpActivity extends AppCompatActivity {


  private   Long timeoutSeconds = 60L;

    private PhoneAuthProvider.ForceResendingToken resendingToken;

    private EditText otpInput;
    private Button nextBtn;
    private ProgressBar progressBar;
    private TextView resendOtpTextView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LoginViewModel loginViewModel;
    private String receivedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        MyApplication myApplication = (MyApplication) getApplication();
        loginViewModel = myApplication.getAuthViewModel();
        otpInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
        startResendTimer();


        loginViewModel.sendOtp(false, this);
        loginViewModel.getOtpSentStatus().observe(this, otpSent -> {

            setInProgress(true);
            if (otpSent) {
                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                setInProgress(false);
                resendOtpTextView.setText("Resend OTP");


            } else {
                AndroidUtil.showToast(getApplicationContext(), "OTP sending failed");
                setInProgress(false);
                resendOtpTextView.setVisibility(View.GONE);
            }

        });


        nextBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString();
            loginViewModel.setOtp(enteredOtp);
            loginViewModel.signInWithOtp();
            loginViewModel.getUserExistStatus().observe(this, userExists ->{
                Intent intent;
                if(userExists){

                    intent = new Intent(LoginOtpActivity.this, MainActivity.class);
                }
                else{
                    intent = new Intent(LoginOtpActivity.this, RegistrationActivity.class);
                }
                startActivity(intent);
            });
        });

        resendOtpTextView.setOnClickListener((v) -> {

            loginViewModel.sendOtp(true,this);
            loginViewModel.getOtpSentStatus().observe(this, otpSent -> {
                startResendTimer();
                setInProgress(true);


                if (otpSent) {
                    AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");

                    resendOtpTextView.setVisibility(View.GONE);
                    setInProgress(false);

                } else {
                    AndroidUtil.showToast(getApplicationContext(), "OTP sending failed");
                    setInProgress(false);
                    resendOtpTextView.setVisibility(View.GONE);

                }

            });

        });

    }



    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }



    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setText("Resend OTP");
                        resendOtpTextView.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }


}



