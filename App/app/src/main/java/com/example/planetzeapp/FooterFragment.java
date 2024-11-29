package com.example.planetzeapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FooterFragment extends Fragment {

    private FooterFragmentListener listener;

    public interface FooterFragmentListener {
        void onFooterButtonClicked(int buttonId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FooterFragmentListener) {
            listener = (FooterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FooterFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_footer, container, false);

        ImageButton Button1 = view.findViewById(R.id.imageButton4);
        ImageButton Button2 = view.findViewById(R.id.imageButton5);

        Button1.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFooterButtonClicked(R.id.imageButton4);
            }
        });

        Button2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFooterButtonClicked(R.id.imageButton5);
            }
        });

        return view;
    }


    private void setupImageButtons(View view) {
        int[] buttonIds = new int[]{R.id.imageButton4, R.id.imageButton5};
        for (int buttonId : buttonIds) {
            ImageButton button = view.findViewById(buttonId);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFooterButtonClicked(buttonId);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
