package org.example.blps_lab1.adapters.course.mapper;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.exception.common.FailedToMapException;
import org.example.blps_lab1.adapters.course.dto.CourseDto;
import org.example.blps_lab1.core.domain.course.Course;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CourseMapper {

    public static List<CourseDto> toDto(List<Course> courses) {
        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }

    public static CourseDto toDto(Course course) {
        return new CourseDto(
                course.getCourseUUID(),
                course.getCourseName(),
                course.getCoursePrice(),
                course.getCourseDescription(),
                course.getTopicName(),
                course.getCreationTime(),
                course.getCourseDuration(),
                course.getWithJobOffer(),
                course.getIsCompleted()
        );
    }

    public static Course toEntity(CourseDto dto) {
        if (dto == null) {
            log.error("fail to parse dto, cause dto == null");
            throw new FailedToMapException("Невозможно обработать данные");
        }
        Course course = new Course();
        if (dto.getCourseName() != null) {
            course.setCourseName(dto.getCourseName());
        }
        return course;
    }
}
