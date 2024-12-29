package com.pacman.MentAlly.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.home.api.ApiService;
import com.pacman.MentAlly.ui.home.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {
    private BarChart barChart;
    private LineChart lineChart;
    private ApiService apiService;

    private final int[] SENTIMENT_COLORS = {
            Color.rgb(255, 87, 51),    // Rouge pour "angry"
            Color.rgb(255, 195, 0),    // Jaune pour "happy"
            Color.rgb(66, 134, 244),    // Bleu pour "sad"
            Color.rgb(76, 175, 80),     // Vert pour "good"
            Color.rgb(156, 39, 176),    // Violet pour "anxious"
            Color.rgb(239, 83, 80),     // Rouge clair pour "bad"
            Color.rgb(255, 152, 0),     // Orange pour "excited"
            Color.rgb(158, 158, 158),    // Gris pour "bored"
            Color.rgb(63, 81, 181)      // Indigo pour "depressed"
    };

    private final String[] SENTIMENT_LABELS = {
            "angry", "happy", "sad", "good",
            "anxious", "bad", "excited", "bored", "depressed"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initializeCharts();
        createSentimentLegend();
        fetchUserStatistics();
    }

    private void createSentimentLegend() {
        LinearLayout legendLayout = findViewById(R.id.legendLayout);

        for (int i = 0; i < SENTIMENT_COLORS.length; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8); // Add some padding

            View colorView = new View(this);
            colorView.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
            colorView.setBackgroundColor(SENTIMENT_COLORS[i]);

            TextView textView = new TextView(this);
            textView.setText(capitalizeFirstLetter(SENTIMENT_LABELS[i]));
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            row.addView(colorView);
            row.addView(textView);
            legendLayout.addView(row);
        }
    }

    private void initializeCharts() {
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        apiService = RetrofitClient.getInstance().getApiService();

        configureBarChart();
        configureLineChart();
    }

    private void configureBarChart() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setScaleEnabled(false);
        barChart.animateY(1500);

        Legend barLegend = barChart.getLegend();
        barLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        barLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        barLegend.setTextSize(12f);
        barLegend.setTextColor(Color.BLACK);
    }

    private void configureLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.animateX(1500);

        Legend lineLegend = lineChart.getLegend();
        lineLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lineLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        lineLegend.setTextSize(12f);
        lineLegend.setTextColor(Color.BLACK);
    }

    private void fetchUserStatistics() {
        Long userId = getUserId(); // Dynamically get user ID from SharedPreferences
        if (userId != null) {
            apiService.getUserStatistics(String.valueOf(userId)).enqueue(new Callback<Map<String, Integer>>() {
                @Override
                public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        displayStatistics(response.body());
                    } else {
                        Log.d("StatisticsActivity", "No statistics available or error code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                    Log.e("StatisticsActivity", "Error fetching statistics", t);
                }
            });
        }
    }

    private Long getUserId() {
        // Retrieve the user ID from SharedPreferences
        return getSharedPreferences("MentAllyPrefs", MODE_PRIVATE).getLong("userId", -1L);
    }

    private void displayStatistics(Map<String, Integer> stats) {
        List<BarEntry> barEntries = new ArrayList<>();
        List<Entry> lineEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int index = 0; index < SENTIMENT_LABELS.length; index++) {
            String sentiment = SENTIMENT_LABELS[index];
            Integer value = stats.get(sentiment);

            if (value != null) {
                barEntries.add(new BarEntry(index, value));
                lineEntries.add(new Entry(index, value));
                labels.add(capitalizeFirstLetter(sentiment)); // Add sentiment label
            } else {
                barEntries.add(new BarEntry(index, 0));
                lineEntries.add(new Entry(index, 0));
                labels.add(capitalizeFirstLetter(sentiment));
            }
        }

        // Configuration du BarChart
        BarDataSet barDataSet = new BarDataSet(barEntries, "Émotions");
        barDataSet.setColors(SENTIMENT_COLORS);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);
        barChart.setData(barData);

        // Configuration du LineChart
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Évolution des émotions");
        lineDataSet.setColor(Color.rgb(66, 134, 244));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.rgb(66, 134, 244));
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(66, 134, 244));
        lineDataSet.setFillAlpha(50);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Configuration des axes X pour les deux graphiques
        setupXAxis(barChart.getXAxis(), labels);
        setupXAxis(lineChart.getXAxis(), labels);

        // Mise à jour des graphiques
        barChart.invalidate();
        lineChart.invalidate();
    }

    private void setupXAxis(XAxis xAxis, List<String> labels) {
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(labels.size(), true); // Set label count
        xAxis.setAxisMinimum(0); // Set minimum value
        xAxis.setAxisMaximum(labels.size() - 1); // Set maximum value
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}