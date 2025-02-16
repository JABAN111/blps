package org.example.blps_lab1.courseSignUp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.courseSignUp.models.Topic;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String courseName;
    private Float coursePrice;
    private Topic topicName;
    private Integer courseDuration;
    private Boolean withJobOffer;
}
