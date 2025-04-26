package org.example.blps_lab1.adapters.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {
    private String name;
    private Boolean isCompleted;
    private Integer orderNumber;
    private String description;
    private Boolean isBlocked;
    private Integer totalPoints;
    private LocalDateTime localDateTime;
}
