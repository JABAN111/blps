package org.example.blps_lab1.core.domain.course;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode
public class CourseProgressId implements Serializable {
    private Long userId;
    private UUID courseId;

    public CourseProgressId() {}

    public CourseProgressId(UUID courseId, Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }
}
