package com.pacman.MentAlly.ui.habit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.habit.api.RetrofitClient;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitView> {

    private static final String TAG = "HabitAdapter";
    private final Context mContext;
    private final ArrayList<Habit> habitList;
    private final Long userId;

    public HabitAdapter(Context context, ArrayList<Habit> habits) {
        this.mContext = context;
        this.habitList = habits;
        // Récupérer l'ID utilisateur depuis SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MentAllyPrefs", Context.MODE_PRIVATE);
        this.userId = prefs.getLong("userId", -1L);
        Log.d(TAG, "Adapter créé avec userId: " + userId);

        if (userId == -1L) {
            Log.e(TAG, "Aucun ID utilisateur valide trouvé");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HabitView holder, final int position) {
        Habit habit = habitList.get(position);
        Log.d(TAG, "Binding habit: " + habit.getHabitName() + ", progress: " + habit.getProgress() + "/" + habit.getMaxProgress());

        holder.habitName.setText(habit.getHabitName());
        holder.progress.setMax(habit.getMaxProgress());
        holder.progress.setProgress(habit.getProgress(), true);

        // Ne pas activer les boutons si l'ID utilisateur n'est pas valide
        if (userId == -1L) {
            holder.upButton.setEnabled(false);
            holder.downButton.setEnabled(false);
            return;
        }

        holder.upButton.setOnClickListener(v -> {
            Log.d(TAG, "Up button clicked for habit: " + habit.getHabitName());
            incrementProgress(holder, position);
        });

        holder.downButton.setOnClickListener(v -> {
            Log.d(TAG, "Down button clicked for habit: " + habit.getHabitName());
            decrementProgress(holder, position);
        });
    }

    private void incrementProgress(HabitView holder, int position) {
        Habit habit = habitList.get(position);
        int currentProgress = habit.getProgress();
        Log.d(TAG, "Tentative d'incrément de " + currentProgress + " à " + (currentProgress + 1) + " pour userId: " + userId);

        // Désactiver temporairement les boutons
        holder.upButton.setEnabled(false);
        holder.downButton.setEnabled(false);

        RetrofitClient.getInstance()
                .getApiService()
                .updateProgress(userId, habit.getHabitId(), currentProgress + 1)
                .enqueue(new Callback<Habit>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<Habit> call, Response<Habit> response) {
                        // Réactiver les boutons
                        holder.upButton.setEnabled(true);
                        holder.downButton.setEnabled(true);

                        Log.d(TAG, "Réponse reçue. Code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Mise à jour réussie. Nouvelle progression: " + response.body().getProgress());
                            habit.setProgress(response.body().getProgress());
                            holder.progress.setProgress(habit.getProgress(), true);
                            notifyItemChanged(position);
                        } else {
                            handleError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Habit> call, Throwable t) {
                        // Réactiver les boutons
                        holder.upButton.setEnabled(true);
                        holder.downButton.setEnabled(true);

                        Log.e(TAG, "Échec de l'appel API", t);
                        Toast.makeText(mContext, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void decrementProgress(HabitView holder, int position) {
        Habit habit = habitList.get(position);
        int currentProgress = habit.getProgress();
        if (currentProgress > 0) {
            Log.d(TAG, "Tentative de décrément de " + currentProgress + " à " + (currentProgress - 1) + " pour userId: " + userId);

            // Désactiver temporairement les boutons
            holder.upButton.setEnabled(false);
            holder.downButton.setEnabled(false);

            RetrofitClient.getInstance()
                    .getApiService()
                    .updateProgress(userId, habit.getHabitId(), currentProgress - 1)
                    .enqueue(new Callback<Habit>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<Habit> call, Response<Habit> response) {
                            // Réactiver les boutons
                            holder.upButton.setEnabled(true);
                            holder.downButton.setEnabled(true);

                            Log.d(TAG, "Réponse reçue. Code: " + response.code());
                            if (response.isSuccessful() && response.body() != null) {
                                Log.d(TAG, "Mise à jour réussie. Nouvelle progression: " + response.body().getProgress());
                                habit.setProgress(response.body().getProgress());
                                holder.progress.setProgress(habit.getProgress(), true);
                                notifyItemChanged(position);
                            } else {
                                handleError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Habit> call, Throwable t) {
                            // Réactiver les boutons
                            holder.upButton.setEnabled(true);
                            holder.downButton.setEnabled(true);

                            Log.e(TAG, "Échec de l'appel API", t);
                            Toast.makeText(mContext, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void handleError(Response<Habit> response) {
        Log.e(TAG, "Erreur de mise à jour. Code: " + response.code());
        if (response.errorBody() != null) {
            try {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error body: " + errorBody);
                Toast.makeText(mContext, "Erreur: " + errorBody, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Impossible de lire l'erreur", e);
                Toast.makeText(mContext, "Erreur de mise à jour: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public HabitView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_habit, parent, false);
        return new HabitView(view);
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    static class HabitView extends RecyclerView.ViewHolder {
        final TextView habitName;
        final ProgressBar progress;
        final ImageButton upButton;
        final ImageButton downButton;

        HabitView(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitname);
            progress = itemView.findViewById(R.id.progressBar);
            upButton = itemView.findViewById(R.id.habityes);
            downButton = itemView.findViewById(R.id.habitno);
        }
    }
}