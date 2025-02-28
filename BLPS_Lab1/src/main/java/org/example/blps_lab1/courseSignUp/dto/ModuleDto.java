package org.example.blps_lab1.courseSignUp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.courseSignUp.models.Course;

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

}
