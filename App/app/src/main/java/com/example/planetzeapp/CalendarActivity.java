package com.example.planetzeapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private String currentUid;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar_view);

        // Find views by ID
        calendarView = findViewById(R.id.calendar_view);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid).child("daily answers");

        // Set listener for calendar interactions
        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + day;

            databaseRef.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        databaseRef.setValue(selectedDate).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Date " + selectedDate + " added successfully to Firebase.");
                            } else {
                                Log.d(TAG, "Failed to add date " + selectedDate + " to Firebase: " + task.getException());
                            }
                        });
                    } else {
                        Log.d(TAG, "Date " + selectedDate + " already exists in Firebase.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Database error: " + error.getMessage());
                }
            });
        });
    }
}
