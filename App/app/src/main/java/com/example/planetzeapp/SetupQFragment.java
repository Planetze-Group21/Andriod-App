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

        questions.add(new MultipleChoiceQuestion(
                "1. Do you own or regularly use a car?",
                new String[]{"Yes", "No"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "1A. What type of car do you drive?",
                new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don’t know"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "1B. How many kilometers/miles do you drive per year?",
                new String[]{"Up to 5,000 km (3,000 miles)", "5,000–10,000 km (3,000–6,000 miles)",
                        "10,000–15,000 km (6,000–9,000 miles)", "15,000–20,000 km (9,000–12,000 miles)",
                        "20,000–25,000 km (12,000–15,000 miles)", "More than 25,000 km (15,000 miles)"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "2. How often do you use public transportation (bus, train, subway)?",
                new String[]{"Never", "Occasionally (1-2 times/week)", "Frequently (3-4 times/week)", "Always (5+ times/week)"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "3. How much time do you spend on public transport (bus, train, subway)?",
                new String[]{"Under 1 hour", "1-3 hours", "3-5 hours", "5-10 hours", "More than 10 hours"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "4. How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?",
                new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "5. How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?",
                new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "6. What best describes your diet?",
                new String[]{"Vegetarian", "Vegan", "Pescatarian (fish/seafood)", "Meat-based (eat all types of animal products)"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "7. How often do you eat the following animal-based product: Beef?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "8. How often do you eat the following animal-based product: Pork?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "9. How often do you eat the following animal-based product: Chicken?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "10. How often do you eat the following animal-based product: Fish/Seafood?",
                new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "11. How often do you waste food or throw away uneaten leftovers?",
                new String[]{"Never", "Rarely", "Occasionally", "Frequently"},
                false,
                -1
        ));

        // Housing Questions
        questions.add(new MultipleChoiceQuestion(
                "12. What type of home do you live in?",
                new String[]{"Detached house", "Semi-detached house",
                        "Townhouse", "Condo/Apartment", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "13. How many people live in your household?",
                new String[]{"1", "2", "3-4", "5 or more"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "14. What is the size of your home?",
                new String[]{"Under 1000 sq. ft.", "1000-2000 sq. ft.", "Over 2000 sq. ft."},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "15. What type of energy do you use to heat your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Wood", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "16. What is your average monthly electricity bill?",
                new String[]{"Under $50", "$50-$100", "$101-$150", "$151-$200", "Over $200"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "17. What type of energy do you use to heat water in your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Solar", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "18. Do you use any renewable energy sources for electricity or heating?",
                new String[]{"Yes, primarily", "Yes, partially", "No"},
                false,
                -1
        ));

        // Consumption Questions
        questions.add(new MultipleChoiceQuestion(
                "19. How often do you buy new clothes?",
                new String[]{"Monthly", "Quarterly", "Annually", "Rarely"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "20. Do you buy second-hand or eco-friendly products?",
                new String[]{"Yes, regularly", "Yes, occasionally", "No"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "21. How many electronic devices have you purchased in the past year?",
                new String[]{"None", "1", "2", "3 or more"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "22. How often do you recycle?",
                new String[]{"Never", "Occasionally", "Always"},
                false,
                -1
        ));

        View view = inflater.inflate(R.layout.fragment_setup_q, container, false);

        MultipleChoiceQuestion question = questions.get(0);

        // Set the question text to the TextView
        TextView questionTextView = view.findViewById(R.id.questionTextView);
        questionTextView.setText(question.getQuestion());


        // Initialize the answer buttons and set their text dynamically
        Button[] buttons = new Button[]{
                view.findViewById(R.id.buttonAnswer1),
                view.findViewById(R.id.buttonAnswer2),
                view.findViewById(R.id.buttonAnswer3),
                view.findViewById(R.id.buttonAnswer4),
                view.findViewById(R.id.buttonAnswer5)
        };

        // Loop through the options and set the text for each button
        for (int i = 0; i < question.getOptions().length; i++) {
            buttons[i].setVisibility(View.VISIBLE); // Make sure the button is visible
            buttons[i].setText(question.getOptions()[i]);

            // Set up onClickListener for each button
            final int optionIndex = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleAnswerClick(question.getOptions()[optionIndex]);
                }
            });
        }

        // Hide any remaining buttons if there are fewer than 5 options
        for (int i = question.getOptions().length; i < buttons.length; i++) {
            buttons[i].setVisibility(View.GONE);
        }

        return view;
    }

    private void handleAnswerClick(String answer) {
        questions.get(1).setUserAnswer(answer);
        navigateToQ12Fragment();
    }
    private void navigateToQ12Fragment() {
        Log.d("SetupQFragment", "Navigating to Q12Fragment");

        // Create an instance of Q12Fragment
        QFragment qFragment = new QFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, qFragment);
        transaction.addToBackStack(null);  // Optional: if you want to add this to the back stack
        transaction.commit();
    }


}
