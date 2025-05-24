package org.example.blps_lab1.core.domain.course.nw;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Table(name = "student")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "finished_exercise",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "new_exercise_uuid")
    )
    private List<NewExercise> finishedExercises = new ArrayList<>();

    // должно было бы быть @ManyToOne на User -> пользователи существуют только в xml
    //  поэтому есть потребность указывать идюк UserXml в виде обычной записи
    private Long usid;

    @ManyToMany
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "new_course_uuid")
    )
    private List<NewCourse> courses = new ArrayList<>();
}
