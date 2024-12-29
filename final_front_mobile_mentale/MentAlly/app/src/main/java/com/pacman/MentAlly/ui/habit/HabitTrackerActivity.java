package com.pacman.MentAlly.ui.habit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.habit.api.RetrofitClient;
import com.pacman.MentAlly.ui.home.MainActivity;
import com.pacman.MentAlly.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitTrackerActivity extends MainActivity {

    private static final String TAG = "HabitTrackerActivity";
    private final ArrayList<Habit> habitList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HabitAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.frag_container);
        getLayoutInflater().inflate(R.layout.activity_habit_tracker, contentFrameLayout);

        // Récupérer l'ID utilisateur depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        // Vérifier si l'utilisateur est connecté
        if (userId == -1L) {
            Log.w(TAG, "No valid userId found, redirecting to login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialisation des vues
        recyclerView = findViewById(R.id.habitlist);
        recyclerView.getBackground().setAlpha(50);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadHabits);

        Button newHabit = findViewById(R.id.new_habit);
        newHabit.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), NewHabitActivity.class);
            startActivity(i);
        });

        // Initialisation du RecyclerView
        adapter = new HabitAdapter(this, habitList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Chargement initial des habitudes
        loadHabits();
    }

    private void loadHabits() {
        Log.d(TAG, "Loading habits for userId: " + userId);

        RetrofitClient.getInstance().getApiService().getUserHabits(userId)
                .enqueue(new Callback<List<Habit>>() {
                    @Override
                    public void onResponse(Call<List<Habit>> call, Response<List<Habit>> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            habitList.clear();
                            habitList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Successfully loaded " + habitList.size() + " habits");
                        } else {
                            Log.e(TAG, "Error loading habits: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    String error = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + error);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }
                            }
                            Toast.makeText(HabitTrackerActivity.this,
                                    "Erreur lors du chargement des habitudes: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Habit>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "Network error loading habits", t);
                        Toast.makeText(HabitTrackerActivity.this,
                                "Erreur de connexion: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier si l'utilisateur est toujours connecté
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        Long currentUserId = prefs.getLong("userId", -1L);
        if (currentUserId == -1L || !currentUserId.equals(userId)) {
            Log.w(TAG, "User session changed or invalid, redirecting to login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            loadHabits();
        }
    }
}