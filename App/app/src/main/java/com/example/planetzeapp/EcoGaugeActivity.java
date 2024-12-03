package com.example.planetzeapp;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.graphics.Color;
import android.widget.ScrollView;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EcoGaugeActivity extends AppCompatActivity {
    private LineChart lineChart;
    private Button btnWeek, btnMonth, btnYear;
    private Button selectedButton = null;
    private TextView totalEmissionsText;
    private TextView timePeriodText;
    private HorizontalBarChart stackedBarChart;
    private ScrollView scrollView;
    private float totalYearEmissions = 0f;
    private FirebaseAuth mAuth;
    private float dailyCO2eValue;

    private int daysFetched = 0;
    private int monthsFetched = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_gauge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ecogauge), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        //Line Chart
        lineChart = findViewById(R.id.lineChart);
        btnWeek = findViewById(R.id.btn_week);
        btnMonth = findViewById(R.id.btn_month);
        btnYear = findViewById(R.id.btn_year);
        totalEmissionsText = findViewById(R.id.totalEmissionsText);
        timePeriodText = findViewById(R.id.timePeriodText);

        //display week chart by default
        selectButton(btnWeek);
        btnWeek.setTextColor(getResources().getColorStateList(R.color.button_text_selector));
        List<String> weekLabels = generateWeekLabels();
        getWeekData(currentUserId);

        btnWeek.setOnClickListener(view -> {
            selectButton(btnWeek);
            getWeekData(currentUserId);
        });
        btnMonth.setOnClickListener(view -> {
            selectButton(btnMonth);
            getMonthData(currentUserId);
        });
        btnYear.setOnClickListener(view -> {
            selectButton(btnYear);
            getYearData(currentUserId);
        });

        //Horizontal Bar Chart
        stackedBarChart = findViewById(R.id.stackedBarChart);
        setupStackedBarChart(currentUserId);

        //Bar Chart
        BarChart barChart = findViewById(R.id.barChart);
        scrollView = findViewById(R.id.scrollView);
        checkIfChartIsVisible(barChart);
        setupBarChart(currentUserId, barChart);

        if (savedInstanceState == null) {
            FooterFragment footerFragment = new FooterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.footer_container, footerFragment); // You can use add() or replace()
            transaction.commit();
        }
    }

    private void updateChart(List<Entry> data, List<String> xAxisLabels, String label) {
        LineDataSet dataSet = new LineDataSet(data, label);
        dataSet.setColor(Color.parseColor("#009999"));
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4D009999")); // Semi-transparent fill
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        //display total emissions
        float totalEmissions = 0f;
        for (Entry entry : data) {
            totalEmissions += entry.getY();
        }

        String timePeriod = label.contains("Weekly") ? "this week" :
                label.contains("Monthly") ? "this month" :
                        label.contains("Yearly") ? "this year" : "this period";
        totalEmissionsText.setText(String.format("%.1f kg CO2e", totalEmissions));
        timePeriodText.setText(String.format("emitted %s", timePeriod));
        //configure X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setGridLineWidth(0.5f);
        xAxis.enableGridDashedLine(10f, 5f, 0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < xAxisLabels.size()) {
                    return xAxisLabels.get(index);
                }
                return ""; // Default empty for invalid indices
            }
        });
        TextView monthRangeTitle = findViewById(R.id.monthRangeTitle);
        if (label.contains("Monthly")) {
            monthRangeTitle.setText(generateMonthRangeTitle());
        } else {
            monthRangeTitle.setText("");
        }
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisLineColor(Color.BLACK);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.LTGRAY);
        lineChart.getAxisLeft().setGridLineWidth(0.5f);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate(); // Refresh chart
    }

    public interface OnDailyCO2eFetched {
        void onCO2eFetched(float dailyCO2e);
    }

    private void getWeekData(String userId) {
        List<Entry> data = new ArrayList<>(); // List to store the entries for the chart
        Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
        daysFetched = 0; // Reset the counter before fetching

        // Loop to iterate over the last 7 days
        for (int i = 0; i <= 7; i++) {
            final int dayIndex = i; // Store the current day index (for use in the callback)
            Calendar tempCalendar = (Calendar) calendar.clone(); // Clone the calendar object to modify it
            tempCalendar.add(Calendar.DAY_OF_YEAR, -7 + i); // Subtract i days from today to get the past days

            // Format the date in yyyy-MM-dd format to match the Firebase data structure
            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.getTime());
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("daily_answers").child(dateKey);

            // Call the method to check if the date exists in the database
            checkIfDateExists(dataRef, new OnDailyCO2eFetched() {
                @Override
                public void onCO2eFetched(float dailyCO2e) {
                    // When the data is fetched for a day, add it to the data list
                    data.add(new Entry(dayIndex, dailyCO2e));

                    // Increment the counter
                    daysFetched++;
                    if (daysFetched == 8) { // All days fetched
                        // Update the chart with the collected data
                        updateChart(data, generateWeekLabels(), "Weekly Carbon Emissions");
                    }
                }
            });
        }
    }

    private void checkIfDateExists(DatabaseReference dataRef, final OnDailyCO2eFetched callback) {
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double dailyCo2e = snapshot.child("daily_CO2e").getValue(Double.class); // The date exists
                    callback.onCO2eFetched(dailyCo2e.floatValue());
                } else {
                    callback.onCO2eFetched(0f); // The date does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error checking date: " + error.getMessage());
                callback.onCO2eFetched(0f);
            }

        });
    }

    private List<String> generateWeekLabels() {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weekdays = new DateFormatSymbols().getShortWeekdays();
        for (int i = 0; i < 7; i++) {
            int dayIndex = (today + i - 1) % 7 + 1;
            days.add(weekdays[dayIndex]);
        }
        days.add(weekdays[today]);
        return days;
    }

    private void getMonthData(String userId) {
        List<Entry> data = new ArrayList<>(); // List to store the entries for the chart
        Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
        daysFetched = 0; // Reset the counter before fetching

        // Loop to iterate over the last 7 days
        for (int i = 0; i <= 29; i++) {
            final int dayIndex = i; // Store the current day index (for use in the callback)
            Calendar tempCalendar = (Calendar) calendar.clone(); // Clone the calendar object to modify it
            tempCalendar.add(Calendar.DAY_OF_YEAR, -29 + i); // Subtract i days from today to get the past days

            // Format the date in yyyy-MM-dd format to match the Firebase data structure
            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.getTime());
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("daily_answers").child(dateKey);

            // Call the method to check if the date exists in the database
            checkIfDateExists(dataRef, new OnDailyCO2eFetched() {
                @Override
                public void onCO2eFetched(float dailyCO2e) {
                    // When the data is fetched for a day, add it to the data list
                    data.add(new Entry(dayIndex, dailyCO2e));

                    // Increment the counter
                    daysFetched++;
                    if (daysFetched == 30) { // All days fetched
                        // Update the chart with the collected data
                        updateChart(data, generateMonthLabels(), "Monthly Carbon Emissions");
                    }
                }
            });
        }
    }

    private List<String> generateMonthLabels() {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); // Start with today
        calendar.add(Calendar.DAY_OF_MONTH, -29); // Go back 29 days (including today makes 30)
        for (int i = 0; i < 30; i++) {
            days.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))); // Add day of the month
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }
        return days;
    }

    private String generateMonthRangeTitle() {
        Calendar calendar = Calendar.getInstance(); // Today
        String currentMonth = new DateFormatSymbols().getShortMonths()[calendar.get(Calendar.MONTH)];
        calendar.add(Calendar.DAY_OF_MONTH, -29); // Go back 29 days
        String startMonth = new DateFormatSymbols().getShortMonths()[calendar.get(Calendar.MONTH)];
        if (startMonth.equals(currentMonth)) {
            return startMonth; // Single month, e.g., "Jan"
        } else {
            return startMonth + "-" + currentMonth; // Crosses months, e.g., "Dec-Jan"
        }
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private void getYearData(String userId) {
        List<Entry> data = new ArrayList<>(); // List to store the entries for the chart
        Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
        monthsFetched = 0; // Reset the counter before fetching

        // Loop to iterate over the last 7 days
        for (int i = 0; i <= 11; i++) {
            final int monthIndex = i; // Store the current day index (for use in the callback)
            Calendar tempCalendar = (Calendar) calendar.clone(); // Clone the calendar object to modify it
            tempCalendar.add(Calendar.MONTH, -11 + i); // Subtract i days from today to get the past days

            tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDayofMonth = tempCalendar.getTime();
            tempCalendar.set(Calendar.DAY_OF_MONTH, tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date lastDayofMonth = tempCalendar.getTime();

            final float[] totalMonthlyCO2e = {0f};

            for (Date date = firstDayofMonth; !date.after(lastDayofMonth); date = addDays(date, 1)) {
                final Date currentDate = new Date(date.getTime());

                String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("daily_answers").child(dateKey);

                checkIfDateExists(dataRef, new OnDailyCO2eFetched() {
                    @Override
                    public void onCO2eFetched(float dailyCO2e) {
                        totalMonthlyCO2e[0] += dailyCO2e;
                        if (currentDate.equals(lastDayofMonth)) {
                            data.add(new Entry(monthIndex, totalMonthlyCO2e[0]));
                            monthsFetched++;
                            if (monthsFetched == 12) {
                                updateChart(data, generateYearLabels(),
                                        "Yearly Carbon Emissions");
                            }
                        }
                    }
                });

            }
            ;

        }
    }

    private List<String> generateYearLabels() {
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        String[] monthsOfYear = new DateFormatSymbols().getShortMonths();
        for (int i = 11; i >= 0; i--) {
            int monthIndex = (thisMonth - i + 12) % 12;
            months.add(monthsOfYear[monthIndex]);
        }
        months.add(monthsOfYear[thisMonth]);
        return months;
    }

    private void selectButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setSelected(false); // Deselect previous button
        }
        button.setSelected(true); // Select the clicked button
        selectedButton = button; // Update selectedButton reference
    }

    interface OnCategoryPercentagesCalculated {
        void onPercentagesCalculated(float[] percentages);
    }

    private void calculateCategoryPercentages(String userId, final OnCategoryPercentagesCalculated callback) {
        final double[] categoryTotals = {0.0, 0.0, 0.0, 0.0}; //Transportation, Energy, Food, Shopping
        final double[] totalCO2e = {0.0};
        Calendar calendar = Calendar.getInstance();
        final int[] daysFetched = {0};

        for (int i = 0; i <= 29; i++) {
            Calendar tempCalendar = (Calendar) calendar.clone();
            tempCalendar.add(Calendar.DAY_OF_YEAR, -i);

            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.getTime());
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("daily_answers").child(dateKey);

            dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        double dailyCO2e = snapshot.child("daily_CO2e").getValue(Double.class);
                        totalCO2e[0] += dailyCO2e;

                        categoryTotals[0] += snapshot.child("Transportation").child("Transportation_CO2e")
                                .getValue(Double.class) != null ? snapshot.child("Transportation").child("Transportation_CO2e").getValue(Double.class) : 0.0;
                        categoryTotals[1] += snapshot.child("Energy").child("Energy_CO2e")
                                .getValue(Double.class) != null ? snapshot.child("Energy").child("Energy_CO2e").getValue(Double.class) : 0.0;
                        categoryTotals[2] += snapshot.child("Food").child("Food_CO2e")
                                .getValue(Double.class) != null ? snapshot.child("Food").child("Food_CO2e").getValue(Double.class) : 0.0;
                        categoryTotals[3] += snapshot.child("Consumption").child("Consumption_CO2e")
                                .getValue(Double.class) != null ? snapshot.child("Consumption").child("Consumption_CO2e").getValue(Double.class) : 0.0;
                    }

                    daysFetched[0]++;
                    if (daysFetched[0] == 30) {
                        float[] percentages = new float[4];
                        if (totalCO2e[0] == 0.0) {
                            for (int j = 0; j < 4; j++) {
                                percentages[j] = 0.0f;
                            }
                        } else {
                            float cumulativePercentage = 0f;
                            for (int j = 0; j < 3; j++) { // Calculate for the first three categories
                                percentages[j] = (float) (categoryTotals[j] / totalCO2e[0]) * 100;
                                cumulativePercentage += percentages[j];
                            }
                            percentages[3] = 100f - cumulativePercentage; // Assign remaining percentage to the last category
                        }
                        callback.onPercentagesCalculated(percentages);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Error", "Error fetching data: " + error.getMessage());
                }
            });
        }
    }

    private void setupStackedBarChart(String userId) {
        calculateCategoryPercentages(userId, new OnCategoryPercentagesCalculated() {
            @Override
            public void onPercentagesCalculated(float[] percentages) {
                float[] emissions = percentages;
                float[] percentagesCopy = percentages;
                String[] categories = {"Transportation", "Energy", "Food", "Shopping"};

                List<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0f, emissions));

                BarDataSet dataSet = new BarDataSet(entries, "");
                dataSet.setColors(new int[]{
                        Color.parseColor("#D8DBE2"),  // Transportation
                        Color.parseColor("#009999"),  // Energy Use
                        Color.parseColor("#373F51"),  // Food
                        Color.parseColor("#A9BCD0"),  // Shopping
                });
                dataSet.setStackLabels(categories);

                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format("%.0f%%", value); // Format as percentages
                    }
                });

                // Enable values to be drawn above each segment
                dataSet.setDrawValues(false);
                // Create BarData and assign it to the chart
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(1f);
                stackedBarChart.setData(barData);
                // X-axis customization
                XAxis xAxis = stackedBarChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position at bottom for horizontal layout
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(false); // Hide X-axis labels
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setAxisMinimum(0f);
                // Y-axis customization
                stackedBarChart.getAxisRight().setEnabled(false); // Disable right Y-axis
                stackedBarChart.getAxisLeft().setAxisMinimum(0f);
                stackedBarChart.getAxisLeft().setAxisMaximum(100f); // 100% for percentages
                stackedBarChart.getAxisLeft().setGranularity(10f);
                stackedBarChart.getAxisLeft().setDrawGridLines(false);
                stackedBarChart.getAxisLeft().setEnabled(false); // Hide left Y-axis for cleaner look
                // Remove description text
                stackedBarChart.getDescription().setEnabled(false);
                // Enable the legend and customize it
                Legend legend = stackedBarChart.getLegend();
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);
                legend.setXEntrySpace(35f);
                legend.setYEntrySpace(0f);
                legend.setWordWrapEnabled(true);
                legend.setFormToTextSpace(3f);
                legend.setMaxSizePercent(0.85f);

                // Modify the legend entries to show percentages
                ArrayList<LegendEntry> legendEntries = new ArrayList<>();
                if (percentages[0] == 0.0f && percentages[1] == 0.0f && percentages[2] == 0.0f && percentages[3] == 0.0f) {
                    legendEntries.add(new LegendEntry("No emissions this month", Legend.LegendForm.SQUARE, 10f, 10f, null, Color.GRAY));
                } else {
                    for (int i = 0; i < categories.length; i++) {
                        String label = categories[i] + " (" + String.format("%.0f%%", percentages[i]) + ")";
                        int color = dataSet.getColors().get(i);
                        legendEntries.add(new LegendEntry(label, Legend.LegendForm.SQUARE, 10f, 10f, null, color));
                    }
                }
                // Set custom legend entries
                legend.setCustom(legendEntries);
                // Disable interactions
                stackedBarChart.setTouchEnabled(false);
                stackedBarChart.setDragEnabled(false);
                stackedBarChart.setScaleEnabled(false);
                // Set extra offsets for spacing
                stackedBarChart.setExtraOffsets(0f, 0f, 10f, 10f);
                // Refresh the chart
                stackedBarChart.invalidate();
                stackedBarChart.post(new Runnable() {
                    @Override
                    public void run() {
                        // Add animation for the bar chart to come from the left (horizontal animation)
                        stackedBarChart.animateY(1500, Easing.EaseInOutQuart);  // 1500ms duration, smooth easing
                    }
                });
            }
        });
    }

    public interface CountryCallback {
        void onCountryFetched(String country);
    }

    private void getUserCountry(String userId, CountryCallback callback) {
        DatabaseReference countryRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("country");

        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if (snapshot.exists()){
                    String country = snapshot.getValue(String.class);
                    callback.onCountryFetched(country);
                }
                else{
                    callback.onCountryFetched("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                callback.onCountryFetched(""); // Set to empty string on error
            }
        });
    }

    private void setupBarChart(String userId, BarChart chart) {
        float globalEmissions = 4.658219f;  // Global average
        float[] nationalEmissions = {0f};  // National average
        float[] personEmissions = {0f};  // Person's emission average
        final boolean[] fetchComplete = {false, false};  // Flags for fetch completion

        getUserCountry(userId, new CountryCallback() {
            @Override
            public void onCountryFetched(String country) {
                if (!country.isEmpty()) {
                    DatabaseReference nationalAvgRef = FirebaseDatabase.getInstance().getReference("avg_annual_emissions").child(country).child("emissions_per_capita");

                    nationalAvgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                nationalEmissions[0] = snapshot.getValue(Double.class).floatValue();
                                Log.d("NationalEmissions", "Fetched national emissions: " + nationalEmissions[0]);
                            } else {
                                nationalEmissions[0] = 0f;
                            }
                            fetchComplete[0] = true;
                            setupChart(globalEmissions, nationalEmissions[0], personEmissions[0], chart, fetchComplete);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Error fetching national emissions: " + error.getMessage());
                            fetchComplete[0] = true;
                            setupChart(globalEmissions, nationalEmissions[0], personEmissions[0], chart, fetchComplete);
                        }
                    });
                } else {
                    Log.d("CountryFetch", "User country is not set.");
                }
            }
        });

        //fetch user's average
        DatabaseReference userAvgRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("annual_answers");

        userAvgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DataSnapshot", snapshot.toString());
                if(snapshot.exists()){
                    Double annualCo2e = snapshot.child("annual_co2e").getValue(Double.class);
                    if(annualCo2e != null){
                        personEmissions[0] = annualCo2e.floatValue() / 1000f;
                    } else{
                        personEmissions[0] = 0f;
                    }
                }
                else{
                    personEmissions[0] = 0f;
                }

                fetchComplete[1] = true;
                setupChart(globalEmissions, nationalEmissions[0], personEmissions[0], chart, fetchComplete);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error fetching data: " + error.getMessage());
                fetchComplete[1] = true;  // Still mark it as fetched to avoid blocking
                setupChart(globalEmissions, nationalEmissions[0], personEmissions[0], chart, fetchComplete);
            }
        });
    }

    private void setupChart(float globalEmissions, float nationalEmissions, float personEmissions, BarChart chart, boolean[] fetchComplete){
        if (fetchComplete[0] && fetchComplete[1]){
            Log.d("ChartData", "Global: " + globalEmissions + ", National: " + nationalEmissions + ", Person: " + personEmissions);
            float[] emissions = {globalEmissions, nationalEmissions, personEmissions};
            float maxEmission = 0f;
            for (float emission : emissions) {
                if (emission > maxEmission) {
                    maxEmission = emission;
                }
            }
            maxEmission = maxEmission * 1.1f;
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, globalEmissions));  // Bar for Global emissions
            entries.add(new BarEntry(1f, nationalEmissions));  // Bar for National emissions
            entries.add(new BarEntry(2f, personEmissions));  // Bar for Person emissions
            // Create a BarDataSet for the three bars
            BarDataSet barDataSet = new BarDataSet(entries, "Emissions Comparison");
            barDataSet.setColors(new int[]{
                    Color.parseColor("#373F51"), // Color for Global
                    Color.parseColor("#D8DBE2"), // Color for National
                    Color.parseColor("#009999")  // Color for Person
            });
            barDataSet.setValueTextSize(12f); // Size of the value text
            barDataSet.setValueTextColor(Color.BLACK); // Text color for the value
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.45f);
            // Set the data to the chart
            BarChart barChart = findViewById(R.id.barChart);
            barChart.setData(barData);
            // X-axis customization
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Set X-axis at the bottom
            barChart.getXAxis().setGranularity(1f); // Ensure bars don't overlap
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"global average", "national average", "you"})); // Labels for the bars
            barChart.getData().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    // Format the value to 1 decimal place and include "tonnes" with a line break
                    return String.format("%.1f tonnes", value);
                }
            });
            barChart.getXAxis().setDrawGridLines(false); // No grid lines
            barChart.getXAxis().setDrawLabels(true); // Show the X-axis labels
            // Customize the chart
            barChart.getDescription().setEnabled(false); // Hide description
            barChart.getLegend().setEnabled(false); // Hide legend
            barChart.getAxisLeft().setEnabled(false); // Disable the left Y-axis
            barChart.getAxisRight().setEnabled(false); // Disable the right Y-axis
            barChart.getAxisLeft().setDrawGridLines(false); // No grid lines on the left Y-axis
            barChart.getAxisLeft().setAxisMinimum(0f);  // Start from zero
            barChart.getAxisLeft().setAxisMaximum(maxEmission); // Set max to dynamic value (with a bit of padding)
            barChart.getAxisLeft().setGranularity(maxEmission / 5f);
            // Refresh the chart to apply changes
            barChart.invalidate();
        }
    }
    private void animateBarsIndividually(final BarChart barChart) {
        // Define the delay for each bar
        final long delay = 750; // Delay between each bar's animation (in milliseconds)
        // Loop through the entries to animate them one by one
        for (int i = 0; i < barChart.getBarData().getDataSetCount(); i++) {
            final int index = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Here, we animate the Y axis, but it only animates after the delay
                    barChart.setVisibility(View.VISIBLE);
                    barChart.animateY(1500, Easing.EaseInOutQuad);  // Adjust duration for the animation
                }
            }, delay * (index + 1));  // Each bar has a delay based on its index
        }
    }
    private void checkIfChartIsVisible(final BarChart barChart) {
        barChart.setVisibility(View.INVISIBLE);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (isViewFullyVisible(barChart)) {
                    scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Animate the bars
                        animateBarsIndividually(barChart);
                    });
                }
            }
        });
    }
    private boolean isViewFullyVisible(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        // Check if the entire view is within the visible portion of the screen
        return rect.top >= 0 && rect.bottom <= screenHeight;
    }
}
