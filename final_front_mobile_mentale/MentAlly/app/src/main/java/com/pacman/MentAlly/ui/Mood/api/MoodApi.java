package com.pacman.MentAlly.ui.Mood.api;

import com.pacman.MentAlly.ui.Mood.Mood;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface MoodApi {
    @POST("api/moods")
    Call<Mood> createMood(@Body Mood mood);

    @GET("api/moods/user/{userId}")
    Call<List<Mood>> getUserMoods(@Path("userId") Long userId);

    @GET("api/moods/{id}")
    Call<Mood> getMood(@Path("id") Long id);

    @PUT("api/moods/{id}")
    Call<Mood> updateMood(@Path("id") Long id, @Body Mood mood);

    @DELETE("api/moods/{id}")
    Call<Void> deleteMood(@Path("id") Long id);

    @DELETE("api/moods/user/{userId}")
    Call<Void> deleteUserMoods(@Path("userId") Long userId);
}