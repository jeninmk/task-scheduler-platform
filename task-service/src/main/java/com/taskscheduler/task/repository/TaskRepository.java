package com.taskscheduler.task.repository;

import com.taskscheduler.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' AND t.scheduledTime <= :now")
    List<Task> findPendingTasks(@Param("now") LocalDateTime now);
    
    List<Task> findByStatus(String status);
}
