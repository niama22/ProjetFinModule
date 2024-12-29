package com.pacman.MentAlly.ui.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pacman.MentAlly.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_DATE_HEADER = 3; // Nouveau type pour l'en-tête de date

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isDateHeader()) {
            return VIEW_TYPE_DATE_HEADER; // Retourne le type d'en-tête de date
        } else {
            return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_BOT) {
            View view = inflater.inflate(R.layout.item_message_bot, parent, false);
            return new BotMessageViewHolder(view);
        } else { // VIEW_TYPE_DATE_HEADER
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        } else if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.userMessageTextView);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getText());
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.botMessageTextView);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getText());
        }
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeaderTextView;

        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeaderTextView = itemView.findViewById(R.id.dateHeaderTextView);
        }

        public void bind(Message message) {
            dateHeaderTextView.setText(message.getText());
        }
    }
}