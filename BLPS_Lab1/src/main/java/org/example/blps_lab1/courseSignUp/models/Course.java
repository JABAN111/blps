package org.example.blps_lab1.courseSignUp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.authorization.models.User;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private BigDecimal coursePrice;

    @Column(nullable = false)
    private String courseDescription;

    @Column
    @Enumerated(EnumType.STRING)
    private Topic topicName;

    @Column(nullable = false)
    private Integer courseDuration;

    @Column
    private Boolean withJobOffer;

    @ManyToMany
    @JoinTable(name = "user_courses", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<User> userList;
}
