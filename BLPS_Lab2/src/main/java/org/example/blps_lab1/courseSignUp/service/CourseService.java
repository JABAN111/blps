package org.example.blps_lab1.courseSignUp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.example.blps_lab1.courseSignUp.dto.CourseDto;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.repository.CourseRepository;
import org.example.blps_lab1.lms.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Course createCourse(final Course course){
        Course newCourse = courseRepository.save(course);
        newCourse.setCreationTime(LocalDateTime.now());
        log.info("Created course: {}", newCourse);
        return newCourse;
    }

    public Course find(final String courseName){
        return courseRepository.findByCourseName(courseName);
    }

    public Course find(final long id){
        var optionalCourse = courseRepository.findById(id);
        if(optionalCourse.isEmpty()){
            log.warn("Course with id: {} not exist", id);
            throw new ObjectNotExistException("Курс с id: " + id + " не существует");
        }
        return optionalCourse.get();
    }

    public List<Course> saveAll(List<Course> courses){
        return courseRepository.saveAll(courses);
    }


    public Course getCourseById(final Long id){
        Optional<Course> course = courseRepository.findById(id);
        if(course.isEmpty()){
            log.error("Course with id {} does not exist", id);
            throw new ObjectNotExistException("Курс с таким id не существует");
        }
        log.info("Get course by id: {}", id);
        return course.get();
    }

    public void deleteCourse(final Long id){
        Optional<Course> deletingCourse = courseRepository.findById(id);
        if(deletingCourse.isEmpty()){
            log.error("Course with id {} does not exist", id);
            throw new RuntimeException("Курс с таким id не существует");
        }
        courseRepository.deleteById(id);
        log.info("Course deleted: {}", id);
    }

    public boolean isExist(final Long id){
        return courseRepository.findById(id).isPresent();
    }

    public List<Course> getAllCourses(){
        var list = courseRepository.findAll();
        log.info("Get courses list {}", list.size());
        return list;
    }

    public Course updateCourse(Long courseId, CourseDto courseDto){
        if(courseRepository.findById(courseId).isEmpty()){
            log.error("Course with id {} does not exist", courseId);
            throw new ObjectNotFoundException("Курс не найден");
        }
        return courseRepository.findById(courseId).map(course -> {
            course.setCourseName(courseDto.getCourseName());
            course.setCoursePrice(courseDto.getCoursePrice());
            course.setTopicName(courseDto.getTopicName());
            course.setCourseDuration(courseDto.getCourseDuration());
            course.setWithJobOffer(courseDto.getWithJobOffer());
            return courseRepository.save(course);
        }).orElseThrow(() -> {
            log.error("Course with id {} can't be updated", courseId);
            return new RuntimeException("Не получилось обновить курс");
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Course> enrollUser(Long userId, Long courseId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found in enroll"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found in enroll"));

        List<Course> enrolledCourses = new ArrayList<>();


        if(!user.getCourseList().contains(course)){
            user.getCourseList().add(course);
            enrolledCourses.add(course);
        }

        List<Course> additionalCourses = new ArrayList<>(course.getAdditionalCourseList());
        user.getCourseList().addAll(additionalCourses);
        emailService.informAboutNewCourses(user.getEmail(), course.getCourseName(), course.getCoursePrice(), additionalCourses);
        enrolledCourses.addAll(additionalCourses);
        userRepository.save(user);
        return enrolledCourses;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Course addAdditionalCourses(Long courseId, Long additionalId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ObjectNotFoundException("Курс с id " + courseId + " не найден"));

        Course additionalCourse = courseRepository.findById(additionalId)
                .orElseThrow(() -> new ObjectNotFoundException("Дополнительный курс с id " + additionalId + " не найден"));

        if(!course.getAdditionalCourseList().contains(additionalCourse)){
            course.getAdditionalCourseList().add(additionalCourse);
            courseRepository.save(course);
            log.info("Курс {} добавлен в дополнительные курсы для {}", additionalId, courseId);
        } else{
            log.warn("Курс {} уже есть в дополнительных курсах для {}", additionalId, courseId);
        }
        return course;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Course addListOfCourses(Long id, List<Course> additionalCourses){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Курс с id "+ id + " не найден"));

        for(Course additionalCourse : additionalCourses){
            if(additionalCourse.getCourseId() == null){
                courseRepository.save(additionalCourse);
            }
        }
        course.getAdditionalCourseList().addAll(additionalCourses);
        courseRepository.save(course);
        log.info("Курсы добавлены в дополнительные курсы для курса с id {}", id);
        return course;
    }

    public List<CourseDto> convertToDto(List<Course> courses){
        return courses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CourseDto convertToDto(Course course){
        return new CourseDto(
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
}
