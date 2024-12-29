package com.pacman.MentAlly.ui.profile.api;

import com.pacman.MentAlly.ui.register.bean.User;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {
    @GET("api/users/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);
    @GET("api/users/{userId}")
    Call<User> getUserById(@Path("userId") Long userId);
}
