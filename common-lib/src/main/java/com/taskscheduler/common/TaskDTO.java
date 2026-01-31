package com.taskscheduler.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status = "PENDING";
    private int priority = 1;
    private LocalDateTime scheduledTime;
    private LocalDateTime executionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
}
