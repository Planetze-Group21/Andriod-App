package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FooterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_footer, container, false);

        // Footer buttons
        ImageButton button1 = view.findViewById(R.id.imageButton4); // Eco Gauge
        ImageButton button2 = view.findViewById(R.id.imageButton5); // Eco Tracker
        ImageButton button3 = view.findViewById(R.id.imageButton1); // Eco hub

        button1.setOnClickListener(v -> startActivity(new Intent(getActivity(), EcoGaugeActivity.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(getActivity(), EcoTrackerActivty.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(getActivity(), EcoHubActivity.class)));

        return view;
    }
}