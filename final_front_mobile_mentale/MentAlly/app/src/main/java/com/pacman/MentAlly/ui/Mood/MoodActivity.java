package com.pacman.MentAlly.ui.Mood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.Mood.api.MoodApi;
import com.pacman.MentAlly.ui.Mood.api.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoodActivity extends AppCompatActivity {
    private static final String TAG = "MoodActivity";
    private static final int ADD_MOOD_REQUEST = 1;

    private MoodApi moodApi;
    private String userEmailOrId;
    private ListView listView;
    private Button addMoodButton;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        initializeViews();
        setupApi();
        setupAddButton();
        loadMoods();
    }

    private void initializeViews() {
        listView = findViewById(R.id.moodlist);
        addMoodButton = findViewById(R.id.addmood);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
    }

    private void setupApi() {
        moodApi = RetrofitClient.getClient().create(MoodApi.class);
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);

        Long userId = prefs.getLong("userId", -1L);

        Log.d(TAG, "Retrieved user ID: " + userId);

        if (userId == -1L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Store userId as String for backward compatibility with existing code
        userEmailOrId = String.valueOf(userId);
    }

    private void setupAddButton() {
        addMoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(MoodActivity.this, AddMoodActivity.class);
            startActivityForResult(intent, ADD_MOOD_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MOOD_REQUEST && resultCode == RESULT_OK) {
            loadMoods();
        }
    }

    private void loadMoods() {
        progressBar.setVisibility(View.VISIBLE);
        Long userId = Long.parseLong(userEmailOrId);
        Log.d(TAG, "Loading moods for user ID: " + userId);

        Call<List<Mood>> call = moodApi.getUserMoods(userId);
        call.enqueue(new Callback<List<Mood>>() {
            @Override
            public void onResponse(Call<List<Mood>> call, Response<List<Mood>> response) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Mood> moods = response.body();
                    Log.d(TAG, "Received " + moods.size() + " moods");

                    if (moods.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        MoodAdapter adapter = new MoodAdapter(MoodActivity.this, moods);
                        listView.setAdapter(adapter);
                    }
                } else {
                    try {
                        Log.e(TAG, "Error: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
                        Toast.makeText(MoodActivity.this,
                                "Failed to load moods", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Mood>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error", t);
                Toast.makeText(MoodActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}