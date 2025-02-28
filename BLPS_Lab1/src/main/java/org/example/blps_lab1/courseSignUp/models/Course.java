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

    @Column(nullable = false)
    private Boolean isFinished;

    @ManyToMany(mappedBy = "courseList")
    private List<User> userList;

    @ManyToMany
    @JoinTable(
            name = "additional_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_courses_id")
    )
    private List<Course> additionalCourseList;

    @PrePersist()
    public void prePersist(){
        isFinished = false;
    }

}
