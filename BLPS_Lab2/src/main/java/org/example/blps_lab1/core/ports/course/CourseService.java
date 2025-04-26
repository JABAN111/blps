package org.example.blps_lab1.core.ports.course;

import org.example.blps_lab1.adapters.course.dto.CourseDto;
import org.example.blps_lab1.core.domain.course.Course;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    Course createCourse(final Course course);
    Course find(final UUID id);
    List<Course> addAll(List<Course> courses);
    Course getCourseByUUID(final UUID id);
    void deleteCourse(final UUID courseUUID);
    List<Course> getAllCourses();
    Course updateCourse(UUID courseUUID, CourseDto courseDto);
    List<Course> enrollUser(Long userId, UUID courseUUID);
    Course addAdditionalCourses(UUID courseUUID, UUID additionalCourseUUID);
    Course addListOfCourses(UUID uuid, List<Course> additionalCourses);
}
