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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HabitTracker extends Fragment {
    int Walk_counter = 0, No_shopping_counter = 0, Vegan_eating_counter = 0;
    //   String Energy_tracker;
    View view;
    CheckBox transportationCheck;
    CheckBox foodCheck;
    CheckBox consumptionCheck;
    CheckBox energyCheck;
    View transportationHabitbox;
    View foodHabitbox;
    View energyHabitbox;
    View consumptionHabitbox;
    TextView transportationHabit;
    TextView energyHabit;
    TextView consHabit;
    TextView foodHabit;
    private DatabaseReference daily_answer_ref;
    private DatabaseReference monthly_answer_ref;
    View transTrack;
    View consTrack;
    View foodTrack;
    View energyTrack;

    TextView transvalue;
    TextView consvalue;
    TextView foodvalue ;
    TextView energyvalue ;

    private FirebaseAuth mAuth;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.habit_tracker, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Retrieve the user ID
        } else {
            Log.e("HabitTracker", "No user is logged in.");
        }

        List<List<String>> habits = createList();
        if (userId != null) {
            daily_answer_ref = FirebaseDatabase.getInstance().getReference().child("daily_answers").child(userId);
            monthly_answer_ref = FirebaseDatabase.getInstance().getReference().child("monthly_answers").child(userId);
        } else {
            Log.e("HabitTracker", "User ID is null. Cannot initialize daily_answer_ref.");
            return view;
        }

        transportationCheck = view.findViewById(R.id.filterTransportation);
        foodCheck = view.findViewById(R.id.filterFood);
        consumptionCheck = view.findViewById(R.id.filterConsumption);
        energyCheck = view.findViewById(R.id.filterEnergy);
        transportationHabitbox = view.findViewById(R.id.transportationSection);
        foodHabitbox = view.findViewById(R.id.foodSection);
        energyHabitbox = view.findViewById(R.id.energySection);
        consumptionHabitbox = view.findViewById(R.id.consumptionSection);
        transportationHabit = view.findViewById(R.id.habbittrans);
        energyHabit = view.findViewById(R.id.habbitenergy);
        consHabit = view.findViewById(R.id.habbitcons);
        foodHabit = view.findViewById(R.id.habbitfood);
        transTrack = view.findViewById(R.id.tracker1);
        consTrack = view.findViewById(R.id.tracker2);
        foodTrack = view.findViewById(R.id.tracker3);
        energyTrack = view.findViewById(R.id.tracker4);


        transportationHabit.setText(habits.get(0).get(0));
        foodHabit.setText(habits.get(1).get(0));
        consHabit.setText(habits.get(2).get(0));
        energyHabit.setText(habits.get(3).get(0));

        transTrack.setVisibility(View.VISIBLE);
        consTrack.setVisibility(View.VISIBLE);
        foodTrack.setVisibility(View.VISIBLE);
        energyTrack.setVisibility(View.VISIBLE);

        transvalue = view.findViewById(R.id.TransTrack);
        consvalue = view.findViewById(R.id.ConsTrack);
        foodvalue = view.findViewById(R.id.foodTrack);
        energyvalue = view.findViewById(R.id.energyTrack);
        updateWalkingTracker();
        updateShoppingTracker();
        updateFoodTracker();
        energy_tracker();


        transportationHabitbox.setVisibility(View.VISIBLE);
        foodHabitbox.setVisibility(View.VISIBLE);
        consumptionHabitbox.setVisibility(View.VISIBLE);
        energyHabitbox.setVisibility(View.VISIBLE);

        // Set the checkbox listeners
        transportationCheck.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility());
        foodCheck.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility());
        consumptionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility());
        energyCheck.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility());


        return view;
    }
    private void updateVisibility() {
        boolean anyChecked = transportationCheck.isChecked() || foodCheck.isChecked()
                || consumptionCheck.isChecked() || energyCheck.isChecked();

        // Show all sections and trackers if no checkbox is checked
        if (!anyChecked) {
            transportationHabitbox.setVisibility(View.VISIBLE);
            transTrack.setVisibility(View.VISIBLE);

            foodHabitbox.setVisibility(View.VISIBLE);
            foodTrack.setVisibility(View.VISIBLE);

            consumptionHabitbox.setVisibility(View.VISIBLE);
            consTrack.setVisibility(View.VISIBLE);

            energyHabitbox.setVisibility(View.VISIBLE);
            energyTrack.setVisibility(View.VISIBLE);
            return;
        }

        // Otherwise, update visibility based on individual checkbox states
        transportationHabitbox.setVisibility(transportationCheck.isChecked() ? View.VISIBLE : View.GONE);
        transTrack.setVisibility(transportationCheck.isChecked() ? View.VISIBLE : View.GONE);

        foodHabitbox.setVisibility(foodCheck.isChecked() ? View.VISIBLE : View.GONE);
        foodTrack.setVisibility(foodCheck.isChecked() ? View.VISIBLE : View.GONE);

        consumptionHabitbox.setVisibility(consumptionCheck.isChecked() ? View.VISIBLE : View.GONE);
        consTrack.setVisibility(consumptionCheck.isChecked() ? View.VISIBLE : View.GONE);

        energyHabitbox.setVisibility(energyCheck.isChecked() ? View.VISIBLE : View.GONE);
        energyTrack.setVisibility(energyCheck.isChecked() ? View.VISIBLE : View.GONE);
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

    private void updateWalkingTracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    Double walkingVal = dateSnapshot.child("Transportation").child("Walking").child("value").getValue(Double.class);
                    Double drivingVal = dateSnapshot.child("Transportation").child("Driving").child("value").getValue(Double.class);

                    walkingVal = walkingVal == null ? 0.0 : walkingVal;
                    drivingVal = drivingVal == null ? 0.0 : drivingVal;

                    if (drivingVal == 0 && walkingVal > 0) {
                        Walk_counter++;
                    }
                }
                transvalue.setText(String.valueOf(Walk_counter));
                Log.d("WalkingTracker", "Total Walk Counter: " + Walk_counter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WalkingTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
    }


    private void updateShoppingTracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    Double clothesVal = dateSnapshot.child("Consumption").child("Clothing").child("value").getValue(Double.class);

                    clothesVal = clothesVal == null ? 0.0 : clothesVal;

                    if (clothesVal > 0) {
                        No_shopping_counter++;
                    }
                }

                consvalue.setText(String.valueOf(No_shopping_counter));
                Log.d("ShoppingTracker", "Total Shopping Counter: " + No_shopping_counter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ShoppingTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
    }


    private void updateFoodTracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot beefsnap = dateSnapshot.child("Food").child("Beef");
                    DataSnapshot chickensnap = dateSnapshot.child("Food").child("Chicken");
                    DataSnapshot fishsnap = dateSnapshot.child("Food").child("Fish");
                    DataSnapshot porksnap = dateSnapshot.child("Food").child("Pork");
                    DataSnapshot plantbasedsnap = dateSnapshot.child("Food").child("Plant-Based");

                    Double beefVal = beefsnap.child("value").getValue(Double.class);
                    Double chickenVal = chickensnap.child("value").getValue(Double.class);
                    Double fishVal = fishsnap.child("value").getValue(Double.class);
                    Double porkVal = porksnap.child("value").getValue(Double.class);
                    Double plantBasedVal = plantbasedsnap.child("value").getValue(Double.class);

                    beefVal = beefVal == null ? 0.0 : beefVal;
                    chickenVal = chickenVal == null ? 0.0 : chickenVal;
                    fishVal = fishVal == null ? 0.0 : fishVal;
                    porkVal = porkVal == null ? 0.0 : porkVal;
                    plantBasedVal = plantBasedVal == null ? 0.0 : plantBasedVal;

                    if (beefVal > 0 || chickenVal > 0 || fishVal > 0 || porkVal > 0 || plantBasedVal > 0) {
                        Vegan_eating_counter++;
                    }
                }

                Log.d("FoodTracker", "Total Food Counter: " + Vegan_eating_counter);
                foodvalue.setText(String.valueOf(Vegan_eating_counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FoodTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
    }


    private void energy_tracker() {

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

                currentElectricity = currentElectricity == null ? 0.0 : currentElectricity;
                previousElectricity = previousElectricity == null ? 0.0 : previousElectricity;
                currentWater = currentWater == null ? 0.0 : currentWater;
                previousWater = previousWater == null ? 0.0 : previousWater;
                currentGas = currentGas == null ? 0.0 : currentGas;
                previousGas = previousGas == null ? 0.0 : previousGas;

                double energyChange = (currentElectricity + currentWater + currentGas) - (previousElectricity + previousWater + previousGas);
                String trend = energyChange < 0 ? "Reduced Energy Usage by " + energyChange : "Increased Energy Usage by " + energyChange ;

                energyvalue = view.findViewById(R.id.energyTrack);
                energyvalue.setText(trend);
                Log.d("EnergyTracker", "Energy trend: " + trend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EnergyTracker", "Failed to read energy values: " + databaseError.getMessage());
            }
        });
    }
}


