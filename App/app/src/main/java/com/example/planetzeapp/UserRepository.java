package com.example.planetzeapp;


import androidx.annotation.NonNull;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserRepository {


    public interface SetupQsCallback {
        void onSetupComplete(boolean isSetupComplete);


        void onError(String errorMessage);
    }


    public void checkSetupQuestions(String uid, SetupQsCallback callback) {
        DatabaseReference finalQRef = FirebaseDatabase.getInstance()
                .getReference("users").child(uid)
                .child("annual_answers").child("consumption").child("recycling_frequency");


        finalQRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String recyclingFrequency = snapshot.getValue(String.class);
                    boolean isSetupComplete = recyclingFrequency != null &&
                            (recyclingFrequency.equalsIgnoreCase("never") ||
                                    recyclingFrequency.equalsIgnoreCase("occasionally") ||
                                    recyclingFrequency.equalsIgnoreCase("frequently") ||
                                    recyclingFrequency.equalsIgnoreCase("always"));
                    callback.onSetupComplete(isSetupComplete);
                } else {
                    callback.onSetupComplete(false);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}




