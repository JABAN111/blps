package org.example.blps_lab1.core.domain.auth;

import java.sql.Timestamp;

import jakarta.persistence.*;
import org.example.blps_lab1.core.domain.course.Course;

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

    private String userEmail;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Course course;

    private Timestamp updatedAt;
    private Timestamp createdAt;

    @PrePersist
    public void createTimeStamps() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void updateTimestamps(){
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
