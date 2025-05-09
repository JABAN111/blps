package org.example.blps_lab1.core.domain.course.nw;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "new_exercise_id")
    private NewExercise finishedExercise;

    // должно было бы быть @ManyToOne на User -> пользователи существуют только в xml
    //  поэтому есть потребность указывать идюк UserXml в виде обычной записи
    private Long user_id;


}
