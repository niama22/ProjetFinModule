package com.pacman.MentAlly.ui.Mood;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pacman.MentAlly.R;

import java.util.List;
public class MoodAdapter extends ArrayAdapter<Mood> {
    private static final String TAG = "MoodAdapter";
    private final Context context;
    private final List<Mood> moods;

    public MoodAdapter(Context context, List<Mood> moods) {
        super(context, R.layout.activity_moodlog, moods);
        this.context = context;
        this.moods = moods;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_moodlog, parent, false);
        }

        TextView dateView = row.findViewById(R.id.date);
        TextView descView = row.findViewById(R.id.description);
        ImageView moodView = row.findViewById(R.id.emoji);

        Mood mood = moods.get(position);
        dateView.setText(mood.getDate());
        descView.setText(mood.getDescription());

        switch (mood.getMoodType().toLowerCase()) {
            case "happy": moodView.setImageResource(R.drawable.happy); break;
            case "sad": moodView.setImageResource(R.drawable.sad); break;
            case "cool": moodView.setImageResource(R.drawable.cool); break;
            case "scared": moodView.setImageResource(R.drawable.scared); break;
            case "lovely": moodView.setImageResource(R.drawable.lovely); break;
            case "depressed": moodView.setImageResource(R.drawable.depressed); break;
            case "flushed": moodView.setImageResource(R.drawable.flushed); break;
            case "angel": moodView.setImageResource(R.drawable.angel); break;
            case "neutral": moodView.setImageResource(R.drawable.neutral); break;
            case "sick": moodView.setImageResource(R.drawable.sick); break;
            case "nerd": moodView.setImageResource(R.drawable.nerd); break;
            case "sleepy": moodView.setImageResource(R.drawable.sleepy); break;
            case "devil": moodView.setImageResource(R.drawable.devil); break;
            case "angry": moodView.setImageResource(R.drawable.angry); break;
            default: moodView.setImageResource(R.drawable.neutral);
        }

        return row;
    }
}