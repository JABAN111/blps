package org.example.blps_lab1.courseSignUp.models;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
