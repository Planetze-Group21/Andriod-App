package com.example.myapplication;

import java.util.ArrayList;

public class MCQInfo {

    private String question;
    private String answer;
    int subquestions;
    ArrayList<MCQInfo> subquestion;
    int isSubq;
    boolean answered;
    boolean follow_up;
    int mandatory; // -1 if yes , -2 if optional, or if its based on a certain answer of a q (mandtory=index) of the q it depends on

    public MCQInfo(String question, int isSubq,  int mandatory) {
        this.question = question;
        this.answered = false;
        if(isSubq>0)
            subquestion= new ArrayList<>();
        else
            subquestion=null;

        this.mandatory = mandatory;
    }

    public void setUserAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }
    public void Q_answered() {
        answered = true;
    }
    public boolean isAnswered(){
        return answered;
    }
    public boolean isMandatory(){
        if(mandatory>=0)
            return false;
        return true;
    }
    public void addsubq(MCQInfo subq){
        subquestion.add(subq);
    }

}
