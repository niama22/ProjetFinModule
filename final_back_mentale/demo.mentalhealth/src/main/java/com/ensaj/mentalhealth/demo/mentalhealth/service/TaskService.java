package com.ensaj.mentalhealth.demo.mentalhealth.service;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.TaskEntity;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskEntity> getCompletedTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndCompleted(userId, true);
    }

    public List<TaskEntity> getIncompleteTasksByUserId(Long userId) {
        return taskRepository.findByUserIdAndCompleted(userId, false);
    }

    public TaskEntity createTask(TaskEntity task) {
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        if (task.getUserId() == null) {
            throw new IllegalArgumentException("User ID must be provided");
        }
        return taskRepository.save(task);
    }

    public TaskEntity markTaskAsCompleted(Long userId, Long taskId) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        TaskEntity task = taskOpt.get();
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to task");
        }

        task.setCompleted(true);
        return taskRepository.save(task);
    }

    public void deleteTask(Long userId, Long taskId) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return; // Task already doesn't exist
        }

        TaskEntity task = taskOpt.get();
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to task");
        }

        taskRepository.deleteById(taskId);
    }

    public void deleteAllUserTasks(Long userId, boolean completed) {
        List<TaskEntity> tasks = taskRepository.findByUserIdAndCompleted(userId, completed);
        taskRepository.deleteAll(tasks);
    }

    public TaskEntity updateTask(Long userId, Long taskId, TaskEntity updatedTask) {
        Optional<TaskEntity> existingTaskOpt = taskRepository.findById(taskId);
        if (existingTaskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        TaskEntity existingTask = existingTaskOpt.get();
        if (!existingTask.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to task");
        }

        // Update only non-null fields
        if (updatedTask.getName() != null) {
            existingTask.setName(updatedTask.getName());
        }
        if (updatedTask.getStartDate() != null) {
            existingTask.setStartDate(updatedTask.getStartDate());
        }
        if (updatedTask.getEndDate() != null) {
            existingTask.setEndDate(updatedTask.getEndDate());
        }

        return taskRepository.save(existingTask);
    }

    public TaskEntity getTaskById(Long userId, Long taskId) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        TaskEntity task = taskOpt.get();
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to task");
        }

        return task;
    }
}
