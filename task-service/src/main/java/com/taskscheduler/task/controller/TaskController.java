package com.taskscheduler.task.controller;

import com.taskscheduler.common.dto.ApiResponse;
import com.taskscheduler.common.dto.TaskDTO;
import com.taskscheduler.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(
            @RequestBody TaskDTO taskDTO,
            @RequestHeader("X-User-Id") Long userId) {
        taskDTO.setUserId(userId);
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(ApiResponse.success("Task created", createdTask));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        TaskDTO task = taskService.getTask(id, userId);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getUserTasks(
            @RequestHeader("X-User-Id") Long userId) {
        List<TaskDTO> tasks = taskService.getUserTasks(userId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDTO taskDTO,
            @RequestHeader("X-User-Id") Long userId) {
        taskDTO.setUserId(userId);
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(ApiResponse.success("Task updated", updatedTask));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted", null));
    }
}
