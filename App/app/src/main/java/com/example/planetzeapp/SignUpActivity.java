package com.example.planetzeapp;

import static com.google.firebase.Firebase.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private FirebaseDatabase database;
    private EditText firstName, lastName, signupEmail, signupPassword, reenterPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        reenterPassword = findViewById(R.id.reenter_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = firstName.getText().toString().trim();
                String lName = lastName.getText().toString().trim();
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String reenter = reenterPassword.getText().toString().trim();

                if(fName.isEmpty()){
                    firstName.setError("Please input a first name.");
                }
                if(lName.isEmpty()){
                    lastName.setError("Please input a last name.");
                }
                if(user.isEmpty()){
                    signupEmail.setError("Please input an email.");
                }
                if(pass.isEmpty()){
                    signupPassword.setError("Please input a password.");
                }
                if(reenter.isEmpty()){
                    reenterPassword.setError("Please re-enter your password.");
                }
                else if(pass.equals(reenter)){
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Send verification email using FirebaseAuth instance
                                if (auth.getCurrentUser() != null) {
                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                initializeUserInDatabase(fName, lName);
                                                Toast.makeText(SignUpActivity.this, "Sign up successful! Verification email sent.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    reenterPassword.setError("Passwords don't match.");
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
    private void initializeUserInDatabase(String fName, String lName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            Map<String, Object> userData = new HashMap<>();
            Map<String, String> name = new HashMap<>();
            name.put("first_name", fName);
            name.put("last_name", lName);
            userData.put("name", name);
            userData.put("country", "");
            userData.put("annual_answers", new HashMap<>());
            userData.put("monthly_answers", new HashMap<>());
            userData.put("daily_answers", new HashMap<>());

            databaseReference.child(uid).setValue(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "User added to database");
                        } else {
                            // Handle failure
                            Log.e("Firebase", "Error adding user", task.getException());
                        }
                    });
        } else {
            Log.e("Firebase", "No user is signed in");
        }
    }
}