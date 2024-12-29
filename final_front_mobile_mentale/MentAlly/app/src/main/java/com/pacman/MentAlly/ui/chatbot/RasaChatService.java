package com.pacman.MentAlly.ui.chatbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RasaChatService {
    private static final String TAG = "RasaChatService";
    private static final String BASE_URL = "http://10.0.2.2:5005/webhooks/rest/webhook";
    private static final String SPRING_URL = "http://10.0.2.2:8083/api/messages";
    private final OkHttpClient client;
    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;

    public RasaChatService(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("MentAllyPrefs", Context.MODE_PRIVATE);
        this.client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
        this.gson = new Gson();
    }

    public interface RasaResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public interface MessageStoreCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    private Long getUserId() {
        long userId = prefs.getLong("userId", -1L);
        Log.d(TAG, "Getting userId from SharedPreferences: " + userId);
        return userId;
    }

    public void sendMessage(String message, RasaResponseCallback callback) {
        Long userId = getUserId();
        Log.d(TAG, "Sending message for userId: " + userId);

        if (userId == -1L) {
            String error = "Utilisateur non connecté";
            Log.e(TAG, error);
            callback.onError(error);
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("sender", "user_" + userId);
            json.put("message", message);
            Log.d(TAG, "Created JSON request: " + json.toString());
        } catch (Exception e) {
            String error = "Erreur de création JSON: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
            return;
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        Log.d(TAG, "Sending request to: " + BASE_URL);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = "Erreur d'envoi: " + e.getMessage();
                Log.e(TAG, error, e);
                callback.onError(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "Pas de réponse";
                Log.d(TAG, "Received response: " + responseBody);

                if (!response.isSuccessful()) {
                    String error = "Réponse inattendue : " + response.code() + " - " + responseBody;
                    Log.e(TAG, error);
                    callback.onError(error);
                    return;
                }

                callback.onResponse(responseBody);
            }
        });
    }

    public void storeMessage(String messageText, boolean isUser, MessageStoreCallback callback) {
        Long userId = getUserId();
        if (userId == -1L) {
            String error = "Tentative de stockage de message sans utilisateur connecté";
            Log.e(TAG, error);
            callback.onError(error);
            return;
        }

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("userId", userId);
        messageJson.addProperty("text", messageText);  // Changed from 'content' to 'text'
        messageJson.addProperty("isUser", isUser); // Changed from 'user' to 'isUser' to match entity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            messageJson.addProperty("timestamp", LocalDateTime.now().toString());
        }

        Log.d(TAG, "Message JSON to send: " + messageJson.toString());

        Log.d(TAG, "Attempting to store message: " + messageJson.toString());

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                messageJson.toString()
        );

        Request request = new Request.Builder()
                .url(SPRING_URL)
                .post(requestBody)
                .build();

        Log.d(TAG, "Sending store request to: " + SPRING_URL);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = "Erreur de stockage: " + e.getMessage();
                Log.e(TAG, error, e);
                callback.onError(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "No body";
                Log.d(TAG, "Store response: " + response.code() + " - " + responseBody);

                if (!response.isSuccessful()) {
                    String error = "Erreur de stockage: " + response.code() + " - " + responseBody;
                    Log.e(TAG, error);
                    callback.onError(error);
                    return;
                }

                callback.onSuccess(responseBody);
            }
        });
    }

    public String extractBotResponse(String jsonResponse) {
        Log.d(TAG, "Extracting bot response from: " + jsonResponse);
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            StringBuilder fullResponse = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject response = jsonArray.getJSONObject(i);
                if (response.has("text")) {
                    if (fullResponse.length() > 0) {
                        fullResponse.append("\n");
                    }
                    fullResponse.append(response.getString("text"));
                }
            }

            String finalResponse = fullResponse.length() > 0 ?
                    fullResponse.toString() :
                    "Je n'ai pas compris.";
            Log.d(TAG, "Extracted bot response: " + finalResponse);
            return finalResponse;

        } catch (Exception e) {
            Log.e(TAG, "Erreur de parsing", e);
            return "Erreur de traitement de la réponse";
        }
    }
}