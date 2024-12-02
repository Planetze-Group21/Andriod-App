package com.example.planetzeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HabitTracker extends Fragment {
    int Walk_counter = 0, No_shopping_counter = 0, Vegan_eating_counter = 0;
    String Energy_tracker;
    View view;
    CheckBox transportationCheck = view.findViewById(R.id.filterTransportation);
    CheckBox foodCheck = view.findViewById(R.id.filterFood);
    CheckBox consumptionCheck = view.findViewById(R.id.filterConsumption);
    CheckBox energyCheck = view.findViewById(R.id.filterEnergy);
    View transportationHabitbox = view.findViewById(R.id.transportationSection);
    View foodHabitbox = view.findViewById(R.id.foodSection);
    View energyHabitbox = view.findViewById(R.id.energySection);
    View consumptionHabitbox = view.findViewById(R.id.consumptionSection);
    TextView transportationHabit = view.findViewById(R.id.habbittrans);
    TextView energyHabit = view.findViewById(R.id.habbitenergy);
    TextView consHabit = view.findViewById(R.id.habbitcons);
    TextView foodHabit = view.findViewById(R.id.habbitfood);
    private DatabaseReference daily_answer_ref;

    View transTrack= view.findViewById(R.id.tracker1);
    View consTrack= view.findViewById(R.id.tracker2);
    View foodTrack= view.findViewById(R.id.tracker3);
    View energyTrack= view.findViewById(R.id.tracker4);



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.habit_tracker, container, false);

        List<List<String>> habits = createList();
        daily_answer_ref = FirebaseDatabase.getInstance().getReference().child("daily_answers");


        transportationHabit.setText(habits.get(0).get(0));
        foodHabit.setText(habits.get(1).get(0));
        consHabit.setText(habits.get(2).get(0));
        energyHabit.setText(habits.get(3).get(0));

        transTrack.setVisibility(View.VISIBLE);
        consTrack.setVisibility(View.VISIBLE);
        foodTrack.setVisibility(View.VISIBLE);
        energyTrack.setVisibility(View.VISIBLE);

        TextView transvalue=view.findViewById(R.id.TransTrack);
        TextView consvalue=view.findViewById(R.id.ConsTrack);
        TextView foodvalue=view.findViewById(R.id.foodTrack);
        TextView energyvalue=view.findViewById(R.id.energyTrack);

        transvalue.setText(walking_tracker());
        consvalue.setText(shopping_tracker());
        foodvalue.setText(food_tracker());
        energyvalue.setText(energy_tracker());


        transportationHabitbox.setVisibility(View.VISIBLE);
        foodHabitbox.setVisibility(View.VISIBLE);
        consumptionHabitbox.setVisibility(View.VISIBLE);
        energyHabitbox.setVisibility(View.VISIBLE);

        // Set the checkbox listeners
        transportationCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            transportationHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSectionsAndTrackers(transportationHabitbox, transTrack);
            }
        });

        foodCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            foodHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSectionsAndTrackers(foodHabitbox, foodTrack);
            }
        });

        consumptionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            consumptionHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSectionsAndTrackers(consumptionHabitbox, consTrack);
            }
        });

        energyCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            energyHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSectionsAndTrackers(energyHabitbox, energyTrack);
            }
        });

        return view;
    }

    private void hideOtherSectionsAndTrackers(View visibleSection, View visibleTracker) {
        // Sections
        transportationHabitbox.setVisibility(visibleSection == transportationHabitbox ? View.VISIBLE : View.GONE);
        foodHabitbox.setVisibility(visibleSection == foodHabitbox ? View.VISIBLE : View.GONE);
        consumptionHabitbox.setVisibility(visibleSection == consumptionHabitbox ? View.VISIBLE : View.GONE);
        energyHabitbox.setVisibility(visibleSection == energyHabitbox ? View.VISIBLE : View.GONE);


        transTrack.setVisibility(visibleTracker == transTrack ? View.VISIBLE : View.GONE);
        consTrack.setVisibility(visibleTracker == consTrack ? View.VISIBLE : View.GONE);
        foodTrack.setVisibility(visibleTracker == foodTrack ? View.VISIBLE : View.GONE);
        energyTrack.setVisibility(visibleTracker == energyTrack ? View.VISIBLE : View.GONE);
    }


    private List<List<String>> createList() {
        List<String> transportation = new ArrayList<>();
        transportation.add("Try to walk/cycle more and drive less! On average, walking/cycling reduces your CO2 emissions to a fourth of the original value, and helps promote the idea of walkable cities!");

        List<String> food = new ArrayList<>();
        food.add("Adopt a plant-based diet to reduce your carbon footprint. Limit meat and dairy intake to 1-2 times a week to balance enjoying your favorite foods with supporting an eco-friendly lifestyle.");

        List<String> consumption = new ArrayList<>();
        consumption.add("Limit purchases to once every 2 weeks or month to reduce impulse spending and lower your carbon footprint, as many items travel long distances, increasing CO2 emissions.");

        List<String> energy = new ArrayList<>();
        energy.add("Switch to a more renewable form of energy like solar power! Not only is this eco-friendly, but it also allows you to power your appliances/homes for free!");

        List<List<String>> habits = new ArrayList<>();
        habits.add(transportation);
        habits.add(food);
        habits.add(consumption);
        habits.add(energy);

        return habits;
    }

    private int walking_tracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {


                    DataSnapshot drivingsnap = dateSnapshot.child("Transportation").child("Driving");
                    DataSnapshot walkingsnap = dateSnapshot.child("Transportation").child("Walking");

                    Double driving_val = drivingsnap.child("value").getValue(Double.class);
                    Double walking_val = walkingsnap.child("value").getValue(Double.class);
                    if (driving_val == null)
                        driving_val = 0.0;
                    if (walking_val == null)
                        walking_val = 0.0;

                    if (driving_val != 0) {
                        continue;
                    } else if (driving_val == 0 && walking_val == 0) {
                        continue;
                    } else if (driving_val == 0 && walking_val > 0) {
                        Walk_counter++;
                    }
                }
                Log.d("WalkingTracker", "Total Walk Counter: " + Walk_counter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WalkingTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
        return Walk_counter;
    }

    private int shopping_tracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {


                    DataSnapshot clothingsnap = dateSnapshot.child("Consumption").child("Clothing");

                    Double clothing_val = clothingsnap.child("value").getValue(Double.class);
                    if (clothing_val == null)
                        clothing_val = 0.0;

                    if (clothing_val == 0) {
                        No_shopping_counter++;
                    }
                }
                Log.d("ConsumptionTracker", "Total No-Shop Counter: " + No_shopping_counter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConsumptionTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
        return No_shopping_counter;
    }

    private int food_tracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {

                    DataSnapshot plantdietsnap = dateSnapshot.child("Food").child("Plant-Based");
                    DataSnapshot beefsnap = dateSnapshot.child("Food").child("Plant-Based");
                    DataSnapshot chickensnap = dateSnapshot.child("Food").child("Plant-Based");
                    DataSnapshot fishsnap = dateSnapshot.child("Food").child("Plant-Based");
                    DataSnapshot porksnap = dateSnapshot.child("Food").child("Plant-Based");


                    Double plant_value = plantdietsnap.child("value").getValue(Double.class);
                    Double beefv = beefsnap.child("value").getValue(Double.class);
                    Double chickenv = chickensnap.child("value").getValue(Double.class);
                    Double fishv = fishsnap.child("value").getValue(Double.class);
                    Double porkv = porksnap.child("value").getValue(Double.class);

                    if (plant_value == null)
                        plant_value = 0.0;
                    if (beefv == null)
                        beefv = 0.0;
                    if (chickenv == null)
                        chickenv = 0.0;
                    if (porkv == null)
                        porkv = 0.0;
                    if (fishv == null)
                        fishv = 0.0;

                    if (plant_value > 0 && beefv == 0 && chickenv == 0 && porkv == 0 && fishv == 0) {
                        Vegan_eating_counter++;
                    }

                }
                Log.d("FoodTracker", "Total Vegan Counter: " + Vegan_eating_counter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConsumptionTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
        return Vegan_eating_counter;
    }

    private String energy_tracker() {
        DatabaseReference monthly_answer_ref = FirebaseDatabase.getInstance().getReference().child("monthly_answers");

        Calendar calendar = Calendar.getInstance();
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String previousMonth = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());

        monthly_answer_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double currentElectricity = dataSnapshot.child(currentMonth).child("Energy").child("Electricity").getValue(Double.class);
                Double previousElectricity = dataSnapshot.child(previousMonth).child("Energy").child("Electricity").getValue(Double.class);
                Double currentWater = dataSnapshot.child(currentMonth).child("Energy").child("Water").getValue(Double.class);
                Double previousWater = dataSnapshot.child(previousMonth).child("Energy").child("Water").getValue(Double.class);
                Double currentGas = dataSnapshot.child(currentMonth).child("Energy").child("Gas").getValue(Double.class);
                Double previousGas = dataSnapshot.child(previousMonth).child("Energy").child("Gas").getValue(Double.class);

                if (currentElectricity == null) currentElectricity = 0.0;
                if (previousElectricity == null) previousElectricity = 0.0;
                if (currentWater == null) currentWater = 0.0;
                if (previousWater == null) previousWater = 0.0;
                if (currentGas == null) currentGas = 0.0;
                if (previousGas == null) previousGas = 0.0;
                double currentTotal = currentWater + currentGas + currentElectricity;
                double prevTotal = previousWater + previousGas + previousElectricity;

                if (currentTotal > prevTotal) {
                    Energy_tracker = "Energy consumption has increased by " + (currentTotal - prevTotal) + " this month.";
                } else if (currentElectricity < previousElectricity) {
                    Energy_tracker = "Energy consumption has decreased by " + (currentTotal - prevTotal) + " this month.";
                } else {
                    Energy_tracker = "Energy consumption has remained constant this month.";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ElectricityComparison", "Failed to read value: " + databaseError.getMessage());
            }
        });
        return Energy_tracker ;
    }
}