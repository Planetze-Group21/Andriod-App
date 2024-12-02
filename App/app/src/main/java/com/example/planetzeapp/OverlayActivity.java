package com.example.planetzeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class OverlayActivity extends AppCompatActivity implements FooterFragment.FooterFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        // Load the initial content fragment
        loadFragment(new AnnualDisplayFragment());

        // Add the static footer fragment
        FooterFragment footerFragment = new FooterFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.footer_container, footerFragment)
                .commit();
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFooterButtonClicked(int buttonId) {
        Fragment nextFragment;
        if (buttonId == R.id.imageButton4) { // Eco Gauge
            nextFragment = new AnnualDisplayFragment();
        } else if (buttonId == R.id.imageButton5) { // Eco Tracker
            nextFragment = new Introduction();
        } else {
            nextFragment = new AnnualDisplayFragment(); // Fallback fragment
        }

        loadFragment(nextFragment);
    }

}
