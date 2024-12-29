package com.pacman.MentAlly.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.chatbot.MainActivity2;
import com.pacman.MentAlly.ui.chatbot.Message;
import com.pacman.MentAlly.ui.home.api.ApiService;
import com.pacman.MentAlly.ui.home.api.MessageResponse;
import com.pacman.MentAlly.ui.home.api.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchiveActivity extends AppCompatActivity {
    private static final String TAG = "ArchiveActivity";
    private RecyclerView recyclerViewMessages;
    private ArchiveAdapter archiveAdapter;
    private List<ConversationGroup> conversationGroups;
    private ApiService apiService;
    private Long currentUserId;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        initializeViews();
        initializeApiService();
        initializeUserId();
        setupRecyclerView();
        setupClickListeners();

        // Fetch conversations only if everything is properly initialized
        if (isInitializedProperly()) {
            fetchConversations();
        }
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        Button btnStatistics = findViewById(R.id.btnStatistics);

        if (emptyStateTextView == null) {
            Log.e(TAG, "emptyStateTextView not found in layout");
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(ArchiveActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }

    private void initializeApiService() {
        apiService = RetrofitClient.getInstance().getApiService();
        if (apiService == null) {
            Log.e(TAG, "Failed to initialize ApiService");
            Toast.makeText(this, "Service initialization error", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeUserId() {
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1L);

        if (currentUserId == -1L) {
            Log.e(TAG, "No user ID found");
            Toast.makeText(this, "Error: User not connected", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        conversationGroups = new ArrayList<>();
        archiveAdapter = new ArchiveAdapter(conversationGroups, this::onConversationClicked);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(archiveAdapter);
    }

    private void setupClickListeners() {
        Button btnStatistics = findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(ArchiveActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }

    private boolean isInitializedProperly() {
        return apiService != null && currentUserId != -1L && emptyStateTextView != null;
    }

    private void updateEmptyState() {
        if (emptyStateTextView == null) {
            Log.e(TAG, "emptyStateTextView is null in updateEmptyState");
            return;
        }

        if (conversationGroups.isEmpty()) {
            recyclerViewMessages.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMessages.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    private void fetchConversations() {
        Log.d(TAG, "Fetching conversations...");

        apiService.getAllMessages().enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        List<MessageResponse> messages = response.body();
                        Log.d(TAG, "Received " + messages.size() + " messages");

                        processMessages(messages);
                        updateEmptyState();
                    } else {
                        Log.e(TAG, "Error response: " + response.code());
                        Toast.makeText(ArchiveActivity.this,
                                "Error retrieving messages",
                                Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error fetching conversations", t);
                    Toast.makeText(ArchiveActivity.this,
                            "Server connection error",
                            Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
            }
        });
    }

    private void processMessages(List<MessageResponse> messages) {
        Map<String, List<Message>> messagesByDate = new HashMap<>();
        Map<String, Integer> sentimentStats = new HashMap<>();

        for (MessageResponse messageResponse : messages) {
            if (messageResponse.getTimestamp() != null) {
                String date = messageResponse.getTimestamp().split("T")[0];
                Message message = new Message(
                        messageResponse.getContent(),
                        messageResponse.isUser(),
                        false
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    messagesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(message);
                } else {
                    if (!messagesByDate.containsKey(date)) {
                        messagesByDate.put(date, new ArrayList<>());
                    }
                    messagesByDate.get(date).add(message);
                }

                if (messageResponse.isUser() && messageResponse.getContent() != null) {
                    analyzeSentiments(messageResponse.getContent(), sentimentStats);
                }
            }
        }

        updateConversationGroups(messagesByDate);

        if (!sentimentStats.isEmpty() && currentUserId != -1L) {
            saveStatistics(currentUserId, sentimentStats);
        }
    }

    private void updateConversationGroups(Map<String, List<Message>> messagesByDate) {
        conversationGroups.clear();
        for (Map.Entry<String, List<Message>> entry : messagesByDate.entrySet()) {
            conversationGroups.add(new ConversationGroup(entry.getKey(), entry.getValue()));
        }

        Collections.sort(conversationGroups, (g1, g2) -> g2.getDate().compareTo(g1.getDate()));
        archiveAdapter.notifyDataSetChanged();
    }

    private void onConversationClicked(ConversationGroup conversation) {
        Map<String, Integer> sentimentStats = new HashMap<>();

        for (Message message : conversation.getMessages()) {
            if (message.isUser()) {
                analyzeSentiments(message.getText(), sentimentStats);
            }
        }

        if (!sentimentStats.isEmpty() && currentUserId != -1L) {
            saveStatistics(currentUserId, sentimentStats);
        }

        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("CONVERSATION_DATE", conversation.getDate());
        startActivity(intent);
    }

    private void analyzeSentiments(String text, Map<String, Integer> sentimentStats) {
        if (text == null) {
            Log.w(TAG, "Attempted to analyze null text");
            return;
        }

        String[] sentiments = {
                "sad", "happy", "depressed", "good", "bad",
                "angry", "excited", "bored", "anxious",
                "worried", "stressed", "peaceful", "calm"
        };

        String textLower = text.toLowerCase();
        for (String sentiment : sentiments) {
            if (textLower.contains(sentiment)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sentimentStats.merge(sentiment, 1, Integer::sum);
                } else {
                    Integer count = sentimentStats.get(sentiment);
                    sentimentStats.put(sentiment, count == null ? 1 : count + 1);
                }
            }
        }
    }

    private void saveStatistics(Long userId, Map<String, Integer> sentimentStats) {
        apiService.saveStatistics(userId, sentimentStats).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Statistics saved successfully");
                } else {
                    Log.e(TAG, "Error saving statistics: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error saving statistics", t);
            }
        });
    }
}