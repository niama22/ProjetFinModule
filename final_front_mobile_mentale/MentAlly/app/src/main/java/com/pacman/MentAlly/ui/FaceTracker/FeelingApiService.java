package com.pacman.MentAlly.ui.FaceTracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeelingApiService {
    @POST("api/feelings")
    Call<Feeling> createFeeling(@Body Feeling feeling);

    @GET("/api/feelings/user/{userId}")
    Call<List<Feeling>> getFeelingsByUserId(@Path("userId") Long userId);
}
