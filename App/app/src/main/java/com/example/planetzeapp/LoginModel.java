package com.example.planetzeapp;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginModel {
    private final FirebaseAuth auth;


    public LoginModel() {
        this.auth = FirebaseAuth.getInstance();
    }


    public void login(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        callback.onSuccess(user);
                    } else {
                        callback.onEmailNotVerified(user);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }


    public interface LoginCallback {
        void onSuccess(FirebaseUser user);


        void onEmailNotVerified(FirebaseUser user);


        void onFailure(Exception e);
    }
}

