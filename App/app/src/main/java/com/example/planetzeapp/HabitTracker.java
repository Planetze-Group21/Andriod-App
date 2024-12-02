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

import java.util.ArrayList;
import java.util.List;

public class HabitTracker extends Fragment {
    int Walk_counter = 0, No_shopping_counter = 0, Vegan_eating_counter = 0;
 //   String Energy_tracker;
    View view;
    CheckBox transportationCheck;
    CheckBox foodCheck ;
    CheckBox consumptionCheck ;
    CheckBox energyCheck;
    View transportationHabitbox ;
    View foodHabitbox ;
    View energyHabitbox;
    View consumptionHabitbox;
    TextView transportationHabit;
    TextView energyHabit ;
    TextView consHabit ;
    TextView foodHabit ;
    private DatabaseReference daily_answer_ref;

    View transTrack;
    View consTrack;
    View foodTrack;
    View energyTrack;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.habit_tracker, container, false);

        List<List<String>> habits = createList();
        daily_answer_ref = FirebaseDatabase.getInstance().getReference().child("daily_answers");

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
         transTrack= view.findViewById(R.id.tracker1);
         consTrack= view.findViewById(R.id.tracker2);
         foodTrack= view.findViewById(R.id.tracker3);
         energyTrack= view.findViewById(R.id.tracker4);



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
        updateWalkingTracker();
        updateShoppingTracker();
        updateFoodTracker();
        String hel="hello";
        transvalue.setText(String.valueOf(Walk_counter));
        consvalue.setText(String.valueOf(No_shopping_counter));
        foodvalue.setText(String.valueOf(Vegan_eating_counter));
        energyvalue.setText(hel);


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

    private void updateWalkingTracker() {
        daily_answer_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int walkCounter = 0;
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    Double walkingVal = dateSnapshot.child("Transportation").child("Walking").child("value").getValue(Double.class);
                    Double drivingVal = dateSnapshot.child("Transportation").child("Driving").child("value").getValue(Double.class);

                    walkingVal = walkingVal == null ? 0.0 : walkingVal;
                    drivingVal = drivingVal == null ? 0.0 : drivingVal;

                    if (drivingVal == 0 && walkingVal > 0) {
                        walkCounter++;
                    }
                }
                Walk_counter = walkCounter;
                // Update UI here
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
                int shoppingCounter = 0;
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    Double groceriesVal = dateSnapshot.child("Shopping").child("Groceries").child("value").getValue(Double.class);
                    Double clothesVal = dateSnapshot.child("Shopping").child("Clothes").child("value").getValue(Double.class);

                    groceriesVal = groceriesVal == null ? 0.0 : groceriesVal;
                    clothesVal = clothesVal == null ? 0.0 : clothesVal;

                    if (groceriesVal > 0 || clothesVal > 0) {
                        shoppingCounter++;
                    }
                }
                No_shopping_counter= shoppingCounter;
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
                int foodCounter = 0;
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
                        foodCounter++;
                    }
                }
                Vegan_eating_counter = foodCounter;
                Log.d("FoodTracker", "Total Food Counter: " + Vegan_eating_counter);
                // Update UI with Food_counter value here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FoodTracker", "Failed to read value: " + databaseError.getMessage());
            }
        });
    }


/*private String energy_tracker() {
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
    }*/
}


