package com.pacman.MentAlly.ui.Mood;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.Mood.api.MoodApi;
import com.pacman.MentAlly.ui.Mood.api.RetrofitClient;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoodActivity extends AppCompatActivity {
    private static final String TAG = "AddMoodActivity";

    private MoodApi moodApi;
    private Long userId;
    private String moodType;
    private TextView selectDate;
    private EditText descBox;
    private Button addMoodButton;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Mood mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmood);

        mood = new Mood();

        moodApi = RetrofitClient.getClient().create(MoodApi.class);
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);

        userId = prefs.getLong("userId", -1L);

        if (userId == -1L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mood.setUserId(userId);
        Log.d(TAG, "User ID set: " + userId);

        initializeViews();
        setupDatePicker();
        setupMoodButtons();
    }

    private void initializeViews() {
        selectDate = findViewById(R.id.selectdate);
        descBox = findViewById(R.id.descbox);
        addMoodButton = findViewById(R.id.addmood);
        addMoodButton.setEnabled(false);

        addMoodButton.setOnClickListener(v -> saveMood());
    }

    private void setupDatePicker() {
        selectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    AddMoodActivity.this,
                    android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });

        dateSetListener = (view, year, month, day) -> {
            month = month + 1;
            String date = day + "/" + month + "/" + year;
            selectDate.setText(date);
            mood.setDate(date);
            checkEnableAddButton();
        };
    }

    private void setupMoodButtons() {
        setupMoodButton(R.id.happy, "happy", "I feel happy");
        setupMoodButton(R.id.sad, "sad", "I feel sad");
        setupMoodButton(R.id.cool, "cool", "I feel cool");
        setupMoodButton(R.id.scared, "scared", "I feel scared");
        setupMoodButton(R.id.lovely, "lovely", "I feel lovely");
        setupMoodButton(R.id.depressed, "depressed", "I feel depressed");
        setupMoodButton(R.id.flushed, "flushed", "I feel flushed");
        setupMoodButton(R.id.angel, "angel", "I feel angelic");
        setupMoodButton(R.id.neutral, "neutral", "I feel neutral");
        setupMoodButton(R.id.sick, "sick", "I feel sick");
        setupMoodButton(R.id.nerd, "nerd", "I feel nerdy");
        setupMoodButton(R.id.sleepy, "sleepy", "I feel sleepy");
        setupMoodButton(R.id.devil, "devil", "I feel devilish");
        setupMoodButton(R.id.angry, "angry", "I feel angry");
    }

    private void setupMoodButton(int buttonId, String moodTypeValue, String toastMessage) {
        View button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                moodType = moodTypeValue;
                mood.setMoodType(moodType);
                Toast.makeText(AddMoodActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                checkEnableAddButton();
            });
        }
    }

    private void checkEnableAddButton() {
        boolean dateSelected = !selectDate.getText().toString().isEmpty();
        boolean moodSelected = moodType != null && !moodType.isEmpty();
        addMoodButton.setEnabled(dateSelected && moodSelected);
    }

    private void saveMood() {
        if (selectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (moodType == null || moodType.isEmpty()) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
            return;
        }

        mood.setDescription(descBox.getText().toString().trim());

        Log.d(TAG, "Saving mood: " + mood.toString());

        Call<Mood> call = moodApi.createMood(mood);
        call.enqueue(new Callback<Mood>() {
            @Override
            public void onResponse(Call<Mood> call, Response<Mood> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Mood saved successfully");
                    Toast.makeText(AddMoodActivity.this,
                            "Mood saved successfully!",
                            Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    String errorMsg = "Failed to save mood";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Log.e(TAG, errorMsg);
                    Toast.makeText(AddMoodActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mood> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                Toast.makeText(AddMoodActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}