package com.pacman.MentAlly.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.home.MainActivity;
import com.pacman.MentAlly.ui.login.LoginActivity;
import com.pacman.MentAlly.ui.profile.api.ApiClient;
import com.pacman.MentAlly.ui.profile.api.UserApi;
import com.pacman.MentAlly.ui.register.bean.User;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends MainActivity {
    private Button editBtn;
    private TextView firstNameTxt;
    private TextView countryTxt;
    private TextView dobTxt;
    private TextView genderTxt;
    private Button logoutBtn;
    private UserApi userApi;
    private User currentUser;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        FrameLayout contentFrameLayout = findViewById(R.id.frag_container);
        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);

        // Initialize views
        firstNameTxt = findViewById(R.id.firstname_textview);
        countryTxt = findViewById(R.id.country_textview);
        dobTxt = findViewById(R.id.dob_textview);
        genderTxt = findViewById(R.id.gender_textview);
        editBtn = findViewById(R.id.editButton);
        logoutBtn = findViewById(R.id.logout);

        // Apply background alpha
        firstNameTxt.getBackground().setAlpha(75);
        countryTxt.getBackground().setAlpha(75);
        dobTxt.getBackground().setAlpha(75);
        genderTxt.getBackground().setAlpha(75);

        // Initialize API client
        userApi = ApiClient.getClient().create(UserApi.class);

        // Get user email from shared preferences or intent
        userEmail = getUserEmailFromPreferences();

        // Load user profile
        loadUserProfile();

        // Setup edit button click listener
        editBtn.setOnClickListener(v -> showEditDialog());

        // Setup logout button click listener
        logoutBtn.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        Long userId = prefs.getLong("userId", -1L);

        if (userId == -1L) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        Call<User> call = userApi.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateUI(currentUser);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void handleApiError(Response<User> response) {
        String errorMessage = "Erreur inconnue";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleNetworkError(Throwable t) {
        String errorMessage;
        if (!isNetworkAvailable()) {
            errorMessage = "No internet connection";
        } else if (t instanceof SocketTimeoutException) {
            errorMessage = "Connection timeout. Please try again";
        } else {
            errorMessage = "Network error: " + t.getMessage();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void updateUI(User user) {
        firstNameTxt.setText(user.getFirstName());
        countryTxt.setText(user.getCountry());
        dobTxt.setText(user.getDob());
        genderTxt.setText(user.getGender());
    }

    private void showEditDialog() {
        final View editProfileDialogView = getLayoutInflater().inflate(R.layout.edit_profile_dialog, null);
        final EditText name = editProfileDialogView.findViewById(R.id.name);
        final EditText dob = editProfileDialogView.findViewById(R.id.dob);
        final EditText country = editProfileDialogView.findViewById(R.id.country);

        // Pre-fill current values
        name.setText(currentUser.getFirstName());
        dob.setText(currentUser.getDob());
        country.setText(currentUser.getCountry());

        // Setup date picker
        dob.setOnClickListener(v -> showDatePicker(dob));

        AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                .setView(editProfileDialogView)
                .setPositiveButton("Update", (dialog1, which) -> updateProfile(
                        name.getText().toString(),
                        dob.getText().toString(),
                        country.getText().toString()
                ))
                .setPositiveButtonIcon(AppCompatResources.getDrawable(ProfileActivity.this, R.drawable.complete_task))
                .create();
        dialog.show();
    }

    private void showDatePicker(final EditText dobEdit) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    month1 = month1 + 1;
                    String date = dayOfMonth + "/" + month1 + "/" + year1;
                    dobEdit.setText(date);
                },
                year, month, day);

        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dateDialog.show();
    }

    private void updateProfile(String name, String dob, String country) {
        currentUser.setFirstName(name);
        currentUser.setDob(dob);
        currentUser.setCountry(country);

        Call<User> call = userApi.updateUser(currentUser.getId(), currentUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // Reload the profile
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(ProfileActivity.this)
                .setMessage("Are you sure you want to sign out?")
                .setCancelable(true)
                .setNegativeButton("Yes", (dialog, which) -> {
                    // Clear user session/preferences
                    clearUserSession();
                    // Navigate to login
                    navigateToLogin();
                })
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private String getUserEmailFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        return prefs.getString("userEmail", null);
    }

    private void clearUserSession() {
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        // Clear activity stack to prevent back navigation
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Method to save user email during login
    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userEmail", email);
        editor.apply();
    }
}
