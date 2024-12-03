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

import java.util.HashMap;

public class CalendarFragment extends Fragment {

    View view;
    private CalendarView calendarView;
    private String currentUid;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar_view, container, false);

        // Find views by ID
        calendarView = view.findViewById(R.id.calendar_view);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid).child("daily_answers");

        if (calendarView != null) {
            calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
                String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                ensureDateSpecificDirectory(selectedDate);
            });
        } else {
            Log.e("CalendarFragment", "CalendarView not found in the layout.");
        }
        return view;
    }

    private HashMap<String, Object> createInnerProduct() {
        HashMap<String, Object> innerProduct = new HashMap<>();
        innerProduct.put("value", 0);
        innerProduct.put("emissions", 0);
        return innerProduct;
    }


    private void ensureDateSpecificDirectory(String date) {
        DatabaseReference dateRef = databaseRef.child(date);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    HashMap<String, Object> PublicTransportationValues = new HashMap<>();
                    PublicTransportationValues.put("Bus", createInnerProduct());
                    PublicTransportationValues.put("Subway", createInnerProduct());
                    PublicTransportationValues.put("Train", createInnerProduct());

                    HashMap<String, Object> FlightValues = new HashMap<>();
                    FlightValues.put("Short-Haul", 0);
                    FlightValues.put("Long-Haul", 0);

                    HashMap<String, Object> TransportationValues = new HashMap<>();
                    TransportationValues.put("Driving", createInnerProduct());
                    TransportationValues.put("Walking", createInnerProduct());
                    TransportationValues.put("Public Transport", PublicTransportationValues);
                    TransportationValues.put("Flights", FlightValues);
                    TransportationValues.put("Transportation_Co2e", 0);

                    HashMap<String, Object> FoodValues = new HashMap<>();
                    FoodValues.put("Beef", createInnerProduct());
                    FoodValues.put("Pork", createInnerProduct());
                    FoodValues.put("Fish", createInnerProduct());
                    FoodValues.put("Chicken", createInnerProduct());
                    FoodValues.put("Plant-Based", createInnerProduct());
                    FoodValues.put("Food_Co2e", 0);

                    HashMap<String, Object> PurchasesValues = new HashMap<>();
                    PurchasesValues.put("Big", createInnerProduct());
                    PurchasesValues.put("Small", createInnerProduct());

                    HashMap<String, Object> ConsumptionValues = new HashMap<>();
                    ConsumptionValues.put("Clothing", createInnerProduct());
                    ConsumptionValues.put("Electronics", createInnerProduct());
                    ConsumptionValues.put("Purchases", PurchasesValues);
                    ConsumptionValues.put("Consumption_CO2e", 0);

                    HashMap<String, Object> EnergyValues = new HashMap<>();
                    EnergyValues.put("Electricity", createInnerProduct());
                    EnergyValues.put("Water", createInnerProduct());
                    EnergyValues.put("Gas", createInnerProduct());
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
