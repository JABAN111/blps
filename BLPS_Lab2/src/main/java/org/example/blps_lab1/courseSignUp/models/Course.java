package org.example.blps_lab1.courseSignUp.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.authorization.models.User;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "courseId")
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

    @Column
    private LocalDateTime creationTime;

    @Column
    private Boolean isCompleted = false;

    @ManyToMany(mappedBy = "courseList")
    private List<User> userList;

    @ManyToMany
    @JoinTable(
            name = "additional_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_courses_id")
    )
    private List<Course> additionalCourseList;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;

    @PrePersist
    public void prePersist() {
        creationTime = LocalDateTime.now();
    }
}
