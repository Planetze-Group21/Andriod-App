package com.example.planetzeapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CalendarActivity extends Fragment {

    View view;
    private CalendarView calendarView;
    private String currentUid;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_eco_tracker_answers, container, false);

        // Find views by ID
        calendarView = view.findViewById(R.id.calendar_view);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid).child("daily answers");

        // Set listener for calendar interactions
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

            ensureDateSpecificDirectory(selectedDate);
        });
        return view;
    }


    private void ensureDateSpecificDirectory(String date) {
        DatabaseReference dateRef = databaseRef.child(date);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    // Create the HashMap structure
                    HashMap<String, Object> InnerProduct = new HashMap<>();
                    InnerProduct.put("value", 0);
                    InnerProduct.put("emissions", 0);

                    HashMap<String, Object> PublicTransportationValues = new HashMap<>();
                    PublicTransportationValues.put("Bus", InnerProduct);
                    PublicTransportationValues.put("Subway", InnerProduct);
                    PublicTransportationValues.put("Train", InnerProduct);

                    HashMap<String, Object> FlightValues = new HashMap<>();
                    FlightValues.put("Short-Haul", 0);
                    FlightValues.put("Long-Haul", 0);

                    HashMap<String, Object> TransportationValues = new HashMap<>();
                    TransportationValues.put("Driving", InnerProduct);
                    TransportationValues.put("Walking", InnerProduct);
                    TransportationValues.put("Public Transport", PublicTransportationValues);
                    TransportationValues.put("Flights", FlightValues);
                    TransportationValues.put("Transportation_Co2e", 0);

                    HashMap<String, Object> FoodValues = new HashMap<>();
                    FoodValues.put("Beef", InnerProduct);
                    FoodValues.put("Pork", InnerProduct);
                    FoodValues.put("Fish", InnerProduct);
                    FoodValues.put("Chicken", InnerProduct);
                    FoodValues.put("Plant-Based", InnerProduct);
                    FoodValues.put("Food_Co2e", 0);

                    HashMap<String, Object> PurchasesValues = new HashMap<>();
                    PurchasesValues.put("Big", InnerProduct);
                    PurchasesValues.put("Small", InnerProduct);

                    HashMap<String, Object> ConsumptionValues = new HashMap<>();
                    ConsumptionValues.put("Clothing", InnerProduct);
                    ConsumptionValues.put("Electronics", InnerProduct);
                    ConsumptionValues.put("Purchases", PurchasesValues);
                    ConsumptionValues.put("Consumption_CO2e", 0);

                    HashMap<String, Object> EnergyValues = new HashMap<>();
                    EnergyValues.put("Electricity", InnerProduct);
                    EnergyValues.put("Water", InnerProduct);
                    EnergyValues.put("Gas", InnerProduct);
                    EnergyValues.put("Energy_CO2e", 0);

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("Transportation", TransportationValues);
                    data.put("Food", FoodValues);
                    data.put("Consumption", ConsumptionValues);
                    data.put("Energy", EnergyValues);
                    data.put("daily_CO2e", 0);

                    // Add the HashMap to the database
                    dateRef.setValue(data).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Log.d(TAG, "Date " + date + " added successfully with default data to Firebase.");
                        } else {
                            Log.e(TAG, "Failed to add default data for date " + date + ": ", task2.getException());
                        }
                    });
                } else {
                    Log.d(TAG, "Date " + date + " already exists in Firebase.");
                }
            } else {
                Log.e(TAG, "Failed to check existence for date " + date + ": ", task.getException());
            }
        });
    }
}
