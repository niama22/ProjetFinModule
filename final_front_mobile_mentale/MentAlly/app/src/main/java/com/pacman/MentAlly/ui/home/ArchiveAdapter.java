package com.pacman.MentAlly.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pacman.MentAlly.R;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ConversationViewHolder> {
    private List<ConversationGroup> conversations;
    private OnConversationClickListener clickListener;

    public interface OnConversationClickListener {
        void onConversationClick(ConversationGroup conversation);
    }

    public ArchiveAdapter(List<ConversationGroup> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ConversationGroup conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;
        private TextView previewTextView;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            previewTextView = itemView.findViewById(R.id.textViewPreview);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onConversationClick(conversations.get(position));
                }
            });
        }

        public void bind(ConversationGroup conversation) {
            dateTextView.setText(conversation.getDate());
            previewTextView.setText(conversation.getPreviewText());
        }
    }
}