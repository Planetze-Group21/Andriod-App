package com.example.planetzeapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EcoTrackerActivity extends AppCompatActivity {

    private static final String TAG = "EcoTrackerActivity";
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton7;
    private ImageButton imageButton9;
    private ImageButton imageButton10;
    private ImageButton imageButton11;
    private ImageButton imageButton12;
    private TextView energyEmissions;
    private TextView consumptionEmissions;
    private TextView foodEmissions;
    private TextView transportationEmissions;
    private TextView dailyEmissions;
    private Map<TextView, DatabaseReference> textViewDatabaseMap = new HashMap<>();
    private Map<TextView, ValueEventListener> activeListeners = new HashMap<>();
    private String currentUid;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ecotracker), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dailyEmissions = findViewById(R.id.realTimeDailyEmissions);
        transportationEmissions = findViewById(R.id.transportation_text);
        foodEmissions = findViewById(R.id.food_text);
        consumptionEmissions = findViewById(R.id.consumption_text);
        energyEmissions = findViewById(R.id.energy_text);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid);

        if (currentUid == null) {
            Log.e("FirebaseSave", "User is not signed in.");
            return;
        }

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        textViewDatabaseMap.put(dailyEmissions, FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("daily_CO2e"));
        textViewDatabaseMap.put(transportationEmissions, FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Transportation")
                .child("Transportation_CO2e"));
        textViewDatabaseMap.put(foodEmissions, FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Food")
                .child("Food_CO2e"));
        textViewDatabaseMap.put(consumptionEmissions, FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Consumption")
                .child("Consumption_CO2e"));
        textViewDatabaseMap.put(energyEmissions, FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Energy")
                .child("Energy_CO2e"));

        attachAllListeners();

        ensureDailyAnswersDirectory();
        ensureDateSpecificDirectory();
        ensureMonthSpecificDirectory();

        imageButton2 = findViewById(R.id.imageButton2);
        imageButton3 = findViewById(R.id.imageButton3);
        imageButton4 = findViewById(R.id.imageButton4);
        imageButton5 = findViewById(R.id.imageButton5);
        imageButton7 = findViewById(R.id.imageButton7);
        imageButton9 = findViewById(R.id.imageButton9);
        imageButton10 = findViewById(R.id.imageButton10);
        imageButton11 = findViewById(R.id.imageButton11);
        imageButton12 = findViewById(R.id.imageButton12);

        imageButton2.setOnClickListener(view -> openDialog1("Transportation", "Driving", "How many kilometres have you driven?"));
        imageButton3.setOnClickListener(view -> {
            List<String> publicTransportations = Arrays.asList("Bus", "Subway", "Train");
            openDialog2("Transportation", "Public Transport", "Select a public transportation option", publicTransportations);
        });
        imageButton4.setOnClickListener(view -> openDialog1("Transportation", "Walking", "How many kilometres have you walked?"));
        imageButton5.setOnClickListener(view -> {
            List<String> FlightsOptions = Arrays.asList("Long-Haul", "Short-Haul");
            openDialog2("Transportation", "Flights", "Select a flight option", FlightsOptions);
        });
        imageButton7.setOnClickListener(view -> {
            List<String> foodOptions = Arrays.asList("Beef", "Pork", "Chicken", "Fish", "Plant-Based");
            openDialog3("Food", "Select a food option", foodOptions);
        });
        imageButton9.setOnClickListener(view -> openDialog1("Consumption", "Clothing", "How many clothes did you buy?"));
        imageButton10.setOnClickListener(view -> openDialog1("Consumption", "Electronics", "How many electronics did you buy?"));
        imageButton11.setOnClickListener(view -> {
            List<String> purchaseOptions = Arrays.asList("Big", "Small");
            openDialog2("Consumption", "Purchases", "Select a purchase option", purchaseOptions);
        });
        imageButton12.setOnClickListener(view -> {
            List<String> energyOptions = Arrays.asList("Electricity", "Water", "Gas");
            openDialog3("Energy", "Select an energy option", energyOptions);
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HabitTracker());
        transaction.commit();

        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.fragment_container2, new CalendarFragment());
        transaction2.commit();
/*
        if (savedInstanceState == null) {
            FooterFragment footerFragment = new FooterFragment();
            FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
            transaction3.replace(R.id.footer_container, footerFragment); // You can use add() or replace()
            transaction3.commit();
        }

 */

    }

    private void attachAllListeners() {
        for (Map.Entry<TextView, DatabaseReference> entry : textViewDatabaseMap.entrySet()) {
            attachListener(entry.getKey(), entry.getValue());
        }
    }

    private void attachListener(TextView textView, DatabaseReference reference) {
        ValueEventListener listener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object value = snapshot.getValue();
                    Log.d("Firebase", "Value from snapshot: " + value);
                    runOnUiThread(() -> textView.setText(value != null ? value.toString() : "Value not available"));
                } else {
                    runOnUiThread(() -> textView.setText("0"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "DatabaseError: " + error.getMessage());
                runOnUiThread(() -> textView.setText("Error: " + error.getMessage()));
            }
        };

        reference.addValueEventListener(listener);
        activeListeners.put(textView, listener);

        Log.d("FirebaseListener", "Listener attached for: " + reference);
    }

    private void detachAllListeners() {
        for (Map.Entry<TextView, DatabaseReference> entry : textViewDatabaseMap.entrySet()) {
            TextView textView = entry.getKey();
            DatabaseReference reference = entry.getValue();
            ValueEventListener listener = activeListeners.get(textView);
            if (listener != null) {
                reference.removeEventListener(listener);
            }
        }
        activeListeners.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachAllListeners();
    }

    public int getDaysInCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return daysInMonth;
    }

    private void ensureDailyAnswersDirectory() {
        databaseRef.child("daily_answers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    databaseRef.child("daily_answers").setValue("Initialized")
                            .addOnCompleteListener(createTask -> {
                                if (createTask.isSuccessful()) {
                                    Log.d("Firebase", "'daily answers' directory created.");
                                } else {
                                    Log.e("Firebase", "Failed to create 'daily answers': ", createTask.getException());
                                }
                            });
                } else {
                    Log.d("Firebase", "'daily answers' directory already exists.");
                }
            } else {
                Log.e("Firebase", "Error checking 'daily answers' existence: ", task.getException());
            }
        });
    }

    private HashMap<String, Object> createInnerProduct() {
        HashMap<String, Object> innerProduct = new HashMap<>();
        innerProduct.put("value", 0);
        innerProduct.put("emissions", 0);
        return innerProduct;
    }

    private void ensureDateSpecificDirectory() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference dateRef = databaseRef.child("daily_answers").child(todayDate);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    // Check if "Transportation" exists
                    if (!snapshot.hasChild("Transportation")) {
                        Log.d("Firebase", "Transportation node missing for date: " + todayDate);

                        // Create Transportation, Consumption, and Food maps
                        HashMap<String, Object> transportationValues = createTransportationMap();
                        HashMap<String, Object> consumptionValues = createConsumptionMap();
                        HashMap<String, Object> foodValues = createFoodMap();

                        // Add these maps to the existing directory
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("Transportation", transportationValues);
                        updates.put("Consumption", consumptionValues);
                        updates.put("Food", foodValues);

                        dateRef.updateChildren(updates).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Log.d("Firebase", "Missing nodes added for date: " + todayDate);
                                Toast.makeText(this, "Missing entries added for " + todayDate, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("Firebase", "Failed to add missing nodes for " + todayDate, updateTask.getException());
                            }
                        });
                    } else {
                        Log.d("Firebase", "Transportation node exists for date: " + todayDate);
                    }
                } else {
                    // Create the entire structure for the date
                    Log.d("Firebase", "Date-specific directory does not exist for: " + todayDate);
                    HashMap<String, Object> data = createFullDateMap();
                    dateRef.setValue(data).addOnCompleteListener(createTask -> {
                        if (createTask.isSuccessful()) {
                            Log.d("Firebase", "Date-specific directory created for: " + todayDate);
                            Toast.makeText(this, "Entry added for " + todayDate, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Firebase", "Failed to create date-specific directory: ", createTask.getException());
                        }
                    });
                }
            } else {
                Log.e("Firebase", "Error checking date-specific directory existence: ", task.getException());
            }
        });
    }

    private HashMap<String, Object> createTransportationMap() {
        HashMap<String, Object> publicTransportationValues = new HashMap<>();
        publicTransportationValues.put("Bus", createInnerProduct());
        publicTransportationValues.put("Subway", createInnerProduct());
        publicTransportationValues.put("Train", createInnerProduct());

        HashMap<String, Object> flightValues = new HashMap<>();
        flightValues.put("Short-Haul", createInnerProduct());
        flightValues.put("Long-Haul", createInnerProduct());

        HashMap<String, Object> transportationValues = new HashMap<>();
        transportationValues.put("Driving", createInnerProduct());
        transportationValues.put("Walking", createInnerProduct());
        transportationValues.put("Public Transport", publicTransportationValues);
        transportationValues.put("Flights", flightValues);
        transportationValues.put("Transportation_CO2e", 0);

        return transportationValues;
    }
    private HashMap<String, Object> createConsumptionMap() {
        HashMap<String, Object> purchasesValues = new HashMap<>();
        purchasesValues.put("Big", createInnerProduct());
        purchasesValues.put("Small", createInnerProduct());

        HashMap<String, Object> consumptionValues = new HashMap<>();
        consumptionValues.put("Clothing", createInnerProduct());
        consumptionValues.put("Electronics", createInnerProduct());
        consumptionValues.put("Purchases", purchasesValues);
        consumptionValues.put("Consumption_CO2e", 0);

        return consumptionValues;
    }
    private HashMap<String, Object> createFoodMap() {
        HashMap<String, Object> foodValues = new HashMap<>();
        foodValues.put("Beef", createInnerProduct());
        foodValues.put("Pork", createInnerProduct());
        foodValues.put("Fish", createInnerProduct());
        foodValues.put("Chicken", createInnerProduct());
        foodValues.put("Plant-Based", createInnerProduct());
        foodValues.put("Food_CO2e", 0);

        return foodValues;
    }
    private HashMap<String, Object> createEnergyMap() {
        HashMap<String, Object> energyValues = new HashMap<>();
        energyValues.put("Electricity", createInnerProduct());
        energyValues.put("Water", createInnerProduct());
        energyValues.put("Gas", createInnerProduct());
        energyValues.put("Energy_CO2e", 0); // Total emissions for energy
        return energyValues;
    }
    private HashMap<String, Object> createFullDateMap() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Transportation", createTransportationMap());
        data.put("Food", createFoodMap());
        data.put("Consumption", createConsumptionMap());
        data.put("Energy", createEnergyMap());
        data.put("daily_CO2e", 0);

        return data;
    }

    private void ensureMonthSpecificDirectory() {
        String todayDate = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        DatabaseReference dateRef = databaseRef.child("monthly_answers").child(todayDate);

        // Check if the directory for the current month exists
        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    // Create initial data for Water, Gas, and Electricity
                    HashMap<String, Object> energyValues = new HashMap<>();
                    energyValues.put("Water", 0);
                    energyValues.put("Gas", 0);
                    energyValues.put("Electricity", 0);

                    // Write the data to Firebase
                    dateRef.setValue(energyValues).addOnCompleteListener(createTask -> {
                        if (createTask.isSuccessful()) {
                            Log.d("Firebase", "Date-specific directory created for: " + todayDate);
                            Toast.makeText(this, "Entry added for " + todayDate, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Firebase", "Failed to create date-specific directory: ", createTask.getException());
                        }
                    });
                } else {
                    Log.d("Firebase", "Month-specific directory already exists for: " + todayDate);
                    Toast.makeText(this, "Entry for " + todayDate + " already exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firebase", "Error checking month-specific directory existence: ", task.getException());
            }
        });
    }


    private void openDialog1(String category, String activity, String dynamicText) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 600);

        TextView dialogTextView = dialogView.findViewById(R.id.dialogMessage);
        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);
        dialogTextView.setText(dynamicText);

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
                            .child(todayDate)
                            .child(category)
                            .child(activity);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = activity.equals("Driving") ? value * 0.02 :
                                activity.equals("Walking") ? value * 0 :
                                        activity.equals("Clothing") ? value * 360 :
                                                activity.equals("Electronics") ? value * 300 : 0;

                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + activity, Toast.LENGTH_SHORT).show();
                            // You no longer need to update resultText here manually
                            // resultText.setText(String.valueOf(emissions));
                            updateTransportationCO2e(todayDate);
                            updateConsumptionCO2e(todayDate);
                            updateDailyCO2e(todayDate);
                            dialog.dismiss();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EcoTrackerActivity.this, "Failed to save emissions for " + activity + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EcoTrackerActivity.this, "Failed to save value for " + activity + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(EcoTrackerActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EcoTrackerActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


    private void openDialog2(String category, String subCategory, String dialogTitle, List<String> options) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.eco_tracker_dialog_options, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 1200);

        TextView dialogTextView = dialogView.findViewById(R.id.radioMessage);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        // Set the title or message for the dialog
        dialogTextView.setText(dialogTitle);

        // Clear any existing radio buttons and add new ones based on the options
        radioGroup.removeAllViews();
        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setId(View.generateViewId());
            radioButton.setTextSize(16); // Ensures text is legible
            radioButton.setPadding(8, 8, 8, 8); // Adds some spacing
            radioGroup.addView(radioButton);
        }

        saveButton.setOnClickListener(view -> {
            int selectedOptionId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedOptionId);

            if (selectedOptionId == -1) {
                Toast.makeText(EcoTrackerActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedOption = selectedRadioButton.getText().toString();
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
                            .child(todayDate)
                            .child(category)
                            .child(subCategory)
                            .child(selectedOption);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = calculateEmissions2(selectedOption, value);
                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + selectedOption, Toast.LENGTH_SHORT).show();
                            // Remove the manual emissions update here
                            // resultText.setText(String.valueOf(emissions));
                            updateTransportationCO2e(todayDate);
                            updateDailyCO2e(todayDate);
                            dialog.dismiss(); // Ensure dialog dismisses for other options
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EcoTrackerActivity.this, "Failed to save emissions for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EcoTrackerActivity.this, "Failed to save value for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(EcoTrackerActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EcoTrackerActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void openDialog3(String category, String dialogTitle, List<String> options) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.eco_tracker_dialog_options, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 1200);

        TextView dialogTextView = dialogView.findViewById(R.id.radioMessage);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        dialogTextView.setText(dialogTitle);

        // Clear any existing radio buttons and add new ones based on the options
        radioGroup.removeAllViews();
        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setId(View.generateViewId());
            radioButton.setTextSize(16); // Ensures text is legible
            radioButton.setPadding(8, 8, 8, 8); // Adds some spacing
            radioGroup.addView(radioButton);
        }

        saveButton.setOnClickListener(view -> {
            int selectedOptionId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedOptionId);

            if (selectedOptionId == -1) {
                Toast.makeText(EcoTrackerActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedOption = selectedRadioButton.getText().toString();
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
                            .child(todayDate)
                            .child(category)
                            .child(selectedOption);

                    double adjustedValue;
                    if (selectedOption.equals("Electricity") || selectedOption.equals("Water") || selectedOption.equals("Gas")) {
                        adjustedValue = value / getDaysInCurrentMonth();

                        ref.child("value").setValue(adjustedValue).addOnSuccessListener(aVoid -> {
                            double emissions = calculateEmissions(selectedOption, adjustedValue, value);
                            ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + selectedOption, Toast.LENGTH_SHORT).show();

                                updateMonthlyandAddDailyEmissions(category, selectedOption, emissions);
                                updateDailyCO2e(todayDate);

                                dialog.dismiss(); // Dismiss dialog
                            }).addOnFailureListener(e -> {
                                Toast.makeText(EcoTrackerActivity.this, "Failed to save emissions for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EcoTrackerActivity.this, "Failed to save value for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Handle non-energy options (e.g., food)
                        adjustedValue = value;
                        double emissions = calculateEmissions(selectedOption, adjustedValue, value);
                        ref.child("value").setValue(adjustedValue).addOnSuccessListener(aVoid -> {
                            ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + selectedOption, Toast.LENGTH_SHORT).show();

                                updateTransportationCO2e(todayDate);
                                updateFoodCO2e(todayDate);
                                updateConsumptionCO2e(todayDate);
                                updateDailyCO2e(todayDate);
                                dialog.dismiss(); // Ensure dialog dismisses for other options
                            }).addOnFailureListener(e -> {
                                Toast.makeText(EcoTrackerActivity.this, "Failed to save emissions for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EcoTrackerActivity.this, "Failed to save value for " + selectedOption + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(EcoTrackerActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EcoTrackerActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void updateMonthlyandAddDailyEmissions(String category, String subCategory, double emissions) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

        // Get the current year and month
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Month is 0-based
        String monthKey = String.format("%04d-%02d", year, month + 1); // Format YYYY-MM
        int daysInMonth = getDaysInCurrentMonth();

        // Update the monthly emissions in the `monthly_answers` directory
        DatabaseReference monthlyRef = userRef.child("monthly_answers").child(monthKey).child(category).child(subCategory).child("emissions");
        monthlyRef.setValue(emissions).addOnSuccessListener(aVoid -> {
            Log.d("EcoTracker", "Updated monthly emissions for " + subCategory + " to " + emissions);

            // Calculate the daily emissions to propagate
            double dailyEmission = emissions / daysInMonth;

            // Propagate the daily emissions to `daily_answers`
            propagateDailyEmissions(userRef, monthKey, category, subCategory, dailyEmission, daysInMonth);

            // Sync the total daily energy emissions
            syncDailyEnergyCO2e(userRef, monthKey);

        }).addOnFailureListener(e -> {
            Log.e("EcoTracker", "Failed to update monthly emissions for " + subCategory + ": " + e.getMessage());
        });
    }

    private void propagateDailyEmissions(DatabaseReference userRef, String monthKey, String category, String subCategory, double dailyEmission, int daysInMonth) {
        DatabaseReference dailyRef = userRef.child("daily_answers");

        // Loop through all days in the month
        for (int day = 1; day <= daysInMonth; day++) {
            String dayKey = String.format("%s-%02d", monthKey, day); // Format as YYYY-MM-DD
            DatabaseReference dayEmissionRef = dailyRef.child(dayKey).child(category).child(subCategory).child("emissions");

            dayEmissionRef.setValue(dailyEmission).addOnSuccessListener(aVoid -> {
                Log.d("EcoTracker", "Updated daily emissions for " + dayKey + " (" + subCategory + "): " + dailyEmission);
            }).addOnFailureListener(e -> {
                Log.e("EcoTracker", "Failed to update daily emissions for " + dayKey + " (" + subCategory + "): " + e.getMessage());
            });
        }
    }

    private void syncDailyEnergyCO2e(DatabaseReference userRef, String monthKey) {
        DatabaseReference dailyRef = userRef.child("daily_answers");

        // Loop through all days in the current month
        int daysInMonth = getDaysInCurrentMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            String dayKey = String.format("%s-%02d", monthKey, day); // Format as YYYY-MM-DD
            DatabaseReference energyRef = dailyRef.child(dayKey).child("Energy");

            energyRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        double totalEmissions = 0;

                        // Sum up emissions for all subcategories under "Energy"
                        for (DataSnapshot energyCategory : snapshot.getChildren()) {
                            if (energyCategory.hasChild("emissions")) {
                                Double emissions = energyCategory.child("emissions").getValue(Double.class);
                                if (emissions != null) {
                                    totalEmissions += emissions;
                                }
                            }
                        }

                        // Update the "Energy_CO2e" field
                        double finalTotalEmissions = totalEmissions;
                        energyRef.child("Energy_CO2e").setValue(totalEmissions).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Log.d("EcoTracker", "Energy_CO2e updated successfully for " + dayKey + ": " + finalTotalEmissions);
                            } else {
                                Log.e("EcoTracker", "Failed to update Energy_CO2e for " + dayKey + ": ", updateTask.getException());
                            }
                        });
                    } else {
                        Log.e("EcoTracker", "Energy data does not exist for " + dayKey);
                    }
                } else {
                    Log.e("EcoTracker", "Failed to retrieve Energy data for " + dayKey + ": ", task.getException());
                }
            });
        }
    }


    private double calculateEmissions(String selectedOption, double adjustedValue, float value) {
        double emissions = 0;
        switch (selectedOption) {
            case "Beef":
                emissions = adjustedValue * 0.58;
                break;
            case "Pork":
                emissions = adjustedValue * 0.34;
                break;
            case "Chicken":
                emissions = adjustedValue * 0.19;
                break;
            case "Fish":
                emissions = adjustedValue * 0.22;
                break;
            case "Plant-Based":
                emissions = adjustedValue * 0.17;
                break;
            case "Electricity":
                emissions = value < 100 ? Math.round((float) 1450 /(getDaysInCurrentMonth())) : Math.round((float) 2300 /(getDaysInCurrentMonth()));
                break;
            case "Water":
                emissions = value < 100 ? Math.round((float) 1000 /(getDaysInCurrentMonth())) : Math.round((float) 1860 /(getDaysInCurrentMonth()));
                break;
            case "Gas":
                emissions = value < 100 ? Math.round((float) 3300 /(getDaysInCurrentMonth())) : Math.round((float) 4700 /(getDaysInCurrentMonth()));
                break;
        }
        return emissions;
    }

    private double calculateEmissions2(String selectedOption, float value) {
        double emissions = 0;
        switch (selectedOption) {
            case "Bus":
            case "Train":
            case "Subway":
                emissions = value < 5 ? 159.25 : (value >= 5 && value < 10) ? 597.16 : 796.25;;
                break;
            case "Short-Haul":
                emissions = 1600;
                break;
            case "Long-Haul":
                emissions = 4400;
                break;
            case "Big":
                emissions = value*1000;
                break;
            case "Small":
                emissions = value*800;
                break;
        }
        return emissions;
    }

    private void updateTransportationCO2e(String todayDate) {
        DatabaseReference transportationRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Transportation");

        transportationRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    double totalEmissions = 0;

                    // Iterate over the top-level keys in the Transportation node
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        if (key != null) {
                            if (key.equals("Driving") || key.equals("Walking")) {
                                // Add emissions directly for top-level keys
                                Double emissions = child.child("emissions").getValue(Double.class);
                                if (emissions != null) {
                                    totalEmissions += emissions;
                                }
                            } else if (key.equals("Public Transport")) {
                                // Handle nested structure under Public Transport
                                for (DataSnapshot transportMode : child.getChildren()) {
                                    Double emissions = transportMode.child("emissions").getValue(Double.class);
                                    if (emissions != null) {
                                        totalEmissions += emissions;
                                    }
                                }
                            } else if (key.equals("Flights")) {
                                // Add emissions directly from Flights
                                for (DataSnapshot flightType : child.getChildren()) {
                                    Double emissions = flightType.child("emissions").getValue(Double.class);
                                    if (emissions != null) {
                                        totalEmissions += emissions;
                                    }
                                }
                            }
                        }
                    }

                    // Update the Transportation_CO2e value in the database
                    double finalTotalEmissions = totalEmissions;
                    transportationRef.child("Transportation_CO2e").setValue(totalEmissions)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Transportation_CO2e updated successfully: " + finalTotalEmissions))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to update Transportation_CO2e: ", e));
                } else {
                    Log.e(TAG, "Transportation node does not exist for the date: " + todayDate);
                }
            } else {
                Log.e(TAG, "Failed to retrieve Transportation node: ", task.getException());
            }
        });
    }

    private void updateConsumptionCO2e(String todayDate) {
        DatabaseReference consumptionRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Consumption");

        consumptionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    double totalEmissions = 0;

                    // Iterate through top-level keys in "Consumption"
                    for (DataSnapshot category : snapshot.getChildren()) {
                        if (category.hasChild("emissions")) {
                            Double emissions = category.child("emissions").getValue(Double.class);
                            if (emissions != null) {
                                totalEmissions += emissions;
                            }
                        }

                        // Handle nested structure for "Purchases"
                        if (category.getKey().equals("Purchases") && category.hasChildren()) {
                            for (DataSnapshot purchaseType : category.getChildren()) {
                                Double emissions = purchaseType.child("emissions").getValue(Double.class);
                                if (emissions != null) {
                                    totalEmissions += emissions;
                                }
                            }
                        }
                    }

                    // Update the "Consumption_CO2e" field
                    double finalTotalEmissions = totalEmissions;
                    consumptionRef.child("Consumption_CO2e").setValue(totalEmissions).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d("EcoTracker", "Consumption_CO2e updated successfully: " + finalTotalEmissions);
                        } else {
                            Log.e("EcoTracker", "Failed to update Consumption_CO2e: ", updateTask.getException());
                        }
                    });
                } else {
                    Log.e("EcoTracker", "Consumption data does not exist for the given date.");
                }
            } else {
                Log.e("EcoTracker", "Failed to retrieve Consumption data: ", task.getException());
            }
        });
    }

    private void updateFoodCO2e(String todayDate) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate)
                .child("Food");

        foodRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    double totalEmissions = 0;

                    // Iterate through each food category
                    for (DataSnapshot foodCategory : snapshot.getChildren()) {
                        if (foodCategory.hasChild("emissions")) {
                            Double emissions = foodCategory.child("emissions").getValue(Double.class);
                            if (emissions != null) {
                                totalEmissions += emissions;
                            }
                        }
                    }

                    // Update the "Food_CO2e" field
                    double finalTotalEmissions = totalEmissions;
                    foodRef.child("Food_CO2e").setValue(totalEmissions).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d("EcoTracker", "Food_CoOe updated successfully: " + finalTotalEmissions);
                        } else {
                            Log.e("EcoTracker", "Failed to update Food_CO2e: ", updateTask.getException());
                        }
                    });
                } else {
                    Log.e("EcoTracker", "Food data does not exist for the given date.");
                }
            } else {
                Log.e("EcoTracker", "Failed to retrieve Food data: ", task.getException());
            }
        });
    }

    private void updateDailyCO2e(String todayDate) {
        DatabaseReference dailyAnswersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(currentUid)
                .child("daily_answers")
                .child(todayDate);

        dailyAnswersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    double transportationCO2e = snapshot.child("Transportation").child("Transportation_CO2e").getValue(Double.class) != null
                            ? snapshot.child("Transportation").child("Transportation_CO2e").getValue(Double.class)
                            : 0.0;

                    double foodCO2e = snapshot.child("Food").child("Food_CO2e").getValue(Double.class) != null
                            ? snapshot.child("Food").child("Food_CO2e").getValue(Double.class)
                            : 0.0;

                    double consumptionCO2e = snapshot.child("Consumption").child("Consumption_CO2e").getValue(Double.class) != null
                            ? snapshot.child("Consumption").child("Consumption_CO2e").getValue(Double.class)
                            : 0.0;

                    double energyCO2e = snapshot.child("Energy").child("Energy_CO2e").getValue(Double.class) != null
                            ? snapshot.child("Energy").child("Energy_CO2e").getValue(Double.class)
                            : 0.0;

                    // Calculate the daily total
                    double dailyCO2e = transportationCO2e + foodCO2e + consumptionCO2e + energyCO2e;

                    // Update the "daily_CO2e" field
                    dailyAnswersRef.child("daily_CO2e").setValue(dailyCO2e).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d("EcoTracker", "daily_CO2e updated successfully: " + dailyCO2e);
                        } else {
                            Log.e("EcoTracker", "Failed to update daily_CO2e: ", updateTask.getException());
                        }
                    });
                } else {
                    Log.e("EcoTracker", "Daily answers data does not exist for the given date.");
                }
            } else {
                Log.e("EcoTracker", "Failed to retrieve daily answers data: ", task.getException());
            }
        });
    }





}