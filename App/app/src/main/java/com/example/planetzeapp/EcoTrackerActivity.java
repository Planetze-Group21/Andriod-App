package com.example.planetzeapp;

import android.app.Dialog;
import android.util.Log;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = database.getReference("users").child(currentUid);

        if (currentUid == null) {
            Log.e("FirebaseSave", "User is not signed in.");
            return;
        }

        ensureDailyAnswersDirectory();
        ensureDateSpecificDirectory();

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

        imageButton2.setOnClickListener(view -> openDialog1("Transportation", "Driving", resultText2));
        imageButton3.setOnClickListener(view -> openDialog2("Transportation", "Public Transportation", resultText3));
        imageButton4.setOnClickListener(view -> openDialog1("Transportation", "Walking", resultText4));
        imageButton5.setOnClickListener(view -> openDialog2("Transportation", "Flights", resultText5));
        imageButton7.setOnClickListener(view -> openDialog3( "Food", resultText7));
        imageButton9.setOnClickListener(view -> openDialog1("Consumption", "Clothes", resultText9));
        imageButton10.setOnClickListener(view -> openDialog1("Consumption", "Electronics", resultText10));
        imageButton11.setOnClickListener(view -> openDialog2("Consumption", "Purchases", resultText11));
        imageButton12.setOnClickListener(view -> openDialog3("Energy", resultText12));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HabitTracker());
        transaction.commit();
    }

    private void ensureDailyAnswersDirectory() {
        databaseRef.child("daily answers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    databaseRef.child("daily answers").setValue("Initialized")
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

    private void ensureDateSpecificDirectory() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference dateRef = databaseRef.child("daily answers").child(todayDate);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
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
                    FlightValues.put("Big", InnerProduct);
                    FlightValues.put("Small", InnerProduct);

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

                    dateRef.setValue(data).addOnCompleteListener(createTask -> {
                        if (createTask.isSuccessful()) {
                            Log.d("Firebase", "Date-specific directory created for: " + todayDate);
                            Toast.makeText(this, "Entry added for " + todayDate, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Firebase", "Failed to create date-specific directory: ", createTask.getException());
                        }
                    });
                } else {
                    Log.d("Firebase", "Date-specific directory already exists for: " + todayDate);
                    Toast.makeText(this, "Entry for " + todayDate + " already exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firebase", "Error checking date-specific directory existence: ", task.getException());
            }
        });
    }


    private void openDialog1(String category, String activity, TextView resultText) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 600);

        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

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
                            .child("daily answers")
                            .child(todayDate)
                            .child(category)
                            .child(activity);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = activity.equals("Driving") ? value * 0.02 :
                                activity.equals("Walking") ? value * 0 :
                                        activity.equals("Clothes") ? value * 360 :
                                                activity.equals("Electronics") ? value * 300 : 0;

                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + activity, Toast.LENGTH_SHORT).show();
                            resultText.setText(String.valueOf(emissions));
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

    private void openDialog2(String category, String activity, TextView resultText) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.eco_tracker_dialog_options, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 600);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

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
                            .child("daily answers")
                            .child(todayDate)
                            .child(category)
                            .child(activity)
                            .child(selectedOption);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = 0;
                        if (selectedOption.equals("Bus")) {
                            if (value < 5) {
                                emissions = 159.25;
                            } else if ((5 <= value) && (value <= 10)) {
                                emissions = 597.16;
                            } else if (value > 10) {
                                emissions = 796.25;
                            }
                        } else if (selectedOption.equals("Subway")) {
                            if (value < 5) {
                                emissions = 159.25;
                            } else if ((5 <= value) && (value <= 10)) {
                                emissions = 597.16;
                            } else if (value > 10) {
                                emissions = 796.25;
                            }
                        } else if (selectedOption.equals("Train")) {
                            if (value < 5) {
                                emissions = 159.25;
                            } else if ((5 <= value) && (value <= 10)) {
                                emissions = 597.16;
                            } else if (value > 10) {
                                emissions = 796.25;
                            }
                        } else if (selectedOption.equals("Short")) {
                            emissions = 1600;
                        } else if (selectedOption.equals("Long")) {
                            emissions = 4400;
                        } else if (selectedOption.equals("Small")) {
                            emissions = value*800;
                        } else if (selectedOption.equals("Large")) {
                            emissions = value*1000;
                        }

                        double finalEmissions = emissions;
                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + activity, Toast.LENGTH_SHORT).show();
                            resultText.setText(String.valueOf(finalEmissions));
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

    private void openDialog3(String category, TextView resultText) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dialog dialog = new Dialog(EcoTrackerActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.eco_tracker_dialog_options, null);
        dialog.setContentView(dialogView);

        dialog.getWindow().setLayout(800, 600);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

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
                            .child("daily answers")
                            .child(todayDate)
                            .child(category)
                            .child(selectedOption);

                    ref.child("value").setValue(value).addOnSuccessListener(aVoid -> {
                        double emissions = 0;
                        if (selectedOption.equals("Beef")) {
                            emissions = value*0.58;
                        } else if (selectedOption.equals("Pork")) {
                            emissions = value*0.34;
                        } else if (selectedOption.equals("Chicken")) {
                            emissions = value*0.19;
                        } else if (selectedOption.equals("Fish")) {
                            emissions = value*0.22;
                        } else if (selectedOption.equals("Plant-Based")) {
                            emissions = value*0.17;
                        } else if (selectedOption.equals("Electricity")) {
                            if (value < 100) {
                                emissions = 1450;
                            } else if (value > 100) {
                                emissions = 2300;
                            }
                        } else if (selectedOption.equals("Water")) {
                            if (value < 100) {
                                emissions = 1000;
                            } else if (value > 100) {
                                emissions = 1860;
                            }
                        } else if (selectedOption.equals("Gas")) {
                            if (value < 100) {
                                emissions = 3300;
                            } else if (value > 100) {
                                emissions = 4700;
                            }
                        }
                        double finalEmissions = emissions;
                        ref.child("emissions").setValue(emissions).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(EcoTrackerActivity.this, "Data saved successfully for " + selectedOption, Toast.LENGTH_SHORT).show();
                            resultText.setText(String.valueOf(finalEmissions));
                            dialog.dismiss();
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
}