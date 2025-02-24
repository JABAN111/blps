package org.example.blps_lab1.courseSignUp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.authorization.models.User;

@Entity
@Data
@Table(name = "course_progress")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CourseProgressId.class)
public class CourseProgress {

    @Id
    private Long courseId;

    @Id
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer earnedPoints;
}
