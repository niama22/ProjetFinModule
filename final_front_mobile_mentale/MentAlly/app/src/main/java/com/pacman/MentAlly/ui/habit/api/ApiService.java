package com.pacman.MentAlly.ui.habit.api;
import com.pacman.MentAlly.ui.habit.Habit;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @GET("habits/user/{userId}")
    Call<List<Habit>> getUserHabits(@Path("userId") Long userId);

    @GET("habits/user/{userId}/habit/{habitId}")
    Call<Habit> getHabit(@Path("userId") String userId, @Path("habitId") Long habitId);

    @POST("habits")
    Call<Habit> createHabit(@Body Habit habit);

    @PUT("habits/user/{userId}/habit/{habitId}")
    Call<Habit> updateHabit(
            @Path("userId") Long userId,
            @Path("habitId") Long habitId,
            @Body Habit habit
    );
    @PUT("habits/user/{userId}/habit/{habitId}/progress")
    Call<Habit> updateProgress(
            @Path("userId") Long userId,
            @Path("habitId") Long habitId,
            @Query("progress") int progress
    );


    @DELETE("habits/user/{userId}/habit/{habitId}")
    Call<Void> deleteHabit(
            @Path("userId") Long userId,
            @Path("habitId") Long habitId
    );
}