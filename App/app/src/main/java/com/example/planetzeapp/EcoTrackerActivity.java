package com.example.planetzeapp;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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


public class EcoTrackerActivity extends AppCompatActivity {

    private ImageButton imageButton2;
    private TextView resultText2;
    private ImageButton imageButton3;
    private TextView resultText3;
    private ImageButton imageButton4;
    private TextView resultText4;
    private ImageButton imageButton5;
    private TextView resultText5;
    private ImageButton imageButton7;
    private TextView resultText7;
    private ImageButton imageButton9;
    private TextView resultText9;
    private ImageButton imageButton10;
    private TextView resultText10;
    private ImageButton imageButton11;
    private TextView resultText11;
    private ImageButton imageButton12;
    private TextView resultText12;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            currentUid = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        imageButton2 = findViewById(R.id.imageButton2);
        resultText2 = findViewById(R.id.result_text2);
        imageButton3 = findViewById(R.id.imageButton3);
        resultText3 = findViewById(R.id.result_text3);
        imageButton4 = findViewById(R.id.imageButton4);
        resultText4 = findViewById(R.id.result_text4);
        imageButton5 = findViewById(R.id.imageButton5);
        resultText5 = findViewById(R.id.result_text5);
        imageButton7 = findViewById(R.id.imageButton7);
        resultText7 = findViewById(R.id.result_text7);
        imageButton9 = findViewById(R.id.imageButton9);
        resultText9 = findViewById(R.id.result_text9);
        imageButton10 = findViewById(R.id.imageButton10);
        resultText10 = findViewById(R.id.result_text10);
        imageButton11 = findViewById(R.id.imageButton11);
        resultText11 = findViewById(R.id.result_text11);
        imageButton12 = findViewById(R.id.imageButton12);
        resultText12 = findViewById(R.id.result_text12);

        imageButton2.setOnClickListener(v -> openInputDialog("input_value2", resultText2));
        imageButton3.setOnClickListener(v -> openInputDialog("input_value3", resultText3));
        imageButton4.setOnClickListener(v -> openInputDialog("input_value4", resultText4));
        imageButton5.setOnClickListener(v -> openInputDialog("input_value5", resultText5));
        imageButton7.setOnClickListener(v -> openInputDialog("input_value7", resultText7));
        imageButton9.setOnClickListener(v -> openInputDialog("input_value9", resultText9));
        imageButton10.setOnClickListener(v -> openInputDialog("input_value10", resultText10));
        imageButton11.setOnClickListener(v -> openInputDialog("input_value11", resultText11));
        imageButton12.setOnClickListener(v -> openInputDialog("input_value12", resultText12));
        }
    }
}