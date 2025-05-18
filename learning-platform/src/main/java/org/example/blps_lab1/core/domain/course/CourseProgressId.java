package org.example.blps_lab1.core.domain.course;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode
public class CourseProgressId implements Serializable {
    private Long userId;
    private Long courseId;

    public CourseProgressId() {}

    public CourseProgressId(Long courseId, Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }
}
