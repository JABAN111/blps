package org.example.blps_lab1.core.domain.course.nw;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.course.Topic;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column
    private LocalDateTime creationTime;

    @ManyToMany
    @JoinTable(
            name = "new_course_new_module",
            joinColumns = @JoinColumn(name = "new_course_uuid"),
            inverseJoinColumns = @JoinColumn(name = "new_module_uuid")
    )
    private List<NewModule> newModuleList = new ArrayList<>();

    //TODO очень и пиздец как очень внимательно тут нужно быть
    @ManyToMany
    @JoinTable(
            name = "additional_courses",
            joinColumns = @JoinColumn(name = "new_course_uuid"),
            inverseJoinColumns = @JoinColumn(name = "additional_courses_uuid")
    )
    private List<NewCourse> additionalCourseList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        creationTime = LocalDateTime.now();
    }

    @PreRemove
    public void preRemove(){
        newModuleList.clear();
    }
}
