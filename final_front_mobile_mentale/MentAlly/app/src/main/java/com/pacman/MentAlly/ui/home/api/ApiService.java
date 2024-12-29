package com.pacman.MentAlly.ui.home.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoint existant
    @GET("api/messages/all")
    Call<List<MessageResponse>> getAllMessages();

    // Nouveaux endpoints pour les statistiques
    @GET("api/statistics/{userId}")
    Call<List<StatisticResponse>> getStatistics(@Path("userId") Long userId);

    @POST("api/statistics/{userId}")
    Call<Void> saveStatistics(@Path("userId") Long userId, @Body Map<String, Integer> statistics);

    @GET("api/statistics/user/{userId}/sentiment/{sentiment}")
    Call<StatisticResponse> getStatisticBySentiment(
            @Path("userId") Long userId,
            @Path("sentiment") String sentiment
    );
    @GET("api/messages/statistics/{userId}")
    Call<Map<String, Integer>> getUserStatistics(@Path("userId") String userId);


    @DELETE("api/statistics/{userId}")
    Call<Void> deleteStatistics(@Path("userId") Long userId);

    @PUT("api/statistics/{userId}/reset")
    Call<Void> resetStatistics(@Path("userId") Long userId);

    @GET("api/statistics/count/{userId}")
    Call<Map<String, Integer>> getTotalSentimentsCount(@Path("userId") Long userId);
}