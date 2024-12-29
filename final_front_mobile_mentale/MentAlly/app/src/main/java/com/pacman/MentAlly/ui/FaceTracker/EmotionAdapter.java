package com.pacman.MentAlly.ui.FaceTracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EmotionAdapter extends RecyclerView.Adapter<EmotionAdapter.EmotionViewHolder> {
    private List<Feeling> feelings = new ArrayList<>();

    @NonNull
    @Override
    public EmotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EmotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmotionViewHolder holder, int position) {
        Feeling feeling = feelings.get(position);
        holder.bind(feeling);
    }

    @Override
    public int getItemCount() {
        return feelings.size();
    }

    public void setFeelings(List<Feeling> feelings) {
        this.feelings = feelings;
        notifyDataSetChanged();
    }

    static class EmotionViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public EmotionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }

        public void bind(Feeling feeling) {
            textView.setText(feeling.getEmotion());  // Assuming you have a getEmotion() method
        }
    }
}