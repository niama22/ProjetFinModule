package com.pacman.MentAlly.ui.chatbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.chatbot.api.ChatApiService;
import com.pacman.MentAlly.ui.chatbot.api.RetrofitClient;
import com.pacman.MentAlly.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private RasaChatService rasaChatService;
    private String selectedDate = null;
    private ChatApiService chatApiService;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        // Vérifier si l'utilisateur est connecté
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);

        if (userId == -1L) {
            Log.w(TAG, "No valid userId found, redirecting to login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String conversationDate = getIntent().getStringExtra("CONVERSATION_DATE");
        selectedDate = conversationDate;

        initComponents();
        setupRecyclerView();
        setupSendButton();

        if (conversationDate != null) {
            fetchMessagesForDate(conversationDate);
        }
    }

    private void initComponents() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        messageList = new ArrayList<>();
        rasaChatService = new RasaChatService(this);
        chatApiService = RetrofitClient.getInstance().getChatApiService();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupSendButton() {
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Effacer le champ de texte immédiatement
                editTextMessage.setText("");

                // Stocker d'abord le message de l'utilisateur
                rasaChatService.storeMessage(messageText, true, new RasaChatService.MessageStoreCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "User message stored successfully");
                        // Ajouter le message à l'interface uniquement après le stockage réussi
                        runOnUiThread(() -> {
                            addMessage(new Message(messageText, true, false));
                        });

                        // Envoyer le message à Rasa après le stockage réussi
                        rasaChatService.sendMessage(messageText, new RasaChatService.RasaResponseCallback() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Raw bot response: " + response);
                                String botResponse = rasaChatService.extractBotResponse(response);

                                // Stocker la réponse du bot
                                rasaChatService.storeMessage(botResponse, false, new RasaChatService.MessageStoreCallback() {
                                    @Override
                                    public void onSuccess(String storeResponse) {
                                        Log.d(TAG, "Bot message stored successfully: " + storeResponse);
                                        runOnUiThread(() -> {
                                            addMessage(new Message(botResponse, false, false));
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "Error storing bot message: " + error);
                                        runOnUiThread(() -> {
                                            Toast.makeText(MainActivity2.this,
                                                    "Erreur de stockage de la réponse: " + error,
                                                    Toast.LENGTH_SHORT).show();
                                            // Afficher quand même le message même si le stockage a échoué
                                            addMessage(new Message(botResponse, false, false));
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error getting bot response: " + error);
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity2.this,
                                            "Erreur de communication avec le bot: " + error,
                                            Toast.LENGTH_SHORT).show();
                                    addMessage(new Message("Erreur: " + error, false, false));
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error storing user message: " + error);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity2.this,
                                    "Erreur de stockage du message: " + error,
                                    Toast.LENGTH_SHORT).show();
                            // Continuer quand même avec l'envoi au bot
                            addMessage(new Message(messageText, true, false));
                            rasaChatService.sendMessage(messageText, new RasaChatService.RasaResponseCallback() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "Raw bot response after storage error: " + response);
                                    String botResponse = rasaChatService.extractBotResponse(response);

                                    // Stocker la réponse du bot même si le message utilisateur n'a pas été stocké
                                    rasaChatService.storeMessage(botResponse, false, new RasaChatService.MessageStoreCallback() {
                                        @Override
                                        public void onSuccess(String storeResponse) {
                                            Log.d(TAG, "Bot message stored successfully after user message storage error: " + storeResponse);
                                            runOnUiThread(() -> {
                                                addMessage(new Message(botResponse, false, false));
                                            });
                                        }

                                        @Override
                                        public void onError(String storeError) {
                                            Log.e(TAG, "Error storing bot message after user message storage error: " + storeError);
                                            runOnUiThread(() -> {
                                                Toast.makeText(MainActivity2.this,
                                                        "Erreur de stockage de la réponse: " + storeError,
                                                        Toast.LENGTH_SHORT).show();
                                                addMessage(new Message(botResponse, false, false));
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "Error getting bot response after storage error: " + error);
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity2.this,
                                                "Erreur de communication avec le bot: " + error,
                                                Toast.LENGTH_SHORT).show();
                                        addMessage(new Message("Erreur: " + error, false, false));
                                    });
                                }
                            });
                        });
                    }
                });
            }
        });
    }

    private void fetchMessagesForDate(String date) {
        Call<List<Message>> call = chatApiService.getMessagesByDate(date);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.notifyDataSetChanged();
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Log.e(TAG, "Error fetching messages: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity2.this,
                                "Erreur lors de la récupération des messages: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch messages", t);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity2.this,
                            "Échec de la récupération des messages: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void addMessage(Message message) {
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier si l'utilisateur est toujours connecté
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        Long currentUserId = prefs.getLong("userId", -1L);
        if (currentUserId == -1L || !currentUserId.equals(userId)) {
            Log.w(TAG, "User session changed or invalid, redirecting to login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}