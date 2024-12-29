package com.pacman.MentAlly.ui.ToDoList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.Mood.api.RetrofitClient;
import com.pacman.MentAlly.ui.ToDoList.api.TaskApi;
import com.pacman.MentAlly.ui.home.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoListActivity extends MainActivity {
    private static final String PREFS_NAME = "MentAllyPrefs";
    private static final String USER_ID_KEY = "userId";

    private Button addButton, completedButton, incompleteButton, deleteListButton;
    private ListView myListView;
    private MyListAdapter mylistadapter;
    private TaskApi taskApi;
    private Long currentUserId;
    private State currentState;

    private final List<Task> incompletedList = new ArrayList<>();
    private final List<Task> completedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.frag_container);
        getLayoutInflater().inflate(R.layout.activity_to_do_list, contentFrameLayout);

        // Get userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getLong(USER_ID_KEY, -1L);

        if (currentUserId == -1L) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        initializeApi();
        loadTasks();
    }

    private void initializeViews() {
        this.currentState = State.INCOMPLETE_TASKS;
        this.addButton = findViewById(R.id.addButton);
        this.completedButton = findViewById(R.id.viewCompletedButton);
        this.incompleteButton = findViewById(R.id.viewIncompletedButton);
        this.deleteListButton = findViewById(R.id.delete_list_btn);

        this.mylistadapter = new MyListAdapter(this);
        this.myListView = findViewById(R.id.myList);
        this.myListView.setAdapter(this.mylistadapter);

        setupClickListeners();
        updateButtonStates();
    }

    private void initializeApi() {
        taskApi = RetrofitClient.getClient().create(TaskApi.class);
    }

    private void loadTasks() {
        getAllCompletedTasksFromDB();
        getAllIncompletedTasksFromDB();
    }

    private void updateButtonStates() {
        if (this.currentState == State.INCOMPLETE_TASKS) {
            this.incompleteButton.setEnabled(false);
            this.completedButton.setEnabled(true);
            this.addButton.setEnabled(true);
        } else {
            this.completedButton.setEnabled(false);
            this.incompleteButton.setEnabled(true);
            this.addButton.setEnabled(false);
        }
    }

    private void getAllCompletedTasksFromDB() {
        if (currentUserId == -1L) return;

        taskApi.getCompletedTasks(currentUserId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    completedList.clear();
                    completedList.addAll(response.body());
                    if (currentState == State.COMPLETE_TASKS) {
                        mylistadapter.setData(completedList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error loading completed tasks: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllIncompletedTasksFromDB() {
        if (currentUserId == -1L) return;

        taskApi.getIncompleteTasks(currentUserId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    incompletedList.clear();
                    incompletedList.addAll(response.body());
                    if (currentState == State.INCOMPLETE_TASKS) {
                        mylistadapter.setData(incompletedList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error loading incomplete tasks: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTaskToDatabase(String taskName, String startDate, String finishDate) {
        if (currentUserId == -1L) return;

        Task task = new Task(taskName, startDate, finishDate);
        taskApi.createTask(currentUserId, task).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    incompletedList.add(response.body());
                    if (currentState == State.INCOMPLETE_TASKS) {
                        mylistadapter.setData(incompletedList);
                    }
                    Toast.makeText(ToDoListActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error creating task: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        addButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.add_new_task_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            EditText taskNameInput = dialog.findViewById(R.id.task_name);
            EditText startDateInput = dialog.findViewById(R.id.start_date);
            EditText endDateInput = dialog.findViewById(R.id.end_date);
            Button addTaskButton = dialog.findViewById(R.id.dialog_add_button);

            startDateInput.setOnClickListener(view -> showDatePickerDialog(startDateInput));
            endDateInput.setOnClickListener(view -> showDatePickerDialog(endDateInput));

            addTaskButton.setOnClickListener(view -> {
                String taskNameText = taskNameInput.getText().toString();
                String startDateText = startDateInput.getText().toString();
                String endDateText = endDateInput.getText().toString();

                if (taskNameText.isEmpty()) {
                    Toast.makeText(ToDoListActivity.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                    return;
                }

                addTaskToDatabase(taskNameText, startDateText, endDateText);
                dialog.dismiss();
            });

            dialog.show();
        });

        completedButton.setOnClickListener(v -> {
            currentState = State.COMPLETE_TASKS;
            updateButtonStates();
            mylistadapter.setData(completedList);
        });

        incompleteButton.setOnClickListener(v -> {
            currentState = State.INCOMPLETE_TASKS;
            updateButtonStates();
            mylistadapter.setData(incompletedList);
        });

        deleteListButton.setOnClickListener(v -> {
            if (currentState == State.INCOMPLETE_TASKS) {
                deleteAllTasks(false);
            } else {
                deleteAllTasks(true);
            }
        });
    }

    private void deleteAllTasks(boolean completed) {
        if (currentUserId == -1L) return;

        taskApi.deleteAllTasks(currentUserId, completed).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (completed) {
                        completedList.clear();
                    } else {
                        incompletedList.clear();
                    }
                    mylistadapter.setData(completed ? completedList : incompletedList);
                    Toast.makeText(ToDoListActivity.this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error deleting tasks: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> editText.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private enum State {
        INCOMPLETE_TASKS, COMPLETE_TASKS
    }
}