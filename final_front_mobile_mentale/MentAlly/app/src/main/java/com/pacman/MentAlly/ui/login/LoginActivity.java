package com.pacman.MentAlly.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.home.HomeActivity;
import com.pacman.MentAlly.ui.home.MainActivity;
import com.pacman.MentAlly.ui.login.models.LoginRequest;
import com.pacman.MentAlly.ui.login.models.LoginResponse;
import com.pacman.MentAlly.ui.login.network.RetrofitClient;
import com.pacman.MentAlly.ui.profile.ProfileActivity;
import com.pacman.MentAlly.ui.register.RegisterActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser les vues
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        register = findViewById(R.id.register);

        loginButton.setOnClickListener(v -> attemptLogin());
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // Validation basique
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Montrer le loading
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Créer la requête
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Faire l'appel API
        RetrofitClient.getInstance()
                .getApi()
                .login(loginRequest)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();

                            Log.d(TAG, "Login response received: " +
                                    "Message=" + loginResponse.getMessage() +
                                    ", Email=" + loginResponse.getEmail() +
                                    ", ID=" + loginResponse.getId());

                            if (loginResponse.getMessage().equals("Login successful")) {
                                // Vérifier si l'ID est présent
                                if (loginResponse.getId() == null) {
                                    Log.w(TAG, "No user ID received from server");
                                }

                                // Sauvegarder les informations de l'utilisateur
                                saveUserSession(loginResponse.getEmail(), loginResponse.getId());
                                ProfileActivity.saveUserEmail(LoginActivity.this, loginResponse.getEmail());

                                Log.d(TAG, "User session saved, starting MainActivity");

                                // Démarrer MainActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = loginResponse.getMessage();
                                Log.w(TAG, "Login failed: " + errorMessage);
                                Toast.makeText(LoginActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            handleLoginError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "Network error during login", t);
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                        Toast.makeText(LoginActivity.this,
                                "Erreur de connexion : " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleLoginError(Response<LoginResponse> response) {
        String errorMessage = "Erreur de connexion";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
                Log.e(TAG, "Error response: " + errorMessage);
            } catch (IOException e) {
                Log.e(TAG, "Error reading error body", e);
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void saveUserSession(String email, Long userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("userEmail", email);

        // Si l'ID est null, utiliser une valeur par défaut
        if (userId != null) {
            editor.putLong("userId", userId);
            Log.d(TAG, "Saving user ID: " + userId);
        } else {
            Log.w(TAG, "User ID is null, using default value");
            editor.putLong("userId", -1L); // Utiliser -1 comme valeur par défaut
        }

        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Log.d(TAG, "User session saved - Email: " + email + ", ID: " + (userId != null ? userId : "default"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Réinitialiser l'état des vues
        loginButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.GONE);
    }
}