package com.taskscheduler.task.service;

import com.taskscheduler.common.dto.TaskDTO;
import com.taskscheduler.task.entity.Task;
import com.taskscheduler.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = new Task();
        mapDtoToEntity(taskDTO, task);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        Task savedTask = taskRepository.save(task);
        return mapEntityToDto(savedTask);
    }
    
    public TaskDTO getTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapEntityToDto(task);
    }
    
    public List<TaskDTO> getUserTasks(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        
        if (!task.getUserId().equals(taskDTO.getUserId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        mapDtoToEntity(taskDTO, task);
        task.setUpdatedAt(LocalDateTime.now());
        
        Task updatedTask = taskRepository.save(task);
        return mapEntityToDto(updatedTask);
    }
    
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        taskRepository.delete(task);
    }
    
    private void mapDtoToEntity(TaskDTO dto, Task entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setPriority(dto.getPriority());
        entity.setScheduledTime(dto.getScheduledTime());
        entity.setUserId(dto.getUserId());
    }
    
    private TaskDTO mapEntityToDto(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setExecutionTime(entity.getExecutionTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setUserId(entity.getUserId());
        return dto;
    }
}
