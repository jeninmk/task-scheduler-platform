package com.taskscheduler.task.service;

import com.taskscheduler.task.config.RabbitMQConfig;
import com.taskscheduler.task.entity.Task;
import com.taskscheduler.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskSchedulerService {
    
    private final TaskRepository taskRepository;
    private final RabbitTemplate rabbitTemplate;
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processScheduledTasks() {
        List<Task> pendingTasks = taskRepository.findPendingTasks(LocalDateTime.now());
        
        for (Task task : pendingTasks) {
            task.setStatus("IN_PROGRESS");
            task.setExecutionTime(LocalDateTime.now());
            taskRepository.save(task);
            
            // Send to queue for processing
            Map<String, Object> message = new HashMap<>();
            message.put("taskId", task.getId());
            message.put("title", task.getTitle());
            message.put("userId", task.getUserId());
            message.put("scheduledTime", task.getScheduledTime().toString());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_EXCHANGE,
                RabbitMQConfig.TASK_ROUTING_KEY,
                message
            );
            
            System.out.println("Sent task to queue: " + task.getId());
        }
    }
}
