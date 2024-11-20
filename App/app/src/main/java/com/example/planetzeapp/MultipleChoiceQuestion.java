package com.example.planetzeapp;

public class MultipleChoiceQuestion{
    private String question;
    private String[] options;
    private String userAnswer;
    boolean answered;
    boolean follow_up;
    int mandatory; // -1 if yes , -2 if optional, index of followup in string

    public MultipleChoiceQuestion() {
    }
    public MultipleChoiceQuestion(String question, String[] options, boolean follow_up,int mandatory) {
        this.question = question;
        this.options = options;
        this.answered = false;
        this.follow_up = follow_up;
        this.mandatory = mandatory;
    }

    public void setUserAnswer(String answer) {
        this.userAnswer = answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getQuestion() {
        return question;
    }
    public String[] getOptions() {
        return options;
    }
    public void Q_answered() {
        answered = true;
    }
    public boolean isAnswered(){
        return answered;
    }
    public boolean isMandatory(){
        if(mandatory >=-1)
            return false;
        return true;
    }
}
