package org.example.blps_lab1.courseSignUp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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
    private Float coursePrice;

    @Column
    @Enumerated(EnumType.STRING)
    private Topic topicName;

    @Column(nullable = false)
    private Integer courseDuration;

    @Column
    private Boolean withJobOffer;
}
