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
import android.view.View;
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
import androidx.fragment.app.FragmentTransaction;

public class AnnualCalc extends Fragment {
   final private FirebaseDatabase db;
   private DatabaseReference userRef;
   public double total, housing, transport, food, consumption;
   DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
   DatabaseReference annual_ans_Ref;
   private String userId;

   public AnnualCalc() {
      db = FirebaseDatabase.getInstance();

      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      if (currentUser != null) {
         userId = currentUser.getUid();
         String userPath = "users/" + userId;
         userRef = db.getReference(userPath);
         annual_ans_Ref = databaseRef.child("users").child(userId).child("annual_answers");
         Log.d("AnnualCalc", "Initialized user path: " + userPath);
      } else {
         System.err.println("No user is signed in. Redirecting to login...");
         userRef = null;
      }
      executeAllEmissionsCalculations();
   }

   public void executeAllEmissionsCalculations() {
      Annual_Home_emissions(() -> {
         Annual_Food_emissions(() -> {
            Annual_transporation_emissions(() -> {
               Annual_consumption_emissions(() -> {
                  Total_emissions(() -> {
                     navigateToOverlayActivity();
                  });
               });
            });
         });
      });
   }
   public void Annual_Home_emissions(Runnable onComplete) {
      int[] sum = {0};
      annual_ans_Ref.get().addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            Log.d("AnnualCalc", "Successfully fetched annual answers for housing.");
            DataSnapshot dataSnapshot = task.getResult();

            // Fetching required data
            String bill = dataSnapshot.child("housing/bill").getValue(String.class);
            Log.d("AnnualCalc", "Fetched housing bill: " + bill);

            String heatingSource = dataSnapshot.child("housing/heating_source").getValue(String.class);
            Log.d("AnnualCalc", "Fetched heating source: " + heatingSource);

            String householdMembers = dataSnapshot.child("housing/household_members").getValue(String.class);
            Log.d("AnnualCalc", "Fetched household members: " + householdMembers);

            String size = dataSnapshot.child("housing/size").getValue(String.class);
            Log.d("AnnualCalc", "Fetched housing size: " + size);

            String structureType = dataSnapshot.child("housing/home_type").getValue(String.class);
            Log.d("AnnualCalc", "Fetched structure type: " + structureType);

            String renewabletype = dataSnapshot.child("housing/renewable_energy_usage").getValue(String.class);
            Log.d("AnnualCalc", "Fetched renewable energy type: " + renewabletype);

            String waterSource = dataSnapshot.child("housing/water_heating_energy").getValue(String.class);
            Log.d("AnnualCalc", "Fetched water source: " + waterSource);

            // Additional emissions logic
            if (waterSource != null && !waterSource.equals(heatingSource)) {
               Log.d("AnnualCalc", "Water source differs from heating source. Adding additional emissions.");
               sum[0] += 300;
            }

            if (renewabletype != null && renewabletype.equals("partially")) {
               Log.d("AnnualCalc", "Renewable type not 'partially'. Adding additional emissions.");
               sum[0] += 4000;
            }

            if (renewabletype != null && renewabletype.equals("primarily")) {
               Log.d("AnnualCalc", "Renewable type not 'primarily'. Adding additional emissions.");
               sum[0] += 6000;
            }

            // Validation for required fields
            if (bill != null && heatingSource != null && householdMembers != null && size != null && structureType != null) {
               Log.d("AnnualCalc", "Housing data is complete. Proceeding with calculations.");

               String key = structureType + "|" + size + "|" + householdMembers + "|" + heatingSource + "|" + bill;
               DatabaseReference calc = databaseRef.child("housing_emissions").child(key);

               calc.get().addOnCompleteListener(calcTask -> {
                  if (calcTask.isSuccessful() && calcTask.getResult() != null) {
                     Integer emissionValue = calcTask.getResult().getValue(Integer.class);

                     if (emissionValue != null) {
                        sum[0] += emissionValue;

                        // Storing the calculated CO2e value
                        annual_ans_Ref.child("housing").child("housing_co2e").setValue(sum[0])
                                .addOnCompleteListener(storeTask -> {
                                   if (storeTask.isSuccessful()) {
                                      Log.d("AnnualCalc", "Housing CO2e value stored successfully: " + sum[0]);
                                   } else {
                                      Log.e("AnnualCalc", "Error storing housing CO2e value.", storeTask.getException());
                                   }
                                });


                        if (onComplete != null) {
                           onComplete.run();
                           housing = sum[0];
                        }
                     } else {
                        Log.w("AnnualCalc", "Emission value is null for key: " + key);
                        if (onComplete != null) {
                           onComplete.run(); // Continue even on error
                           housing = 0;
                        }
                     }
                  } else {
                     Log.e("AnnualCalc", "Error fetching housing emissions.", calcTask.getException());
                  }
               });
            } else {
               Log.e("AnnualCalc", "One or more housing data fields are null: " +
                       "bill=" + bill + ", heatingSource=" + heatingSource +
                       ", householdMembers=" + householdMembers + ", size=" + size +
                       ", structureType=" + structureType);
            }
         } else {
            Log.e("AnnualCalc", "Error fetching annual answers for housing.", task.getException());
         }
      });
   }
   public void Annual_Food_emissions(Runnable onComplete) {
      double[] sum = {0};

      annual_ans_Ref.get().addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            Log.d("AnnualCalc", "Successfully fetched annual answers for food.");
            DataSnapshot dataSnapshot = task.getResult();

            String diet = dataSnapshot.child("food/diet_type").getValue(String.class);
            Log.d("AnnualCalc", "Fetched diet type: " + diet);

            String beef_f = dataSnapshot.child("food/beef_frequency").getValue(String.class);
            Log.d("AnnualCalc", "Fetched beef frequency: " + beef_f);

            String chicken_f = dataSnapshot.child("food/chicken_frequency").getValue(String.class);
            Log.d("AnnualCalc", "Fetched chicken frequency: " + chicken_f);

            String fish_f = dataSnapshot.child("food/fish_frequency").getValue(String.class);
            Log.d("AnnualCalc", "Fetched fish frequency: " + fish_f);

            String pork_f = dataSnapshot.child("food/pork_frequency").getValue(String.class);
            Log.d("AnnualCalc", "Fetched pork frequency: " + pork_f);

            String food_waste_f = dataSnapshot.child("food/food_waste_frequency").getValue(String.class);
            Log.d("AnnualCalc", "Fetched food waste frequency: " + food_waste_f);

            if (diet != null && food_waste_f != null && beef_f != null
                    && chicken_f != null && pork_f != null && fish_f != null) {
               Log.d("AnnualCalc", "Diet and food waste data are not null. Proceeding with calculations.");
               // Track the number of emissions fetches still pending
               double[] remainingTasks = {6}; // Total of 6 fetches for emissions types

               // Fetch and accumulate emissions for each type
               fetchAndAccumulateEmissions("beef_emissions", beef_f, sum, remainingTasks,"food");
               fetchAndAccumulateEmissions("chicken_emissions", chicken_f, sum, remainingTasks,"food");
               fetchAndAccumulateEmissions("fish_emissions", fish_f, sum, remainingTasks,"food");
               fetchAndAccumulateEmissions("pork_emissions", pork_f, sum, remainingTasks,"food");
               fetchAndAccumulateEmissions("food_waste_emissions", food_waste_f, sum, remainingTasks,"food");
               fetchAndAccumulateEmissions("diet_type_emissions", diet, sum, remainingTasks,"food");


               if (onComplete != null) {
                  onComplete.run();
                  food = sum[0];
               }
            } else {
               Log.e("AnnualCalc", "Food: One or more food data fields are null.");
               if (onComplete != null) {
                  onComplete.run(); // Continue even on error
                  food =0;
               }
            }
         } else {
            Log.e("AnnualCalc", "Error fetching annual answers for food: " + task.getException().getMessage());
         }
      });
   }

   private void fetchAndAccumulateEmissions(String emissionsType, String frequency, double[] sum, double[] remainingTasks,String category) {
      if (frequency.toLowerCase() != null) {
         Log.d("AnnualCalc", "fetchAndAccumulateEmissions called for " + emissionsType);

         DatabaseReference calc = databaseRef.child(emissionsType).child(frequency);
         calc.get().addOnCompleteListener(calcTask -> {
            if (calcTask.isSuccessful()) {
               Double emissions = calcTask.getResult().getValue(Double.class);
               if (emissions != null) {
                  sum[0] += emissions;
                  Log.d("AnnualCalc", "Emissions for " + emissionsType + " (" + frequency.toLowerCase() + "): " + emissions);
               } else {
                  Log.w("AnnualCalc", "Emissions value is null for " + emissionsType + " with frequency " + frequency.toLowerCase());
               }
            } else {
               Log.e("AnnualCalc", "Error fetching " + emissionsType + ": " + calcTask.getException().getMessage());
            }

            remainingTasks[0]--;

            if (remainingTasks[0] == 0) {
               annual_ans_Ref.child(category).child(category +"_co2e").setValue(sum[0])
                       .addOnCompleteListener(storeTask -> {
                          if (storeTask.isSuccessful()) {
                             Log.d("AnnualCalc",  category+ "CO2e value stored successfully: " + sum[0]);
                          } else {
                             Log.e("AnnualCalc", "Error storing food CO2e value: " + storeTask.getException().getMessage());
                          }
                       });

            }
         });
      } else {
         Log.e("AnnualCalc", emissionsType + " frequency is null, skipping.");
      }
      //if(category.equals("transportation") && emissionsType.equals("short_flight_footprint")) Total_emissions();
   }
   public void Annual_transporation_emissions(Runnable onComplete){
      double[] sum = {0};

      annual_ans_Ref.get().addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            DataSnapshot dataSnapshot = task.getResult();

            // Fetching data from Firebase
            String car = dataSnapshot.child("transportation/car_type").getValue(String.class);
            Log.d("AnnualCalc", "Fetched waste frequency: " + car);
            String distance = dataSnapshot.child("transportation/distance").getValue(String.class);
            Log.d("AnnualCalc", "Fetched waste frequency: " + distance);

            String hour_on_public_transit = dataSnapshot.child("transportation/hours_on_public_transit").getValue(String.class);
            Log.d("AnnualCalc", "Fetched frequency: " + hour_on_public_transit);

            String long_haul_flights = dataSnapshot.child("transportation/long_haul_flights").getValue(String.class);
            Log.d("AnnualCalc", "Fetched frequency: " + long_haul_flights);

            String short_haul_flights = dataSnapshot.child("transportation/short_haul_flights").getValue(String.class);
            Log.d("AnnualCalc", "Fetched frequency: " + short_haul_flights);

            String public_transit_use = dataSnapshot.child("transportation/public_transit_use").getValue(String.class);
            Log.d("AnnualCalc", "Fetched frequency: " + public_transit_use);

            // Boolean uses_car = dataSnapshot.child("transportation/uses_car").getValue(Boolean.class);

            // Check if the necessary data is not null
            if (distance != null && car != null && hour_on_public_transit != null && short_haul_flights != null && long_haul_flights != null && public_transit_use != null) {
               double[] remainingTasks = {5};

               //fetchAndAccumulateEmissions("long_flight_footprint", hour_on_public_transit, sum, remainingTasks, "transportation");
               fetchAndAccumulateEmissions("long_flight_footprint", long_haul_flights, sum, remainingTasks, "transportation");
               fetchAndAccumulateEmissions("car_type_factors", car, sum, remainingTasks, "transportation");
               fetchAndAccumulateEmissions("car_distance", distance, sum, remainingTasks, "transportation");
               if(public_transit_use.equals("never"))fetchAndAccumulateEmissions("public_transit_footprint", public_transit_use +"|"+hour_on_public_transit, sum, remainingTasks, "transportation");
               else  remainingTasks[0]--;
               fetchAndAccumulateEmissions("short_flight_footprint", short_haul_flights, sum, remainingTasks, "transportation");


               if (onComplete != null) {
                  onComplete.run();
                  transport = sum[0];
               }
            } else {
               Log.e("AnnualCalc", "T: One or more data fields are null.");
               if (onComplete != null) {
                  onComplete.run(); // Continue even on error
                  transport =0;
               }
            }

         } else {
            Log.e("AnnualCalc", "Error fetching annual answers: " + task.getException().getMessage());
         }
      });
   }


   public void Annual_consumption_emissions(Runnable onComplete){
      double[] sum = {0};

      annual_ans_Ref.get().addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            DataSnapshot dataSnapshot = task.getResult();

            // Fetch the frequencies from Firebase
            String clothing_f = dataSnapshot.child("consumption/clothing_frequency").getValue(String.class);
            String eco_friendly_f = dataSnapshot.child("consumption/eco_friendly_frequency").getValue(String.class);
            String recycling_f = dataSnapshot.child("consumption/recycling_frequency").getValue(String.class);
            String device_f = dataSnapshot.child("consumption/device_frequency").getValue(String.class);

            // Log the values to check if they're correctly fetched
            Log.d("AnnualCalc", "Fetched clothing frequency: " + clothing_f);
            Log.d("AnnualCalc", "Fetched eco-friendly frequency: " + eco_friendly_f);
            Log.d("AnnualCalc", "Fetched recycling frequency: " + recycling_f);
            Log.d("AnnualCalc", "Fetched device frequency: " + device_f);

            // Check if any of the values are null
            if (clothing_f != null && eco_friendly_f != null && device_f != null && recycling_f != null) {
               Log.d("AnnualCalc", "C: All consumption data is available. Proceeding with emissions calculations.");

               // Track the remaining tasks
               double[] remainingTasks = {4};

               // Fetch and accumulate emissions for each category
               fetchAndAccumulateEmissions("clothing_emissions", clothing_f, sum, remainingTasks,"consumption");
               fetchAndAccumulateEmissions("clothing_eco_friendly_adjustment", eco_friendly_f, sum, remainingTasks,"consumption");
               fetchAndAccumulateEmissions("clothing_recylcing_adjustment", recycling_f, sum, remainingTasks,"consumption");
               fetchAndAccumulateEmissions("device_recylcing_adjustment", recycling_f, sum, remainingTasks,"consumption");
               fetchAndAccumulateEmissions("device_emissions", device_f, sum, remainingTasks,"consumption");

               if (onComplete != null) {
                  onComplete.run();
                  consumption = sum[0];
               }

            } else {
               Log.e("AnnualCalc", "One or more consumption data fields are null.");
               if (onComplete != null) {
                  onComplete.run(); // Continue even on error
                  consumption =0;
               }
            }
         } else {
            Log.e("AnnualCalc", "Error fetching annual answers for consumption: " + task.getException().getMessage());
         }
      });
   }

   public void Total_emissions(Runnable onComplete) {
      double[] totalEmissions = {0};

      // Fetch data from Firebase
      annual_ans_Ref.get().addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            DataSnapshot dataSnapshot = task.getResult();

            // Retrieve CO2e values for different categories
            Double consumption = dataSnapshot.child("consumption/consumption_co2e").getValue(Double.class);
            Double transport = dataSnapshot.child("transportation/transportation_co2e").getValue(Double.class);
            Double housing = dataSnapshot.child("housing/housing_co2e").getValue(Double.class);
            Double food = dataSnapshot.child("food/food_co2e").getValue(Double.class);

            // Log fetched values
            Log.d("AnnualCalc", "Fetched emissions values:");
            Log.d("AnnualCalc", "Consumption: " + (consumption != null ? consumption : "null"));
            Log.d("AnnualCalc", "Transportation: " + (transport != null ? transport : "null"));
            Log.d("AnnualCalc", "Housing: " + (housing != null ? housing : "null"));
            Log.d("AnnualCalc", "Food: " + (food != null ? food : "null"));

            // Only proceed if transportation emissions are updated (not 0)
            if (transport != null && transport > 0) {
               // Add valid emission values to total emissions
               if (consumption != null) totalEmissions[0] += consumption;
               if (transport != null) totalEmissions[0] += transport;
               if (housing != null) totalEmissions[0] += housing;
               if (food != null) totalEmissions[0] += food;
               total = totalEmissions[0];

               // Log the total emissions
               Log.d("AnnualCalc", "Total emissions calculated: " + totalEmissions[0]);

               // Store the total emissions in Firebase
               annual_ans_Ref.child("annual_co2e").setValue(totalEmissions[0])
                       .addOnCompleteListener(storeTask -> {
                          if (storeTask.isSuccessful()) {
                             Log.d("AnnualCalc", "Total Emissions CO2e value stored successfully: " + totalEmissions[0]);
                          } else {
                             Log.e("AnnualCalc", "Error storing total emissions CO2e value: " + storeTask.getException().getMessage());
                          }
                       });
               if (onComplete != null) {
                  onComplete.run(); // Continue even on error
                  total = totalEmissions[0];
               }
            } else {
               // Log message when transportation emissions are not valid
               if (onComplete != null) {
                  onComplete.run(); // Continue even on error
                  total = 0;
               }
               Log.w("AnnualCalc", "Transportation CO2e is not updated or is zero. Skipping total emissions calculation.");
            }
         } else {
            Log.e("AnnualCalc", "Error fetching total emissions: " + task.getException().getMessage());
         }
      });

   }
   private void navigateToOverlayActivity() {
      if (isAdded()) {
         Intent intent = new Intent(requireContext(), OverlayActivity.class);

         // Optionally, add extra data if needed
         intent.putExtra("key", "value");

         // Start the OverlayActivity
         startActivity(intent);
      } else {
         // Handle the case where the fragment is not attached to the activity
         Log.e("AnnualCalc", "Fragment is not attached.");
      }
   }







}
