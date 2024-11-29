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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_footer, container, false);
        setupImageButtons(view);
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
