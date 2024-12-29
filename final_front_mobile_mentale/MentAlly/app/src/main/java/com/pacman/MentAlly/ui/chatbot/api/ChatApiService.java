package com.pacman.MentAlly.ui.chatbot.api;

import com.pacman.MentAlly.ui.chatbot.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatApiService {
    @GET("api/messages/date/{date}")
    Call<List<Message>> getMessagesByDate(@Path("date") String date);
}