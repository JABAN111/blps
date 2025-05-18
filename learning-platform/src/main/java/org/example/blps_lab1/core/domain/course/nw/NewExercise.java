package org.example.blps_lab1.core.domain.course.nw;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.course.DifficultyLevel;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String answer;


    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column
    private LocalDateTime localDateTime;

    //todo
//    public int getPointsForDifficulty(){
//        return switch (difficultyLevel){
//            case HARD -> 25;
//            case MEDIUM -> 10;
//            case EASY -> 5;
//        };
//    }
}