package org.example.blps_lab1.core.domain.course.nw;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class NewModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    @ManyToMany
    @JoinTable(
            name = "new_module_new_exercises",
            joinColumns = @JoinColumn(name = "new_module_uuid"),
            inverseJoinColumns = @JoinColumn(name = "new_exercise_uuid")
    )
    private List<NewExercise> exercises = new ArrayList<>();


    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }
}
