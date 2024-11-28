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
            if(questions.get(1).getUserAnswer().equals("I don’t know"))
                transportationData.put("car_type", "idk"); // Q1
            else
                transportationData.put("car_type", questions.get(1).getUserAnswer().toLowerCase()); // Q1
            if(questions.get(2).getOptions()[0].equals(questions.get(2).getUserAnswer()))
                transportationData.put("distance","5,000"); // Q2
            else if(questions.get(2).getOptions()[1].equals(questions.get(2).getUserAnswer()))
                transportationData.put("distance","5,000-10,000"); // Q2
            else if(questions.get(2).getOptions()[2].equals(questions.get(2).getUserAnswer()))
                transportationData.put("distance","10,000-15,000"); // Q2
            else if(questions.get(2).getOptions()[3].equals(questions.get(2).getUserAnswer()))
                transportationData.put("distance","15,000-20,000"); // Q2
            else if(questions.get(2).getOptions()[4].equals(questions.get(2).getUserAnswer()))
                transportationData.put("distance","20,00-25,000"); // Q2
            else
                transportationData.put("distance","25,000+"); // Q2
        }
        else
        {
            transportationData.put("uses_car", false); // Q0
            transportationData.put("car_type", "none"); // Q1
            transportationData.put("distance", "0"); // Q2
        }

        if(questions.get(3).getOptions()[0].equals(questions.get(3).getUserAnswer()))
            transportationData.put("public_transit_use","never"); // Q3
        else if(questions.get(3).getOptions()[1].equals(questions.get(2).getUserAnswer()))
            transportationData.put("public_transit_use","occasionally"); // Q3
        else if(questions.get(3).getOptions()[2].equals(questions.get(2).getUserAnswer()))
            transportationData.put("public_transit_use","frequently"); // Q3
        else
            transportationData.put("public_transit_use","always"); // Q3

        if(questions.get(4).getOptions()[0].equals(questions.get(4).getUserAnswer()))
            transportationData.put("hours_on_public_transit", "1"); // Q4
        else if(questions.get(4).getOptions()[1].equals(questions.get(4).getUserAnswer()))
            transportationData.put("hours_on_public_transit", "2-3"); // Q4
        else if(questions.get(4).getOptions()[2].equals(questions.get(4).getUserAnswer()))
            transportationData.put("hours_on_public_transit", "4-5"); // Q4
        else if(questions.get(4).getOptions()[3].equals(questions.get(4).getUserAnswer()))
            transportationData.put("hours_on_public_transit", "6-10"); // Q4
        else
            transportationData.put("hours_on_public_transit", "10+"); // Q4


        if (questions.get(5).getOptions()[0].equals(questions.get(5).getUserAnswer())) {
            transportationData.put("short_haul_flights", "0");
        } else if (questions.get(5).getOptions()[1].equals(questions.get(5).getUserAnswer())) {
            transportationData.put("short_haul_flights", "1-2");
        } else if (questions.get(5).getOptions()[2].equals(questions.get(5).getUserAnswer())) {
            transportationData.put("short_haul_flights", "3-5");
        } else if (questions.get(5).getOptions()[3].equals(questions.get(5).getUserAnswer())) {
            transportationData.put("short_haul_flights", "6-10");
        } else {
            transportationData.put("short_haul_flights", "10+");
        }

        if (questions.get(6).getOptions()[0].equals(questions.get(6).getUserAnswer())) {
            transportationData.put("long_haul_flights", "0");
        } else if (questions.get(6).getOptions()[1].equals(questions.get(6).getUserAnswer())) {
            transportationData.put("long_haul_flights", "1-2");
        } else if (questions.get(6).getOptions()[2].equals(questions.get(6).getUserAnswer())) {
            transportationData.put("long_haul_flights", "3-5");
        } else if (questions.get(6).getOptions()[3].equals(questions.get(6).getUserAnswer())) {
            transportationData.put("long_haul_flights", "6-10");
        } else {
            transportationData.put("long_haul_flights", "10+");         }

        int transportationCo2e = transport_calc();
        transportationData.put("transportation_co2e", transportationCo2e);

        Map<String, Object> foodData = new HashMap<>();

        if (questions.get(7).getOptions()[0].equals(questions.get(7).getUserAnswer())) {
            foodData.put("diet_type", "vegetarian");
        } else if (questions.get(7).getOptions()[1].equals(questions.get(7).getUserAnswer())) {
            foodData.put("diet_type", "vegan");
        } else if (questions.get(7).getOptions()[2].equals(questions.get(7).getUserAnswer())) {
            foodData.put("diet_type", "pescatarian");
        } else
            foodData.put("diet_type", "meat_based");
        if(questions.get(7).getUserAnswer().equals("Meat-based (eat all types of animal products)")) {
            if (questions.get(8).getOptions()[0].equals(questions.get(8).getUserAnswer())) {
                foodData.put("beef_frequency", "daily");
            } else if (questions.get(8).getOptions()[1].equals(questions.get(8).getUserAnswer())) {
                foodData.put("beef_frequency", "frequently");
            } else if (questions.get(8).getOptions()[2].equals(questions.get(8).getUserAnswer())) {
                foodData.put("beef_frequency", "occasionally");
            } else
                foodData.put("beef_frequency", "never");

            if (questions.get(9).getOptions()[0].equals(questions.get(9).getUserAnswer())) {
                foodData.put("pork_frequency", "daily");
            } else if (questions.get(9).getOptions()[1].equals(questions.get(9).getUserAnswer())) {
                foodData.put("pork_frequency", "frequently");
            } else if (questions.get(9).getOptions()[2].equals(questions.get(9).getUserAnswer())) {
                foodData.put("pork_frequency", "occasionally");
            } else
                foodData.put("pork_frequency", "never");


            if (questions.get(10).getOptions()[0].equals(questions.get(10).getUserAnswer())) {
                foodData.put("chicken_frequency", "daily");
            } else if (questions.get(10).getOptions()[1].equals(questions.get(10).getUserAnswer())) {
                foodData.put("chicken_frequency", "frequently");
            } else if (questions.get(10).getOptions()[2].equals(questions.get(10).getUserAnswer())) {
                foodData.put("chicken_frequency", "occasionally");
            } else
                foodData.put("chicken_frequency", "never");

            if (questions.get(11).getOptions()[0].equals(questions.get(11).getUserAnswer())) {
                foodData.put("fish_frequency", "daily");
            } else if (questions.get(11).getOptions()[1].equals(questions.get(11).getUserAnswer())) {
                foodData.put("fish_frequency", "frequently");
            } else if (questions.get(11).getOptions()[2].equals(questions.get(11).getUserAnswer())) {
                foodData.put("fish_frequency", "occasionally");
            } else
                foodData.put("fish_frequency", "never");

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

        if (questions.get(13).getOptions()[0].equals(questions.get(13).getUserAnswer())) {
            houseData.put("home_type", "detached");
        } else if (questions.get(13).getOptions()[1].equals(questions.get(13).getUserAnswer())) {
            houseData.put("home_type", "semi-detached ");
        } else if (questions.get(13).getOptions()[2].equals(questions.get(13).getUserAnswer())) {
            houseData.put("home_type", "townhouse");
        } else if (questions.get(13).getOptions()[3].equals(questions.get(13).getUserAnswer())) {
            houseData.put("home_type", "condo_apartment");
        } else if (questions.get(13).getOptions()[4].equals(questions.get(13).getUserAnswer())) {
            houseData.put("home_type", "other");
        }


        if (questions.get(14).getOptions()[0].equals(questions.get(14).getUserAnswer())) {
            houseData.put("household_members", "1");
        } else if (questions.get(14).getOptions()[1].equals(questions.get(14).getUserAnswer())) {
            houseData.put("household_members", "2");
        } else if (questions.get(14).getOptions()[2].equals(questions.get(14).getUserAnswer())) {
            houseData.put("household_members", "3-4");
        } else if (questions.get(14).getOptions()[3].equals(questions.get(14).getUserAnswer())) {
            houseData.put("household_members", "5+");
        }

        if (questions.get(15).getOptions()[0].equals(questions.get(15).getUserAnswer())) {
            houseData.put("size", "0-1000");
        } else if (questions.get(15).getOptions()[1].equals(questions.get(15).getUserAnswer())) {
            houseData.put("size", "1000-2000");
        } else if (questions.get(15).getOptions()[2].equals(questions.get(15).getUserAnswer())) {
            houseData.put("size", "2000+");
        }
        if (questions.get(16).getOptions()[0].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "natural_gas");
        } else if (questions.get(16).getOptions()[1].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "electricity");
        } else if (questions.get(16).getOptions()[2].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "oil");
        } else if (questions.get(16).getOptions()[3].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "propane");
        } else if (questions.get(16).getOptions()[4].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "wood");
        } else if (questions.get(16).getOptions()[5].equals(questions.get(16).getUserAnswer())) {
            houseData.put("heating_source", "other");
        }
        if (questions.get(17).getOptions()[0].equals(questions.get(17).getUserAnswer())) {
            houseData.put("bill", "0-50");
        } else if (questions.get(17).getOptions()[1].equals(questions.get(17).getUserAnswer())) {
            houseData.put("bill", "50-100");
        } else if (questions.get(17).getOptions()[2].equals(questions.get(17).getUserAnswer())) {
            houseData.put("bill", "100-150");
        } else if (questions.get(17).getOptions()[3].equals(questions.get(17).getUserAnswer())) {
            houseData.put("bill", "150-200");
        } else if (questions.get(17).getOptions()[4].equals(questions.get(17).getUserAnswer())) {
            houseData.put("bill", "200+");
        }


        if (questions.get(18).getOptions()[0].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "natural_gas");
        } else if (questions.get(18).getOptions()[1].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "electricity");
        } else if (questions.get(18).getOptions()[2].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "oil");
        } else if (questions.get(18).getOptions()[3].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "propane");
        } else if (questions.get(18).getOptions()[4].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "solar");
        } else if (questions.get(18).getOptions()[5].equals(questions.get(18).getUserAnswer())) {
            houseData.put("water_heating_energy", "other");
        }


        if (questions.get(19).getOptions()[0].equals(questions.get(19).getUserAnswer())) {
            houseData.put("renewable_energy_usage", "primarily");
        } else if (questions.get(19).getOptions()[1].equals(questions.get(19).getUserAnswer())) {
            houseData.put("renewable_energy_usage", "partially");
        } else if (questions.get(19).getOptions()[2].equals(questions.get(19).getUserAnswer())) {
            houseData.put("renewable_energy_usage", "no");
        }


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

        if (questions.get(21).getOptions()[0].equals(questions.get(21).getUserAnswer())) {
            consumptionData.put("eco_friendly_frequency", "regularly");
        } else if (questions.get(21).getOptions()[1].equals(questions.get(21).getUserAnswer())) {
            consumptionData.put("eco_friendly_frequency", "occasionally");
        } else if (questions.get(21).getOptions()[2].equals(questions.get(21).getUserAnswer())) {
            consumptionData.put("eco_friendly_frequency", "no");
        }

        if (questions.get(22).getOptions()[0].equals(questions.get(22).getUserAnswer())) {
            consumptionData.put("device_frequency", "none");
        } else if (questions.get(22).getOptions()[1].equals(questions.get(22).getUserAnswer())) {
            consumptionData.put("device_frequency", "1");
        } else if (questions.get(22).getOptions()[2].equals(questions.get(22).getUserAnswer())) {
            consumptionData.put("device_frequency", "2");
        } else if (questions.get(22).getOptions()[3].equals(questions.get(22).getUserAnswer())) {
            consumptionData.put("device_frequency", "3");
        }


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