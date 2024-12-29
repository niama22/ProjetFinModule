package com.pacman.MentAlly.ui.register.api;

import com.pacman.MentAlly.ui.login.models.LoginResponse;
import com.pacman.MentAlly.ui.register.bean.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/register")
    Call<LoginResponse> registerUser(@Body User user);

}