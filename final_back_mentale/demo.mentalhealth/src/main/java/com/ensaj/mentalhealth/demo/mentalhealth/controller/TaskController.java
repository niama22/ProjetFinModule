package com.ensaj.mentalhealth.demo.mentalhealth.controller;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.TaskEntity;
import com.ensaj.mentalhealth.demo.mentalhealth.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/completed/{userId}")
    public ResponseEntity<List<TaskEntity>> getCompletedTasks(@PathVariable Long userId) {
        List<TaskEntity> tasks = taskRepository.findByUserIdAndCompleted(userId, true);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/incomplete/{userId}")
    public ResponseEntity<List<TaskEntity>> getIncompleteTasks(@PathVariable Long userId) {
        List<TaskEntity> tasks = taskRepository.findByUserIdAndCompleted(userId, false);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<TaskEntity> createTask(@PathVariable Long userId, @RequestBody TaskEntity task) {
        task.setUserId(userId);
        TaskEntity savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/{userId}/{taskId}/complete")
    public ResponseEntity<?> markAsCompleted(@PathVariable Long userId, @PathVariable Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Task not found or unauthorized"));

        task.setCompleted(true);
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        taskRepository.deleteByUserIdAndId(userId, taskId);
        return ResponseEntity.ok().build();
    }
}