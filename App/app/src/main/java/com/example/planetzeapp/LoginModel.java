package com.example.planetzeapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginModel {
    private final FirebaseAuth auth;
    private final DatabaseReference database;

    public LoginModel() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public void login(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        checkSetupStatus(user.getUid(), callback);
                    } else if (user != null) {
                        callback.onEmailNotVerified();
                    } else {
                        callback.onFailure("User is null.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void checkSetupStatus(String uid, LoginCallback callback) {
        DatabaseReference setupStatusRef = database.child("users").child(uid)
                .child("annual_answers").child("consumption").child("recycling_frequency");

        setupStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String recyclingFrequency = snapshot.getValue(String.class);
                    boolean isSetupComplete = recyclingFrequency != null &&
                            (recyclingFrequency.equalsIgnoreCase("never") ||
                                    recyclingFrequency.equalsIgnoreCase("occasionally") ||
                                    recyclingFrequency.equalsIgnoreCase("frequently") ||
                                    recyclingFrequency.equalsIgnoreCase("always"));
                    if (isSetupComplete) {
                        callback.onSetupComplete();
                    } else {
                        callback.onSetupIncomplete();
                    }
                } else {
                    callback.onSetupIncomplete();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure("Database error: " + error.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSetupComplete();
        void onSetupIncomplete();
        void onEmailNotVerified();
        void onFailure(String errorMessage);
    }
}
