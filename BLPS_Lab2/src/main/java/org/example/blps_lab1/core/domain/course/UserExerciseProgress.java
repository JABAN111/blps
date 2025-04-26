package org.example.blps_lab1.core.domain.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.auth.User;

@Entity
@Table(name = "user_exercise_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExerciseProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(nullable = false)
    private Integer points = 0;

    public UserExerciseProgress(User user, Exercise exercise) {
        this.user = user;
        this.exercise = exercise;
        this.isCompleted = false;
        this.points = 0;
    }

}

