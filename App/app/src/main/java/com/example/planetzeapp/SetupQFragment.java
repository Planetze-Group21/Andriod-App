package com.example.planetzeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class SetupQFragment extends Fragment {
    public static List<MultipleChoiceQuestion> questions = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        questions = new ArrayList<>();
        //0
        questions.add(new MultipleChoiceQuestion(
                "Do you own or regularly use a car?",
                new String[]{"Yes", "No"},
                false,
                -1
        ));
        //1
        questions.add(new MultipleChoiceQuestion(
                "What type of car do you drive?",
                new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don’t know"},
                false,
                -1
        ));
        //2
        questions.add(new MultipleChoiceQuestion(
                "How many kilometers/miles do you drive per year?",
                new String[]{"Up to 5,000 km (3,000 miles)", "5,000–10,000 km (3,000–6,000 miles)",
                        "10,000–15,000 km (6,000–9,000 miles)", "15,000–20,000 km (9,000–12,000 miles)",
                        "20,000–25,000 km (12,000–15,000 miles)", "More than 25,000 km (15,000 miles)"},
                false,
                -1
        ));
        //3
        questions.add(new MultipleChoiceQuestion(
                "How often do you use public transportation (bus, train, subway)?",
                new String[]{"Never", "Occasionally (1-2 times/week)", "Frequently (3-4 times/week)", "Always (5+ times/week)"},
                false,
                -1
        ));
        //4
        questions.add(new MultipleChoiceQuestion(
                "How much time do you spend on public transport (bus, train, subway)?",
                new String[]{"Under 1 hour", "1-3 hours", "3-5 hours", "5-10 hours", "More than 10 hours"},
                false,
                -1
        ));
        //5
        questions.add(new MultipleChoiceQuestion(
                "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?",
                new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"},
                false,
                -1
        ));
        //6
        questions.add(new MultipleChoiceQuestion(
                "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?",
                new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"},
                false,
                -1
        ));
        //7
        questions.add(new MultipleChoiceQuestion(
                "What best describes your diet?",
                new String[]{"Vegetarian", "Vegan", "Pescatarian (fish/seafood)", "Meat-based (eat all types of animal products)"},
                false,
                -1
        ));
        //8
        questions.add(new MultipleChoiceQuestion(
                "How often do you eat the following animal-based product: Beef?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        //9
        questions.add(new MultipleChoiceQuestion(
                "How often do you eat the following animal-based product: Pork?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        //10
        questions.add(new MultipleChoiceQuestion(
                "How often do you eat the following animal-based product: Chicken?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        //11
        questions.add(new MultipleChoiceQuestion(
                "How often do you eat the following animal-based product: Fish/Seafood?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        //12
        questions.add(new MultipleChoiceQuestion(
                "How often do you waste food or throw away uneaten leftovers?",
                new String[]{"Never", "Rarely", "Occasionally", "Frequently"},
                false,
                -1
        ));

        //13
        questions.add(new MultipleChoiceQuestion(
                "What type of home do you live in?",
                new String[]{"Detached house", "Semi-detached house",
                        "Townhouse", "Condo/Apartment", "Other"},
                false,
                -1
        ));
        //14
        questions.add(new MultipleChoiceQuestion(
                "How many people live in your household?",
                new String[]{"1", "2", "3-4", "5 or more"},
                false,
                -1
        ));
        //15
        questions.add(new MultipleChoiceQuestion(
                "What is the size of your home?",
                new String[]{"Under 1000 sq. ft.", "1000-2000 sq. ft.", "Over 2000 sq. ft."},
                false,
                -1
        ));
        //16
        questions.add(new MultipleChoiceQuestion(
                "What type of energy do you use to heat your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Wood", "Other"},
                false,
                -1
        ));
        //17
        questions.add(new MultipleChoiceQuestion(
                "What is your average monthly electricity bill?",
                new String[]{"Under $50", "$50-$100", "$101-$150", "$151-$200", "Over $200"},
                false,
                -1
        ));
        //18
        questions.add(new MultipleChoiceQuestion(
                "What type of energy do you use to heat water in your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Solar", "Other"},
                false,
                -1
        ));
        //19
        questions.add(new MultipleChoiceQuestion(
                "Do you use any renewable energy sources for electricity or heating?",
                new String[]{"Yes, primarily", "Yes, partially", "No"},
                false,
                -1
        ));
        //20
        questions.add(new MultipleChoiceQuestion(
                "How often do you buy new clothes?",
                new String[]{"Monthly", "Quarterly", "Annually", "Rarely"},
                false,
                -1
        ));
        //21
        questions.add(new MultipleChoiceQuestion(
                "Do you buy second-hand or eco-friendly products?",
                new String[]{"Yes, regularly", "Yes, occasionally", "No"},
                false,
                -1
        ));
        //22
        questions.add(new MultipleChoiceQuestion(
                "How many electronic devices have you purchased in the past year?",
                new String[]{"None", "1", "2", "3 or more"},
                false,
                -1
        ));
        //23
        questions.add(new MultipleChoiceQuestion(
                "How often do you recycle?",
                new String[]{"Never", "Occasionally", "Always"},
                false,
                -1
        ));
        navigateToQ12Fragment();
        View view = inflater.inflate(R.layout.fragment_setup_q, container, false);


        return view;
    }

    private void navigateToQ12Fragment() {
        Log.d("SetupQFragment", "Navigating to Q12Fragment");

        // Create an instance of Q12Fragment
        Introduction qFragment = new Introduction();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, qFragment);
        transaction.addToBackStack(null);  // Optional: if you want to add this to the back stack
        transaction.commit();
    }


}
