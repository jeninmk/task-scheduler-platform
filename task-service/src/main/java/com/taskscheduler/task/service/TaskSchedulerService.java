package com.taskscheduler.task.service;

import com.taskscheduler.common.dto.TaskDTO;
import com.taskscheduler.task.entity.Task;
import com.taskscheduler.task.repository.TaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSchedulerService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processScheduledTasks() {
        // Find pending tasks where scheduled time has passed
        List<Task> tasks = taskRepository.findPendingTasksBeforeTime(LocalDateTime.now());
        
        for (Task task : tasks) {
            processTask(task);
        }
    }
    
    private void processTask(Task task) {
        // Update task status
        task.setStatus("PROCESSING");
        task.setExecutionTime(LocalDateTime.now());
        taskRepository.save(task);
        
        // Send notification via RabbitMQ
        TaskDTO taskDTO = convertToDTO(task);
        rabbitTemplate.convertAndSend("task.exchange", "task.routing.key", taskDTO);
        
        // Simulate task processing
        // In a real application, this would be actual business logic
        try {
            Thread.sleep(1000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Update task status to completed
        task.setStatus("COMPLETED");
        taskRepository.save(task);
    }
    
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setScheduledTime(task.getScheduledTime());
        dto.setExecutionTime(task.getExecutionTime());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setUserId(task.getUserId());
        return dto;
    }
}
