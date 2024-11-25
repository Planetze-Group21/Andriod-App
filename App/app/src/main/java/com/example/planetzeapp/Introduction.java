package com.example.planetzeapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class Introduction extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.introduction, container, false);
        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            navigateToQFragment();        });
        return view;

    }
    private void navigateToQFragment() {
        QFragment qFragment = new QFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, qFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}