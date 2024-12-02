package com.example.planetzeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HabitTracker extends Fragment {
    View view;
    CheckBox transportationCheck = view.findViewById(R.id.filterTransportation);
    CheckBox foodCheck = view.findViewById(R.id.filterFood);
    CheckBox consumptionCheck = view.findViewById(R.id.filterConsumption);
    CheckBox energyCheck = view.findViewById(R.id.filterEnergy);

    // Sections for different habits
    View transportationHabitbox = view.findViewById(R.id.transportationSection);
    View foodHabitbox = view.findViewById(R.id.foodSection);
    View energyHabitbox = view.findViewById(R.id.energySection);
    View consumptionHabitbox = view.findViewById(R.id.consumptionSection);
    TextView transportationHabit = view.findViewById(R.id.habbittrans);
    TextView energyHabit = view.findViewById(R.id.habbitenergy);
    TextView consHabit = view.findViewById(R.id.habbitcons);
    TextView foodHabit = view.findViewById(R.id.habbitfood);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.habit_tracker, container, false);

        List<List<String>> habits = createList();


        // Set text from the habits ArrayList
        transportationHabit.setText(habits.get(0).get(0));
        foodHabit.setText(habits.get(1).get(0));
        consHabit.setText(habits.get(2).get(0));
        energyHabit.setText(habits.get(3).get(0));

        // Initially set all sections to visible
        transportationHabitbox.setVisibility(View.VISIBLE);
        foodHabitbox.setVisibility(View.VISIBLE);
        consumptionHabitbox.setVisibility(View.VISIBLE);
        energyHabitbox.setVisibility(View.VISIBLE);

        // Set the checkbox listeners
        transportationCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            transportationHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSections(transportationHabitbox);
            }
        });

        foodCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            foodHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSections(foodHabitbox);
            }
        });

        consumptionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            consumptionHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSections(consumptionHabitbox);
            }
        });

        energyCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            energyHabitbox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                hideOtherSections(energyHabitbox);
            }
        });

        return view;
    }

    private void hideOtherSections(View selectedSection) {
        // Hide all sections
        transportationHabitbox.setVisibility(View.GONE);
        foodHabitbox.setVisibility(View.GONE);
        consumptionHabitbox.setVisibility(View.GONE);
        energyHabitbox.setVisibility(View.GONE);

        selectedSection.setVisibility(View.VISIBLE);
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
}
