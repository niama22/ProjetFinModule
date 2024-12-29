package com.pacman.MentAlly.ui.profile.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8083/"; // URL for Android emulator
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create a logging interceptor to debug requests
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttp client with interceptor and timeout settings
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(10, TimeUnit.SECONDS) // Connection timeout
                    .writeTimeout(10, TimeUnit.SECONDS)   // Write timeout
                    .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                    .build();

            // Build the Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
