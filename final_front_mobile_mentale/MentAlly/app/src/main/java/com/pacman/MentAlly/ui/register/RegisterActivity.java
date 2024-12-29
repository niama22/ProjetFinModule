package com.pacman.MentAlly.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.home.HomeActivity;
import com.pacman.MentAlly.ui.login.models.LoginResponse;
import com.pacman.MentAlly.ui.register.api.ApiService;
import com.pacman.MentAlly.ui.register.bean.User;
import com.pacman.MentAlly.ui.register.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText dobEditText;
    private Spinner genderSpinner;
    private EditText countryEditText;
    private Button registerButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Retrofit via RetrofitClient
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Initialiser les vues
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        dobEditText = findViewById(R.id.DOB);
        genderSpinner = findViewById(R.id.gender);
        countryEditText = findViewById(R.id.country);
        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(this);

        // Configurer le Spinner avec les options de genre depuis strings.xml
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Validation du formulaire
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        firstNameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.addTextChangedListener(afterTextChangedListener);
        dobEditText.addTextChangedListener(afterTextChangedListener);
        countryEditText.addTextChangedListener(afterTextChangedListener);
    }

    private void validateForm() {
        boolean isValid = !emailEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty() &&
                !firstNameEditText.getText().toString().isEmpty() &&
                !lastNameEditText.getText().toString().isEmpty() &&
                !dobEditText.getText().toString().isEmpty() &&
                !countryEditText.getText().toString().isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches() &&
                passwordEditText.getText().toString().length() >= 6;

        registerButton.setEnabled(isValid);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            registerUser();
        }
    }

    private void registerUser() {
        User user = new User();
        user.setEmail(emailEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());
        user.setFirstName(firstNameEditText.getText().toString());
        user.setLastName(lastNameEditText.getText().toString());
        user.setDob(dobEditText.getText().toString());  // Ajout de la date de naissance
        user.setGender(genderSpinner.getSelectedItem().toString());  // Genre sélectionné
        user.setCountry(countryEditText.getText().toString());  // Pays

        Call<LoginResponse> call = apiService.registerUser(user);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this,
                            response.body().getMessage(),
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
