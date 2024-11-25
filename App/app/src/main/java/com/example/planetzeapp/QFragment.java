package com.example.planetzeapp;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QFragment extends SetupQFragment {
    private int questionIndex;

    public QFragment() {
        questionIndex = 0;
    }

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
        Log.d("QFragment", "onCreate: Initializing QFragment");
        setupQuestions();  // Set up the questions outside onCreateView
    }

    private void setupQuestions() {
        // Set up the questions list here
        if (questions == null || questions.isEmpty()) {
            Log.e("QFragment", "Questions list is null or empty.");
            // Initialize questions or fetch them if necessary
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q12, container, false);
        Log.d("QFragment", "onCreateView: Inflating the layout");

        if (questions == null || questions.isEmpty()) {
            Log.e("QFragment", "Questions list is null or empty.");
            return null;  // Or handle the error properly
        }

        MultipleChoiceQuestion question = questions.get(questionIndex);
        Log.d("QFragment", "Loaded question: " + question.getQuestion());
        setupQuestionAndAnswers(view, question);
        return view;
    }

    private void setupQuestionAndAnswers(View view, MultipleChoiceQuestion question) {
        TextView questionTextView = view.findViewById(R.id.questionTextView);
        questionTextView.setText(question.getQuestion());
        Button[] buttons = new Button[] {
                view.findViewById(R.id.buttonAnswer1),
                view.findViewById(R.id.buttonAnswer2),
                view.findViewById(R.id.buttonAnswer3),
                view.findViewById(R.id.buttonAnswer4),
                view.findViewById(R.id.buttonAnswer5),
                view.findViewById(R.id.buttonAnswer6)
        };
        for (Button button : buttons) {
            if (button == null) {
                Log.e("QFragment", "A button is missing in the layout.");
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
    }

    private void handleAnswerClick(String answer) {
        // Log the answer clicked for debugging
        Log.d("QFragment", "Answer clicked: " + answer);
        questions.get(questionIndex).Q_answered();
        questions.get(questionIndex).setUserAnswer(answer);
        Log.d("QFragment", "User answer for question " + questionIndex + ": " + questions.get(questionIndex).getUserAnswer());
        if (questionIndex == 0 && "No".equals(answer)) {
            navigateToNextQuestion(3);
        }
        else if (questionIndex == 7 && "Meat-based (eat all types of animal products)".equals(answer)) {
            navigateToNextQuestion(8);
        }
        else if (questionIndex == 7) {
            navigateToNextQuestion(12);
        }
        else if( questionIndex == 23)
            {
                AnnualQs_Finish qFragment = new AnnualQs_Finish();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, qFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        else
        {navigateToNextQuestion();
        }
    }

    private void navigateToNextQuestion() {
        navigateToNextQuestion(questionIndex + 1);
    }

    private void navigateToNextQuestion(int nextIndex) {
        if (nextIndex < questions.size()) {
            QFragment nextFragment = QFragment.newInstance(nextIndex);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, nextFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }



}
