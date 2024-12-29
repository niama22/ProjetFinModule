package com.pacman.MentAlly.ui.habit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.habit.api.RetrofitClient;
import com.pacman.MentAlly.ui.home.MainActivity;
import com.pacman.MentAlly.ui.login.LoginActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewHabitActivity extends MainActivity {
    private static final String TAG = "NewHabitActivity";
    private EditText habitName;
    private EditText habitEndDate;
    private Spinner trackingFrequency;
    private Button newHabit;
    private Long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.frag_container);
        getLayoutInflater().inflate(R.layout.activity_new_habit, contentFrameLayout);

        // Récupérer l'ID utilisateur
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (userId == -1L) {
            Log.w(TAG, "No valid userId found, redirecting to login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialiser les vues
        habitName = findViewById(R.id.habit_name);
        habitEndDate = findViewById(R.id.habit_end_date);
        trackingFrequency = findViewById(R.id.habit_frequency);
        newHabit = findViewById(R.id.add_new_habit);

        // Configuration du Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.freq, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trackingFrequency.setAdapter(adapter);

        // Configuration du DatePicker
        habitEndDate.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment(habitEndDate);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // Configuration du bouton d'ajout
        newHabit.setOnClickListener(v -> addHabit());
    }
    private void addHabit() {
        if (!validateInputs()) {
            return;
        }

        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setHabitName(habitName.getText().toString().trim());

        // Date de début (aujourd'hui)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = sdf.format(Calendar.getInstance().getTime());
        habit.setStartDate(startDate);

        // Date de fin - convertir au format correct
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        habit.setEndDate(outputFormat.format(new Date(habitEndDate.getText().toString())));

        // Fréquence
        habit.setFrequency(trackingFrequency.getSelectedItem().toString());

        // Progress initial
        habit.setProgress(0);
        habit.setMaxProgress(0);

        Log.d(TAG, "Creating new habit: " + habit);

        // Appel API
        RetrofitClient.getInstance().getApiService().createHabit(habit)
                .enqueue(new Callback<Habit>() {
                    @Override
                    public void onResponse(Call<Habit> call, Response<Habit> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Habit created successfully");
                            Toast.makeText(NewHabitActivity.this,
                                    "Habitude créée avec succès", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Error creating habit: " + response.code() +
                                        " - " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            Toast.makeText(NewHabitActivity.this,
                                    "Erreur lors de la création de l'habitude",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Habit> call, Throwable t) {
                        Log.e(TAG, "Network error creating habit", t);
                        Toast.makeText(NewHabitActivity.this,
                                "Erreur de connexion: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs() {
        String name = habitName.getText().toString().trim();
        String endDate = habitEndDate.getText().toString().trim();

        if (name.isEmpty()) {
            habitName.setError("Le nom est requis");
            return false;
        }

        if (endDate.isEmpty()) {
            habitEndDate.setError("La date de fin est requise");
            return false;
        }

        return true;
    }
}