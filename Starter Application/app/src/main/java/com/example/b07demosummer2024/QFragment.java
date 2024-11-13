package com.example.b07demosummer2024;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class QFragment extends SetupQFragment {

    private int questionIndex;

    // Default constructor
    public QFragment() {
        questionIndex =1;
    }

    // Constructor that accepts an index to set questionIndex
    public QFragment(int index) {
        questionIndex = index;
    }

    public static QFragment newInstance(int index) {
        QFragment fragment = new QFragment(index);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Q12Fragment", "onCreate: Initializing Q12Fragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q12, container, false);
        Log.d("Q12Fragment", "onCreateView: Inflating the layout");

        if (questions == null || questions.isEmpty()) {
            Log.e("Q12Fragment", "Questions list is null or empty.");
            return null;  // Or handle the error properly
        }
        if (questionIndex < 0 || questionIndex >= questions.size()) {
            Log.e("Q12Fragment", "Invalid questionIndex: " + questionIndex);
            // Show a fallback view or handle the error gracefully
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return view;  // Exit the fragment early
        }

        MultipleChoiceQuestion question = questions.get(questionIndex);
        Log.d("Q12Fragment", "Loaded question: " + question.getQuestion());

        // Set the question text
        TextView questionTextView = view.findViewById(R.id.questionTextView);
        questionTextView.setText(question.getQuestion());

        // Initialize buttons for answer options
        Button[] buttons = new Button[]{
                view.findViewById(R.id.buttonAnswer1),
                view.findViewById(R.id.buttonAnswer2),
                view.findViewById(R.id.buttonAnswer3),
                view.findViewById(R.id.buttonAnswer4),
                view.findViewById(R.id.buttonAnswer5),
                view.findViewById(R.id.buttonAnswer6)
        };

        // Check if any button is null
        for (Button button : buttons) {
            if (button == null) {
                Log.e("Q12Fragment", "A button is missing in the layout.");
            }
        }

        for (int i = 0; i < question.getOptions().length; i++) {
            buttons[i].setVisibility(View.VISIBLE);
            buttons[i].setText(question.getOptions()[i]);
            final int optionIndex = i;
            buttons[i].setOnClickListener(v -> handleAnswerClick(question.getOptions()[optionIndex]));
        }


        for (int i = question.getOptions().length; i < buttons.length; i++) {
            buttons[i].setVisibility(View.GONE);
        }

        return view;
    }

    private void handleAnswerClick(String answer) {
        questions.get(questionIndex).setUserAnswer(answer);
        navigateToNextQuestion();
    }

    private void navigateToNextQuestion() {
        if (questionIndex + 1 < questions.size()) {
            QFragment nextFragment = QFragment.newInstance(questionIndex + 1); // Create the next fragment

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, nextFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}

