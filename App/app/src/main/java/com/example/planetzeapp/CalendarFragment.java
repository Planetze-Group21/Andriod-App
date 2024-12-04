package com.example.planetzeapp;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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

        ImageButton editButton1;
        ImageButton editButton2;
        ImageButton editButton3;
        ImageButton editButton4;
        ImageButton editButton5;
        ImageButton editButton6;
        ImageButton editButton7;
        ImageButton editButton8;
        ImageButton editButton9;
        ImageButton editButton10;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid).child("daily_answers");

        if (calendarView != null) {
            calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
                String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                ensureDateSpecificDirectory(selectedDate);

                showPopup(calendarView);



            });
        } else {
            Log.e("CalendarFragment", "CalendarView not found in the layout.");
        }
        return view;
    }

    private void showPopup(CalendarView calendarView) {
    }

    private HashMap<String, Object> createInnerProduct() {
        HashMap<String, Object> innerProduct = new HashMap<>();
        innerProduct.put("value", 0);
        innerProduct.put("emissions", 0);
        return innerProduct;
    }

    private void showPopup(String selectedDate, String category, String activity, String dynamicTest) {
        // Create a custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_eco_tracker); // The XML layout you created
        dialog.setCancelable(true);

        // Get references to the EditText and Button
        EditText valueInput = dialog.findViewById(R.id.editText1);
        Button saveButton = dialog.findViewById(R.id.editButton1);

        // Set up the button click listener
        saveButton.setOnClickListener(view -> {
            String userInput = editText.getText().toString().trim();

            if (!userInput.isEmpty()) {
                try {
                    float value = Float.parseFloat(userInput);

                    if (value <= 0) {
                        Toast.makeText(this, "Value must be positive", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUid)
                            .child("daily_answers")
                            .child(selectedDate)
                            .child(category)
                            .child(activity);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = activity.equals("Driving") ? value * 0.02 :
                                activity.equals("Walking") ? value * 0 :
                                        activity.equals("Clothing") ? value * 360 :
                                                activity.equals("Electronics") ? value * 300 : 0;

                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(CalendarFragment.this, "Data saved successfully for " + activity, Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(CalendarFragment.this, "Failed to save emissions for " + activity + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CalendarFragment.this, "Failed to save value for " + activity + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(CalendarFragment.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CalendarFragment.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void ensureDateSpecificDirectory(String selectedDate) {
        DatabaseReference dateRef = databaseRef.child(selectedDate);

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
                            Log.d(TAG, "Date " + selectedDate + " added successfully with default data to Firebase.");
                        } else {
                            Log.e(TAG, "Failed to add default data for date " + selectedDate + ": ", task2.getException());
                        }
                    });
                } else {
                    Log.d(TAG, "Date " + selectedDate + " already exists in Firebase.");
                }
            } else {
                Log.e(TAG, "Failed to check existence for date " + selectedDate + ": ", task.getException());
            }
        });
    }

    public void storeDataInCategory(String selectedDate, String category, String subcategory, Object value) {
        // Get the reference to the database path for the specific selectedDate, category, and subcategory
        DatabaseReference dateRef = databaseRef.child(selectedDate).child(category).child(subcategory);

        // Set the value at the specified location in Firebase
        dateRef.setValue(value).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Successfully stored data for " + category + " -> " + subcategory + " on " + selectedDate);
            } else {
                Log.e(TAG, "Failed to store data for " + category + " -> " + subcategory + " on " + selectedDate, task.getException());
            }
        });
    }
}
