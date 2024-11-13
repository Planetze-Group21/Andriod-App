package com.example.b07demosummer2024;

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
        // Housing Questions
        questions.add(new MultipleChoiceQuestion(
                "11. What type of home do you live in?",
                new String[]{"Detached house", "Semi-detached house",
                        "Townhouse", "Condo/Apartment", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "12. How many people live in your household?",
                new String[]{"1", "2", "3-4", "5 or more"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "13. What is the size of your home?",
                new String[]{"Under 1000 sq. ft.", "1000-2000 sq. ft.", "Over 2000 sq. ft."},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "14. What type of energy do you use to heat your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Wood", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "15. What is your average monthly electricity bill?",
                new String[]{"Under $50", "$50-$100", "$101-$150", "$151-$200", "Over $200"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "16. What type of energy do you use to heat water in your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Solar", "Other"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "17. Do you use any renewable energy sources for electricity or heating?",
                new String[]{"Yes, primarily", "Yes, partially", "No"},
                false,
                -1
        ));

        // Consumption Questions
        questions.add(new MultipleChoiceQuestion(
                "18. How often do you buy new clothes?",
                new String[]{"Monthly", "Quarterly", "Annually", "Rarely"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "19. Do you buy second-hand or eco-friendly products?",
                new String[]{"Yes, regularly", "Yes, occasionally", "No"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "20. How many electronic devices have you purchased in the past year?",
                new String[]{"None", "1", "2", "3 or more"},
                false,
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "21. How often do you recycle?",
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
