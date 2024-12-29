package com.pacman.MentAlly.ui.login.api;


import com.pacman.MentAlly.ui.login.models.LoginRequest;
import com.pacman.MentAlly.ui.login.models.LoginResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface AuthApi {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
