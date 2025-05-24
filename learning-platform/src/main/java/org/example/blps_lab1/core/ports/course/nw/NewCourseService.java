package org.example.blps_lab1.core.ports.course.nw;

import org.example.blps_lab1.adapters.course.dto.nw.NewCourseDto;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface NewCourseService {
    NewCourse createCourse(final NewCourseDto course);

    NewCourse find(final UUID uuid);

    List<NewCourse> addAll(List<NewCourse> courses);

    NewCourse getCourseByUUID(final UUID uuid);

    void deleteCourse(final UUID courseUUID);

    List<NewCourse> getAllCourses();

    NewCourse updateCourse(UUID courseUUID, NewCourseDto courseDto);

    List<NewCourse> enrollStudent(Long studentID, UUID courseUUID);

    NewCourse addAdditionalCourses(UUID courseUUID, UUID additionalCourseUUID);

    NewCourse linkModule(UUID courseUUID, UUID moduleUUID);

    Boolean isCourseFinished(UUID courseUUID);
}
