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

        // Set buttons visibility and text based on the options available for the question
        for (Button button : buttons) {
            if (button == null) {
                Log.e("QFragment", "A button is missing in the layout.");
            }
        }

        // Loop through question options and set button texts and listeners
        for (int i = 0; i < question.getOptions().length; i++) {
            buttons[i].setVisibility(View.VISIBLE);
            buttons[i].setText(question.getOptions()[i]);
            final int optionIndex = i;
            buttons[i].setOnClickListener(v -> handleAnswerClick(question.getOptions()[optionIndex]));
        }

        // Hide buttons that are not needed
        for (int i = question.getOptions().length; i < buttons.length; i++) {
            buttons[i].setVisibility(View.GONE);
        }
    }

    private void handleAnswerClick(String answer) {
        // Log the answer clicked for debugging
        Log.d("QFragment", "Answer clicked: " + answer);

        // Set the user's answer for the current question
        questions.get(questionIndex).setUserAnswer(answer);

        // Navigate to the appropriate next question based on the current question index
        if (questionIndex == 0 && "No".equals(answer)) {
            navigateToNextQuestion(3);
        }
        else if (questionIndex == 7 && "Meat-based (eat all types of animal products)".equals(answer)) {
            navigateToNextQuestion(8);
        }
        else if (questionIndex == 7) {
            navigateToNextQuestion(12);
        }
        else {
            navigateToNextQuestion();
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
