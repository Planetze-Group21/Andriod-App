package com.example.planetzeapp;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnnualDisplayFragment extends Fragment {

    private FirebaseDatabase db;
    private DatabaseReference userRef;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference annual_ans_Ref;
    AnnualCalc c;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AnnualCalc c = new AnnualCalc();
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.fragment_annual_display, container, false);
        db = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            String userPath = "users/" + userId;
            userRef = db.getReference(userPath);
            annual_ans_Ref = databaseRef.child("users").child(userId).child("annual_answers");
            Log.d("Firebase", "User ID: " + userId);
        } else {
            Log.e("AnnualDisplayFragment", "No user is signed in.");
            userRef = null;
            annual_ans_Ref = null; // Ensure this is null if no user is signed in
        }

        //showFooterFragment();

        if (annual_ans_Ref != null) {
            setupQuestionAndAnswers(view); // Pass view directly here
        } else {
            Log.e("AnnualDisplayFragment", "annual_ans_Ref is null, cannot fetch data.");
        }

        return view;
    }

    private void setupQuestionAndAnswers(View view) {
        // Make sure the view is not null
        if (view == null) {
            Log.e("AnnualDisplayFragment", "View is null in setupQuestionAndAnswers");
            return;
        }

        // Fetch the views by their IDs
        TextView b = view.findViewById(R.id.breakdown_display);
        TextView t = view.findViewById(R.id.total_display);  // Use the passed 'view' instead of getView()

        // Log the state of the views to make sure they're found
        if (b == null) {
            Log.e("AnnualDisplayFragment", "breakdown_display TextView is null");
        }
        if (t == null) {
            Log.e("AnnualDisplayFragment", "total_display TextView is null");
        }

        // Proceed if all necessary views are available
        if (b != null && t != null && annual_ans_Ref != null) {
            breakdown(b);
            fetchTotalEmissions(t);
        } else {
            Log.e("AnnualDisplayFragment", "UI elements or data reference is null.");
        }
    }

    private void fetchTotalEmissions(TextView t) {
        annual_ans_Ref.child("annual_co2e").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Double totalEmissionsKg = task.getResult().getValue(Double.class);

                t.setText(totalEmissionsKg != null
                        ? String.format("%.2f tons", totalEmissionsKg / 1000.0)
                        : "No data available.");
            } else {
                Log.e("TotalEmissions", "Error fetching total emissions: " + task.getException().getMessage());
            }
        });
    }

    private void breakdown(TextView b) {
        annual_ans_Ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                // Get emissions for each category
                Double consumption = dataSnapshot.child("consumption/consumption_co2e").getValue(Double.class);
                Double transport = dataSnapshot.child("transportation/transportation_co2e").getValue(Double.class);
                Double housing = dataSnapshot.child("housing/housing_co2e").getValue(Double.class);
                Double food = dataSnapshot.child("food/food_co2e").getValue(Double.class);

                // Assign default values if any are null
                double[] emissionsKg = {
                        consumption != null ? consumption : 0,
                        transport != null ? transport : 0,
                        housing != null ? housing : 0,
                        food != null ? food : 0
                };

                String[] categories = { "Consumption", "Transportation", "Housing", "Food" };
                double total;

                // Sort the categories by emissions
                for (int i = 0; i < emissionsKg.length - 1; i++) {
                    for (int j = 0; j < emissionsKg.length - i - 1; j++) {
                        if (emissionsKg[j] < emissionsKg[j + 1]) {
                            double tempEmission = emissionsKg[j];

                            emissionsKg[j] = emissionsKg[j + 1];
                            emissionsKg[j + 1] = tempEmission;

                            String tempCategory = categories[j];
                            categories[j] = categories[j + 1];
                            categories[j + 1] = tempCategory;

                        }
                    }
                }
                // Build the result string
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < categories.length; i++) {
                    double emissionTons = emissionsKg[i] / 1000.0;
                    result.append(i + 1) // Rank
                            .append(". ")
                            .append(categories[i])
                            .append(": ")
                            .append(String.format("%.2f", emissionTons))
                            .append(" tons of CO2\n");
                }

                b.setText(result.toString());

            } else {
                Log.e("AnnualCalc", "Error fetching emissions: " + task.getException().getMessage());
            }
        });
    }


    private void compareUserToCountryEmissions() {

    }

//
//    private void showFooterFragment() {
//        // Make the footer fragment container visible
//        View footerContainer = getActivity().findViewById(R.id.footer_fragment_container);
//        if (footerContainer != null) {
//            footerContainer.setVisibility(View.VISIBLE);  // Show the footer fragment container
//        }
//    }


}