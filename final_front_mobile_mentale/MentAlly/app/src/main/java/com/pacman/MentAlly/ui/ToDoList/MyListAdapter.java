package com.pacman.MentAlly.ui.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pacman.MentAlly.R;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends BaseAdapter {

        private final Context context;
        private List<Task> taskList;

        public MyListAdapter(Context context) {
            this.context = context;
            this.taskList = new ArrayList<>();
        }

        // Met à jour les tâches
        public void setData(List<Task> newTasks) {
            this.taskList = newTasks;
            notifyDataSetChanged();
        }

        // Ajoute une tâche
        public void addTask(Task task) {
            this.taskList.add(task);
            notifyDataSetChanged();
        }

        // Supprime une tâche
        public void removeTask(Task task) {
            this.taskList.remove(task);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public Task getItem(int position) {
            return taskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.task, parent, false);
            }

            Task task = taskList.get(position); // Utilisez taskList au lieu de tasks

            TextView taskName = convertView.findViewById(R.id.taskName);
            TextView taskDates = convertView.findViewById(R.id.taskDates);
            TextView taskStatus = convertView.findViewById(R.id.taskStatus);

            taskName.setText(task.getName());
            taskDates.setText(task.getStartDate() + " - " + task.getEndDate());
            taskStatus.setText(task.isCompleted() ? "Completed" : "Pending");

            return convertView;
        }
    }
