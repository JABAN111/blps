package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.domain.course.nw.NewModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewCourseRepository extends JpaRepository<NewCourse, UUID> {
    void removeByNewModuleList(List<NewModule> newModuleList);
}
