package com.ensaj.mentalhealth.demo.mentalhealth.repository;

import com.ensaj.mentalhealth.demo.mentalhealth.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserIdAndCompleted(Long userId, boolean completed);
    void deleteByUserIdAndId(Long userId, Long taskId);
}
