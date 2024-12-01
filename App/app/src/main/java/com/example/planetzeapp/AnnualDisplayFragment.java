package com.example.planetzeapp;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnnualDisplayFragment extends Fragment {

    private FirebaseDatabase db;
    private DatabaseReference userRef;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference annual_ans_Ref;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
            annual_ans_Ref = null; // Nullify references when no user is signed in
        }

        if (annual_ans_Ref != null) {
            setupQuestionAndAnswers(view); // Initialize views and fetch data
        } else {
            Log.e("AnnualDisplayFragment", "annual_ans_Ref is null, cannot fetch data.");
        }

        return view;
    }

    private void setupQuestionAndAnswers(View view) {
        if (view == null) {
            Log.e("AnnualDisplayFragment", "View is null in setupQuestionAndAnswers");
            return;
        }

        // Find views safely
        TextView b = view.findViewById(R.id.breakdown_display);
        TextView c = view.findViewById(R.id.comparison_display);
        TextView t = view.findViewById(R.id.total_display);

        if (b == null || t == null || c == null) {
            Log.e("AnnualDisplayFragment", "One or more TextViews are null.");
            return;
        }

        if (annual_ans_Ref == null) {
            Log.e("AnnualDisplayFragment", "annual_ans_Ref is null. Cannot proceed with data fetch.");
            return;
        }

        breakdown(b);
        fetchTotalEmissions(t);
        compareUserToCountryEmissions(c);
    }

    private void fetchTotalEmissions(TextView t) {
        if (t == null || annual_ans_Ref == null) {
            Log.e("fetchTotalEmissions", "TextView or DatabaseReference is null.");
            return;
        }

        annual_ans_Ref.child("annual_co2e").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Double totalEmissionsKg = task.getResult().getValue(Double.class);
                t.setText(totalEmissionsKg != null
                        ? String.format("%.2f tons", totalEmissionsKg / 1000.0)
                        : "No data available.");
            } else {
                Log.e("fetchTotalEmissions", "Error fetching total emissions: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    private void breakdown(TextView b) {
        if (b == null || annual_ans_Ref == null) {
            Log.e("breakdown", "TextView or DatabaseReference is null.");
            return;
        }

        annual_ans_Ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot dataSnapshot = task.getResult();

                Double consumption = dataSnapshot.child("consumption/consumption_co2e").getValue(Double.class);
                Double transport = dataSnapshot.child("transportation/transportation_co2e").getValue(Double.class);
                Double housing = dataSnapshot.child("housing/housing_co2e").getValue(Double.class);
                Double food = dataSnapshot.child("food/food_co2e").getValue(Double.class);

                double[] emissionsKg = {
                        consumption != null ? consumption : 0,
                        transport != null ? transport : 0,
                        housing != null ? housing : 0,
                        food != null ? food : 0
                };

                String[] categories = { "Consumption", "Transportation", "Housing", "Food" };

                // Sort emissions by value
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

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < categories.length; i++) {
                    double emissionTons = emissionsKg[i] / 1000.0;
                    result.append(i + 1).append(". ")
                            .append(categories[i]).append(": ")
                            .append(String.format("%.2f", emissionTons))
                            .append(" tons of CO2\n");
                }

                b.setText(result.toString());
            } else {
                Log.e("breakdown", "Error fetching emissions: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    private void compareUserToCountryEmissions(TextView comparisonTextView) {
        if (comparisonTextView == null || userId == null) {
            Log.e("compareUserToCountryEmissions", "TextView or userId is null.");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        DatabaseReference avgEmissionsRef = FirebaseDatabase.getInstance().getReference("avg_annual_emissions");

        userRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult() != null) {
                DataSnapshot userSnapshot = userTask.getResult();
                String userCountry = userSnapshot.child("country").getValue(String.class);
                Double userAnnualEmissionsKg = userSnapshot.child("annual_answers/annual_co2e").getValue(Double.class);

                if (userCountry == null || userAnnualEmissionsKg == null) {
                    Log.e("compareUserToCountryEmissions", "User country or emissions data is missing.");
                    return;
                }

                double userAnnualEmissionsTons = userAnnualEmissionsKg / 1000.0;

                avgEmissionsRef.child(userCountry).child("emissions_per_capita").get().addOnCompleteListener(avgTask -> {
                    if (avgTask.isSuccessful() && avgTask.getResult() != null) {
                        Double countryAvgEmissionsTons = avgTask.getResult().getValue(Double.class);

                        if (countryAvgEmissionsTons == null) {
                            Log.e("compareUserToCountryEmissions", "No average emissions data for country: " + userCountry);
                            return;
                        }

                        String comparisonResult = (userAnnualEmissionsTons > countryAvgEmissionsTons)
                                ? "Your carbon footprint is above the national average for " + userCountry
                                : "Your carbon footprint is below the national average for " + userCountry;

                        comparisonTextView.setText(comparisonResult);
                    } else {
                        Log.e("compareUserToCountryEmissions", "Error fetching average emissions: " +
                                (avgTask.getException() != null ? avgTask.getException().getMessage() : "Unknown error"));
                    }
                });

            } else {
                Log.e("compareUserToCountryEmissions", "Error fetching user data: " +
                        (userTask.getException() != null ? userTask.getException().getMessage() : "Unknown error"));
            }
        });
    }
}
