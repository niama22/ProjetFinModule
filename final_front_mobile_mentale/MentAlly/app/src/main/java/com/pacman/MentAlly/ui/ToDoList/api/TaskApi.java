package com.pacman.MentAlly.ui.ToDoList.api;

import com.pacman.MentAlly.ui.ToDoList.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskApi {
    // Get all incomplete tasks for user
    @GET("api/tasks/incomplete/{userId}")
    Call<List<Task>> getIncompleteTasks(@Path("userId") Long userId);

    // Get all completed tasks for user
    @GET("api/tasks/completed/{userId}")
    Call<List<Task>> getCompletedTasks(@Path("userId") Long userId);

    // Create new task
    @POST("api/tasks/{userId}")
    Call<Task> createTask(
            @Path("userId") Long userId,
            @Body Task task
    );

    // Mark task as completed
    @PUT("api/tasks/{userId}/{taskId}/complete")
    Call<Task> markTaskAsCompleted(
            @Path("userId") Long userId,
            @Path("taskId") Long taskId
    );

    // Update task details
    @PUT("api/tasks/{userId}/{taskId}")
    Call<Task> updateTask(
            @Path("userId") Long userId,
            @Path("taskId") Long taskId,
            @Body Task task
    );

    // Delete a specific task
    @DELETE("api/tasks/{userId}/{taskId}")
    Call<Void> deleteTask(
            @Path("userId") Long userId,
            @Path("taskId") Long taskId
    );

    // Delete all tasks (completed or incomplete)
    @DELETE("api/tasks/{userId}/all/{completed}")
    Call<Void> deleteAllTasks(
            @Path("userId") Long userId,
            @Path("completed") boolean completed
    );

    // Get task by ID
    @GET("api/tasks/{userId}/{taskId}")
    Call<Task> getTaskById(
            @Path("userId") Long userId,
            @Path("taskId") Long taskId
    );
}