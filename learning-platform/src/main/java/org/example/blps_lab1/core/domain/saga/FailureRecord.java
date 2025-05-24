package org.example.blps_lab1.core.domain.saga;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class FailureRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; //NOTE: aka email in the whole system
    private String userPassword;
    @ManyToOne
    @JoinColumn(name = "course_uuid")
    private NewCourse course;
    @Enumerated(EnumType.STRING)
    private SagaFailedStep sagaFailedStep;
    private String errorMessage;

    private Instant createdAt;
    private Instant updatedAt;


    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}