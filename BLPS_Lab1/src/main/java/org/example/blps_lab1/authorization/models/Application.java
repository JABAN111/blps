package org.example.blps_lab1.authorization.models;

import java.sql.Timestamp;

import org.example.blps_lab1.courseSignUp.models.Course;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Заявка, которую оставляет пользователь при регистрации
 * 
 * После клиент возвращает ответ, если положителен
 *  присваивается статус Ok
 *  иначе статус Reject
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    private Timestamp updatedAt;
    private Timestamp createdAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
