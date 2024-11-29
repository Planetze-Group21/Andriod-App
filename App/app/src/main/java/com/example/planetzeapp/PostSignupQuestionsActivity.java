package com.example.planetzeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class PostSignupQuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_signup_questions);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView welcomeMessage = findViewById(R.id.welcome_message);

        // Retrieve user's first name and update the TextView
        getUserFirstName(firstName -> {
            if (firstName == null || firstName.trim().isEmpty()) {
                welcomeMessage.setText("Thank you for becoming a citizen of PlanetZe!");
            } else {
                welcomeMessage.setText("Thank you for becoming a citizen of PlanetZe, " + firstName + "!");
            }
        });
    }

    private void getUserFirstName(Consumer<String> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            databaseReference.child(uid).child("name").child("first_name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String firstName = snapshot.getValue(String.class);
                                callback.accept(firstName != null ? firstName : "Citizen");
                            } else {
                                callback.accept("Citizen");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.e("Firebase", "Error retrieving user first name", error.toException());
                            callback.accept("Citizen");
                        }
                    });
        } else {
            Log.e("Firebase", "No user is signed in");
            callback.accept("Citizen");
        }
    }
}
