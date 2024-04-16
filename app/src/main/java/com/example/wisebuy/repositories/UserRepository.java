package com.example.wisebuy.repositories;


import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wisebuy.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UserRepository {
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationCode;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public interface OnOtpSentListener {
        void onOtpSent(String verificationCode, PhoneAuthProvider.ForceResendingToken forceResendingToken);
    }

    public interface OnOtpFailedListener {
        void onOtpFailed(FirebaseException e);
    }

    public interface OnSignInCompleteListener {
        void onSignInComplete(Task<AuthResult> task);
    }

    public interface OnQueryCompleteListener {
        void onQueryComplete(Task<QuerySnapshot> task);
    }

    public interface OnUserAddedCompleteListener {
        void onUserAddedComplete(Task<DocumentReference> task);
    }


    public void sendOtp(String phoneNumber, boolean isResend, Activity activity, OnOtpSentListener onOtpSentListener, OnOtpFailedListener onOtpFailedListener) {

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                String receivedVerificationCode = phoneAuthCredential.getSmsCode();
                                Log.d("VerificationCode", "Received Verification Code: " + receivedVerificationCode);

                                onOtpSentListener.onOtpSent(phoneAuthCredential.getSmsCode(), null);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                onOtpFailedListener.onOtpFailed(e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                onOtpSentListener.onOtpSent(s, forceResendingToken);
                            }
                        });


        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    public void signIn(PhoneAuthCredential credential, OnSignInCompleteListener onSignInCompleteListener) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(onSignInCompleteListener::onSignInComplete);
    }

    public void checkUserExists(String phoneNumber, OnQueryCompleteListener onQueryCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("Users").whereEqualTo("Phone_Number", phoneNumber);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String documentId = documentSnapshot.getId();
                    String userName = documentSnapshot.getString("User_Name");
                    String place = documentSnapshot.getString("Place");

                    User.getInstance().setDocumentId(documentId);
                    User.getInstance().setName(userName);
                    User.getInstance().setPlace(place);
                    User.getInstance().setPhoneNumber(phoneNumber);


                    onQueryCompleteListener.onQueryComplete(task);
                } else {

                    onQueryCompleteListener.onQueryComplete(task);
                }
            } else {

                onQueryCompleteListener.onQueryComplete(task);
            }
        });
    }


    public void addUserToFirestore(OnUserAddedCompleteListener onUserAddedCompleteListener) {
        TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();

        Map<String, Object> userData = new HashMap<>();
        userData.put("Phone_Number", User.getInstance().getPhoneNumber());
        userData.put("User_Name", User.getInstance().getName());
        userData.put("Place", User.getInstance().getPlace());

        Log.d("UserModelCheck", User.getInstance().getPhoneNumber());

        db.collection("Users").add(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        onUserAddedCompleteListener.onUserAddedComplete(task);

                    } else {
                        onUserAddedCompleteListener.onUserAddedComplete(task);
                    }
                });

        Log.d("UserModel", taskCompletionSource.getTask().toString());


    }

    public void updateProfile(String updatedUserName, String updatedPlace, String phone, Consumer<Task<Void>> onSuccess, Consumer<Exception> onError) {
        // Get the reference to the document with the specified phone number
        CollectionReference usersCollection = db.collection("Users");
        Query query = usersCollection.whereEqualTo("Phone_Number", phone);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String documentId = documentSnapshot.getId();

                    // Update specific fields in the document
                    DocumentReference userDocRef = usersCollection.document(documentId);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("User_Name", updatedUserName);
                    updates.put("Place", updatedPlace);

                    userDocRef.update(updates).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            User.getInstance().setName(updatedUserName);
                            User.getInstance().setPlace(updatedPlace);
                            onSuccess.accept(updateTask);
                        } else {
                            onError.accept(updateTask.getException());
                        }
                    });
                } else {
                    // Handle the case where the user document with the specified phone number is not found

                }
            } else {
                // Handle the case where the query is not successful

            }
        });
    }

}
