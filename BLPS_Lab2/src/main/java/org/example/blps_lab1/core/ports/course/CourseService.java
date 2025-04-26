package org.example.blps_lab1.core.ports.course;

import org.example.blps_lab1.adapters.course.dto.CourseDto;
import org.example.blps_lab1.core.domain.course.Course;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    Course createCourse(final Course course);
    Course find(final Long id);
    List<Course> addAll(List<Course> courses);
    Course getCourseByUUID(final Long id);
    void deleteCourse(final Long courseUUID);
    List<Course> getAllCourses();
    Course updateCourse(Long courseUUID, CourseDto courseDto);
    List<Course> enrollUser(Long userId, Long courseUUID);
    Course addAdditionalCourses(Long courseUUID, Long additionalCourseUUID);
    Course addListOfCourses(Long uuid, List<Course> additionalCourses);
}
