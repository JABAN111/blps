package org.example.blps_lab1.courseSignUp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.blps_lab1.courseSignUp.models.Topic;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String courseName;
    private BigDecimal coursePrice;
    private String description;
    private Topic topicName;
    private LocalDateTime creationDate;
    private Integer courseDuration;
    private Boolean withJobOffer;
    private Boolean isCompleted;
}
