package com.pacman.MentAlly.ui.emergency.api;

import com.pacman.MentAlly.ui.emergency.model.EmergencyContact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/emergency-contacts/{userId}")
    Call<List<EmergencyContact>> getEmergencyContacts(@Path("userId") Long userId);

    @POST("api/emergency-contacts")
    Call<EmergencyContact> createEmergencyContact(@Body EmergencyContact contact);

    @PUT("api/emergency-contacts/{id}")
    Call<EmergencyContact> updateEmergencyContact(
            @Path("id") Long id,
            @Body EmergencyContact contact
    );

    @DELETE("api/emergency-contacts/{userId}/{id}")
    Call<Void> deleteEmergencyContact(
            @Path("userId") Long userId,
            @Path("id") Long id
    );

    @DELETE("api/emergency-contacts/{userId}")
    Call<Void> deleteAllEmergencyContacts(@Path("userId") Long userId);
}