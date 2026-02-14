package com.taskscheduler.task.controller;

import com.taskscheduler.common.dto.ApiResponse;
import com.taskscheduler.common.dto.TaskDTO;
import com.taskscheduler.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tasks retrieved successfully", tasks));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Task retrieved successfully", task));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByUserId(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tasks retrieved successfully", tasks));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Task created successfully", createdTask));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Task updated successfully", updatedTask));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Task deleted successfully", null));
    }
}
