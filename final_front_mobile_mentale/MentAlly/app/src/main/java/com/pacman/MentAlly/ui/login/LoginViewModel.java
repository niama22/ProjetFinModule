package com.pacman.MentAlly.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import android.util.Patterns;
import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.login.api.AuthApi;
import com.pacman.MentAlly.ui.login.models.LoginRequest;
import com.pacman.MentAlly.ui.login.models.LoginResponse;
import com.pacman.MentAlly.ui.login.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        Log.d(TAG, "Attempting login for user: " + username);

        AuthApi api = RetrofitClient.getInstance().getApi();
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Appel à l'API de connexion
        api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login successful - Email: " + loginResponse.getEmail() +
                            ", ID: " + loginResponse.getId());
                    loginResult.setValue(loginResponse);
                } else {
                    Log.e(TAG, "Login failed - Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    loginResult.setValue(new LoginResponse(
                            null,
                            "Connexion échouée. Veuillez vérifier vos informations.",
                            null  // ID est null en cas d'échec
                    ));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Network error during login", t);
                loginResult.setValue(new LoginResponse(
                        null,
                        "Erreur de connexion : " + t.getMessage(),
                        null  // ID est null en cas d'erreur
                ));
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(trimmedUsername).matches();
    }

    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        String trimmedPassword = password.trim();
        return trimmedPassword.length() > 5;
    }
}