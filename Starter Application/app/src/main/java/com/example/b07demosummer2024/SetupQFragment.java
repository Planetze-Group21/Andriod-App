package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.List;

public class SetupQFragment extends Fragment {
    private List<MultipleChoiceQuestion> questions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_q, container, false);

        Button buttonSubmit = view.findViewById(R.id.buttonSubmmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSubmit();
            }
        });

        questions = new ArrayList<>();

        // Housing Questions
        questions.add(new MultipleChoiceQuestion(
                "11. What type of home do you live in?",
                new String[]{"Detached house", "Semi-detached house", "Townhouse", "Condo/Apartment", "Other"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "12. How many people live in your household?",
                new String[]{"1", "2", "3-4", "5 or more"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "13. What is the size of your home?",
                new String[]{"Under 1000 sq. ft.", "1000-2000 sq. ft.", "Over 2000 sq. ft."},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "14. What type of energy do you use to heat your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Wood", "Other"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "15. What is your average monthly electricity bill?",
                new String[]{"Under $50", "$50-$100", "$101-$150", "$151-$200", "Over $200"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "16. What type of energy do you use to heat water in your home?",
                new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Solar", "Other"},
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "17. Do you use any renewable energy sources for electricity or heating?",
                new String[]{"Yes, primarily", "Yes, partially", "No"},
                false
                -1
        ));

        // Consumption Questions
        questions.add(new MultipleChoiceQuestion(
                "18. How often do you buy new clothes?",
                new String[]{"Monthly", "Quarterly", "Annually", "Rarely"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "19. Do you buy second-hand or eco-friendly products?",
                new String[]{"Yes, regularly", "Yes, occasionally", "No"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "20. How many electronic devices have you purchased in the past year?",
                new String[]{"None", "1", "2", "3 or more"},
                false
                -1
        ));
        questions.add(new MultipleChoiceQuestion(
                "21. How often do you recycle?",
                new String[]{"Never", "Occasionally", "Always"},
                false
                -1
        ));

        return view;
    }

    // what heppens is Submit button is clicked
    // 1. check if all in answered   2. Save data   3. go to home page/fragment
    private void buttonSubmit() {

        //1. checks if all questions
        boolean allAnswered = true;
        for (MultipleChoiceQuestion question : questions) {
            if (!question.isAnswered() && question.isMandatory(){
                allAnswered = false ;
                break;
        }
        if (allAnswered) {
            //2. save data

            // 3. go to home page
            Fragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.commit();
        } else {
            Toast.makeText(getContext(), "Please answer all the questions before submitting", Toast.LENGTH_SHORT).show();
        }
    }

}
