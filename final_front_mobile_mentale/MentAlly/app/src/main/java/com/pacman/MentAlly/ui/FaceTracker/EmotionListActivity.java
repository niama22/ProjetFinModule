package com.pacman.MentAlly.ui.FaceTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pacman.MentAlly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmotionListActivity extends AppCompatActivity {
    private Long userId;
    private SharedPreferences prefs;
    private FeelingApiService apiService;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_list);

        prefs = getSharedPreferences("MentAllyPrefs", Context.MODE_PRIVATE);
        userId = getCurrentUserId();
        initializeRetrofit();
        barChart = findViewById(R.id.barChart);
        fetchEmotions(); // Fetch emotions to display in the chart
    }

    private void fetchEmotions() {
        apiService.getFeelingsByUserId(userId).enqueue(new Callback<List<Feeling>>() {
            @Override
            public void onResponse(Call<List<Feeling>> call, Response<List<Feeling>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Feeling> feelings = response.body();
                    displayStatistics(feelings);
                } else {
                    Toast.makeText(EmotionListActivity.this, "No emotions found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Feeling>> call, Throwable t) {
                Log.e("EmotionListActivity", "Error fetching emotions", t);
                Toast.makeText(EmotionListActivity.this, "Error fetching emotions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStatistics(List<Feeling> feelings) {
        Map<String, Integer> emotionCounts = new HashMap<>();

        for (Feeling feeling : feelings) {
            String emotion = feeling.getEmotion(); // Assuming getEmotion() returns the emotion as a string
            emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
        }

        // Prepare data for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : emotionCounts.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey()); // Add the emotion as a label
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Emotions");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);

        // Enable value labels on top of the bars
        dataSet.setValueTextSize(12f); // Set text size for value labels
        dataSet.setValueTextColor(Color.BLACK); // Set text color for value labels
        dataSet.setDrawValues(true); // Ensure values are drawn on the bars

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels)); // Set labels for x-axis
        barChart.getXAxis().setGranularity(1f); // Set granularity to ensure labels are shown
        barChart.setFitBars(true); // Makes the x-axis fit the bars to the chart
        barChart.invalidate(); // Refresh the chart
    }

    private Long getCurrentUserId() {
        return prefs.getLong("userId", -1L);
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8083/") // Replace with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(FeelingApiService.class);
    }
}