package org.example.blps_lab1.core.domain.course;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.auth.User;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "courseUUID")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID courseUUID;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private BigDecimal coursePrice;

    private String courseDescription;

    @Column
    @Enumerated(EnumType.STRING)
    private Topic topicName;

    // FIXME почему это блять Integer
    private Integer courseDuration;

    @Column
//    FIXME ???
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
