package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class QuestionPage extends Fragment {
    ArrayList<MCQInfo> q = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scrolling, container, false);
    }
    public void setQuestions() //sets the questions as the elements of the q array.
    {
        MCQInfo q1=new MCQInfo("Do you own or regularly use a car?",1, -1 );
        q1.addsubq(new MCQInfo("What type of car do you drive?",0, -1));
        q1.addsubq(new MCQInfo("How many kilometers/miles do you drive per year?",0, -1));

        MCQInfo q2=new MCQInfo("How often do you use public transportation (bus, train,subway)?",0,-1);
        MCQInfo q3= new MCQInfo("How much time do you spend on public transport (bus, train,subway)?", 0, -1);
        MCQInfo q4= new MCQInfo("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?",0,-1);
        MCQInfo q5= new MCQInfo("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?",0,-1);
        MCQInfo q6= new MCQInfo("What best describes your diet?",1,-1);
        q6.addsubq(new MCQInfo("How often do you eat the following animal-based products?",0,-1));
        MCQInfo q7= new MCQInfo("How often do you waste food or throw away uneaten leftovers?",0,-1);

        q.add(q1);
        q.add(q2);
        q.add(q3);
        q.add(q4);
        q.add(q5);
        q.add(q6);
        q.add(q7);

    }

}