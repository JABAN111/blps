package org.example.blps_lab1.adapters.course.service;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.exception.course.CourseNotExistException;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.exception.common.ObjectNotFoundException;
import org.example.blps_lab1.adapters.course.dto.CourseDto;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.adapters.db.course.CourseRepository;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.example.blps_lab1.core.ports.db.UserDatabase;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserDatabase userRepository;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserDatabase userRepository,
                             EmailService emailService, PlatformTransactionManager platformTransactionManager) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public Course createCourse(final Course course) {
        Course newCourse = courseRepository.save(course);
        log.info("Created course: {}", newCourse);
        return newCourse;
    }

    public Course find(final Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ObjectNotExistException("Курс с id: " + id + " не существует"));
    }

    @Override
    public Course find(String courseName) {
        return courseRepository.findByCourseName(courseName).orElseThrow(() -> new ObjectNotExistException("Курс с таким именем: " + courseName + " не существует"));
    }

    public List<Course> addAll(List<Course> courses) {
        return courseRepository.saveAll(courses);
    }


    public Course getCourseByID(final Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotExistException("Курс с таким id не существует"));
    }

    public void deleteCourse(final Long courseUUID) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                courseRepository.findById(courseUUID).orElseThrow(() -> new CourseNotExistException("Курс с таким id не существует"));
                courseRepository.deleteById(courseUUID);
                log.info("Course deleted: {}", courseUUID);
            }
        });
    }

    public List<Course> getAllCourses() {
        var list = courseRepository.findAll();
        log.info("Get courses list {}", list.size());
        return list;
    }

    public Course updateCourse(Long courseUUID, CourseDto courseDto) {
        return transactionTemplate.execute(status -> {
            if (courseRepository.findById(courseUUID).isEmpty()) {
                log.error("Course with id {} does not exist", courseUUID);
                throw new ObjectNotFoundException("Курс не найден");
            }
            return courseRepository.findById(courseUUID).map(course -> {
                course.setCourseName(courseDto.getCourseName());
                course.setCoursePrice(courseDto.getCoursePrice());
                course.setTopicName(courseDto.getTopicName());
                course.setCourseDuration(courseDto.getCourseDuration());
                course.setWithJobOffer(courseDto.getWithJobOffer());
                return courseRepository.save(course);
            }).orElseThrow(() -> {
                log.error("Course with id {} can't be updated", courseUUID);
                return new RuntimeException("Не получилось обновить курс");
            });
        });
    }

    public List<Course> enrollUser(Long userId, Long courseUUID) {
        return transactionTemplate.execute(status -> {
            UserXml user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("user not found in enroll"));

            courseRepository.findById(courseUUID)
                    .orElseThrow(() -> new RuntimeException("course not found in enroll"));

            List<Course> enrolledCourses = new ArrayList<>();

            userRepository.save(user);
            return enrolledCourses;
        });
    }

    /**
     * Утилитарная функция-транзакция для записи на дополнительные курсы, связанные с основными
     * например, если вы проходите курс "Бухгалтер будущего", и к этому курсу закреплен курс "Python для чайников",
     * то пользователя автоматически должно записать на курс по Python
     *
     * @param courseUUID           основного курса
     * @param additionalCourseUUID второстепенного курса
     * @return основной курс(тот, что с UUID: {@code courseUUID}
     */
    public Course addAdditionalCourses(Long courseUUID, Long additionalCourseUUID) {
        return transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(courseUUID)
                    .orElseThrow(() -> new ObjectNotFoundException("Курс с id " + courseUUID + " не найден"));

            courseRepository.findById(additionalCourseUUID)
                    .orElseThrow(() -> new ObjectNotFoundException("Дополнительный курс с id " + additionalCourseUUID + " не найден"));

            return course;
        });
    }

    public Course addListOfCourses(Long uuid, List<Course> additionalCourses) {
        return transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(uuid)
                    .orElseThrow(() -> new ObjectNotFoundException("Курс с uuid " + uuid + " не найден"));

            for (Course additionalCourse : additionalCourses) {
                if (additionalCourse.getCourseId() == null) {
                    courseRepository.save(additionalCourse);
                }
            }
            courseRepository.save(course);
            log.info("Курсы добавлены в дополнительные курсы для курса с uuid {}", uuid);
            return course;
        });
    }
}
