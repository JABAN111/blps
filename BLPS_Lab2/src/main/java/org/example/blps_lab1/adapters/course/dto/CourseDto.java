package org.example.blps_lab1.adapters.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.blps_lab1.core.domain.course.Topic;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private UUID courseUUID;
    private String courseName;
    private BigDecimal coursePrice;
    private String description;
    private Topic topicName;
    private LocalDateTime creationDate;
    private Integer courseDuration;
    private Boolean withJobOffer;
    private Boolean isCompleted;
}
