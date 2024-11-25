package com.example.planetzeapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnualQs_Finish extends QFragment {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.annual_finish, container, false);
        Button startButton = view.findViewById(R.id.viewResultsButton);
        startButton.setEnabled(false);
        EditText countryInput = view.findViewById(R.id.countryInput);
        countryInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startButton.setEnabled(!s.toString().trim().isEmpty());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        startButton.setOnClickListener(v -> {
            String country = countryInput.getText().toString().trim();
            if (country.isEmpty()) {
                // Optionally show a message to the user if the country is not entered
                Toast.makeText(getContext(), "Please enter a country.", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users").child(uid);
            ref.child("country").setValue(country)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseSave", "Country saved successfully for user: " + uid);
                        } else {
                            Log.e("FirebaseSave", "Failed to save country for user: " + uid, task.getException());
                        }
                    });
            annual_info();
            navigateToQFragment();        });
        return view;

    }
    private void navigateToQFragment() {
        Temp t = new Temp();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, t);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void annual_info() {
        if (uid == null) {
            Log.e("FirebaseSave", "User is not signed in.");
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(uid).child("annual_answers");
        Map<String, Object> annualData = new HashMap<>();

        Map<String, Object> transportationData = new HashMap<>();
        if(questions.get(0).getUserAnswer().equals("True"))
        {
            transportationData.put("uses_car", true); // Q0
            transportationData.put("car_type", questions.get(1).getUserAnswer().toLowerCase()); // Q1
            transportationData.put("distance", questions.get(2).getUserAnswer().toLowerCase()); // Q2
        }
        else
        {
            transportationData.put("uses_car", false); // Q0
            transportationData.put("car_type", "none"); // Q1
            transportationData.put("distance", "0"); // Q2
        }
        transportationData.put("public_transit_use", questions.get(3).getUserAnswer().toLowerCase()); // Q3
        transportationData.put("hours_on_public_transit", questions.get(4).getUserAnswer().toLowerCase()); // Q4
        transportationData.put("num_short_flights", questions.get(5).getUserAnswer().toLowerCase()); // Q5
        transportationData.put("num_long_flights", questions.get(6).getUserAnswer().toLowerCase()); // Q6

        int transportationCo2e = transport_calc();
        transportationData.put("transportation_co2e", transportationCo2e);

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("diet_type", questions.get(7).getUserAnswer().toLowerCase()); // Q7
        if(questions.get(7).getUserAnswer().equals("Meat-based (eat all types of animal products)")) {
            foodData.put("beef_frequency", questions.get(8).getUserAnswer().toLowerCase()); // Q8
            foodData.put("pork_frequency", questions.get(9).getUserAnswer().toLowerCase()); // Q9
            foodData.put("chicken_frequency", questions.get(10).getUserAnswer().toLowerCase()); // Q10
            foodData.put("fish_frequency", questions.get(11).getUserAnswer().toLowerCase()); // Q11
        }
        else {
            foodData.put("beef_frequency", "never"); // Q8
            foodData.put("pork_frequency", "never"); // Q9
            foodData.put("chicken_frequency", "never"); // Q10
            foodData.put("fish_frequency", "never"); // Q11
        }
        foodData.put("food_waste_frequency", questions.get(12).getUserAnswer().toLowerCase()); // Q12
        int foodCo2e = food_calc();
        foodData.put("food_co2e", foodCo2e);
        ref.setValue(foodData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseSave", "Food data saved successfully for user: " + uid);
                    } else {
                        Log.e("FirebaseSave", "Failed to save food data for user: " + uid, task.getException());
                    }
                });

        Map<String, Object> houseData = new HashMap<>();
        houseData.put("structure_type", questions.get(13).getUserAnswer().toLowerCase()); // Q13
        houseData.put("household_members", questions.get(14).getUserAnswer().toLowerCase()); // Q14
        houseData.put("size", questions.get(15).getUserAnswer().toLowerCase()); // Q15
        houseData.put("heating_source", questions.get(16).getUserAnswer().toLowerCase()); // Q16
        houseData.put("bill", questions.get(17).getUserAnswer().toLowerCase()); // Q17
        houseData.put("water_source", questions.get(18).getUserAnswer().toLowerCase()); // Q18
        houseData.put("renewable_energy_uasge", questions.get(19).getUserAnswer().toLowerCase()); // Q19
        int housingCo2e = house_calc();
        houseData.put("house_co2e", housingCo2e);
        ref.setValue(houseData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseSave", "Housing data saved successfully for user: " + uid);
                    } else {
                        Log.e("FirebaseSave", "Failed to save housing data for user: " + uid, task.getException());
                    }
                });

        Map<String, Object> consumptionData = new HashMap<>();
        consumptionData.put("clothing_frequency", questions.get(20).getUserAnswer().toLowerCase()); // Q20
        consumptionData.put("eco_friendly_frequency", questions.get(21).getUserAnswer().toLowerCase()); // Q21
        consumptionData.put("device_frequency", questions.get(22).getUserAnswer().toLowerCase()); // Q22
        consumptionData.put("recycling_frequency", questions.get(23).getUserAnswer().toLowerCase()); // Q23
        int consumptionCo2e = cons_calc();
        consumptionData.put("consumption_co2e", consumptionCo2e);
        ref.setValue(consumptionData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseSave", "food data saved successfully for user: " + uid);
                    } else {
                        Log.e("FirebaseSave", "Failed to save food data for user: " + uid, task.getException());
                    }
                });

        annualData.put("transportation", transportationData);
        annualData.put("food", foodData);
        annualData.put("housing", houseData);
        annualData.put("consumption", consumptionData);
        int annualco2e = annual_calc();
        annualData.put("annual_co2e", annualco2e);
        annualData.put("annual_co2e",annualco2e);
        ref.setValue(annualData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseSave", "Annual data saved successfully for user: " + uid);
                    } else {
                        Log.e("FirebaseSave", "Failed to save annual data for user: " + uid, task.getException());
                    }
                });


    }

    private int transport_calc() {
        int co2e = 0;
        return co2e;
    }
    private int food_calc() {
        int co2e = 0;
        return co2e;
    }
    private int cons_calc() {
        int co2e = 0;
        return co2e;
    }
    private int house_calc() {
        int co2e = 0;
        return co2e;
    }
    private int annual_calc() {
        int co2e = 0;
        return co2e;
    }


}