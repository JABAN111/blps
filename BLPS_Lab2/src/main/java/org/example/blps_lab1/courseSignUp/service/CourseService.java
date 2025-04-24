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
import java.util.UUID;
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

    public Course find(final UUID id){
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


    public Course getCourseByUUID(final UUID id){
        Optional<Course> course = courseRepository.findById(id);
        if(course.isEmpty()){
            log.error("Course with id {} does not exist", id);
            throw new ObjectNotExistException("Курс с таким id не существует");
        }
        log.info("Get course by id: {}", id);
        return course.get();
    }

    public void deleteCourse(final UUID courseUUID){
        Optional<Course> deletingCourse = courseRepository.findById(courseUUID);
        if(deletingCourse.isEmpty()){
            log.error("Course with id {} does not exist", courseUUID);
            throw new RuntimeException("Курс с таким id не существует");
        }
        courseRepository.deleteById(courseUUID);
        log.info("Course deleted: {}", courseUUID);
    }

    public boolean isExist(final UUID courseUUID){
        return courseRepository.findById(courseUUID).isPresent();
    }

    public List<Course> getAllCourses(){
        var list = courseRepository.findAll();
        log.info("Get courses list {}", list.size());
        return list;
    }

    public Course updateCourse(UUID courseUUID, CourseDto courseDto){
        if(courseRepository.findById(courseUUID).isEmpty()){
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
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Course> enrollUser(Long userId, UUID courseUUID){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found in enroll"));

        Course course = courseRepository.findById(courseUUID)
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

//    public List<Course> enrollUser(Long userId, Long courseId){
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//        try{
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("user not found in enroll"));
//
//            Course course = courseRepository.findById(courseId)
//                    .orElseThrow(() -> new RuntimeException("course not found in enroll"));
//
//            List<Course> enrolledCourses = new ArrayList<>();
//
//
//            if(!user.getCourseList().contains(course)){
//                user.getCourseList().add(course);
//                enrolledCourses.add(course);
//            }
//
//            List<Course> additionalCourses = new ArrayList<>(course.getAdditionalCourseList());
//            user.getCourseList().addAll(additionalCourses);
//            emailService.informAboutNewCourses(user.getEmail(), course.getCourseName(), course.getCoursePrice(), additionalCourses);
//            enrolledCourses.addAll(additionalCourses);
//            userRepository.save(user);
//            transactionManager.commit(status);
//            return enrolledCourses;
//        }catch (Exception e){
//            transactionManager.rollback(status);
//            throw new RuntimeException("Transaction failed", e);
//        }
//    }

    /**
     * Утилитарная функция для записи на дополнительные курсы, связанные с основными
     * например, если вы проходите курс "Бухгалтер будущего", и к этому курсу закреплен курс "Python для чайников",
     * то пользователя автоматически должно записать на курс по Python
     * @param courseUUID основного курса
     * @param additionalCourseUUID второстепенного курса
     * @return основной курс(тот, что с UUID: <code>courseUUID</code>
     */
    // TODO: заменить на программную транзакцию +
    //  REQUIRED = default -> нет надобности упоминать
    @Transactional(propagation = Propagation.REQUIRED)
    public Course addAdditionalCourses(UUID courseUUID, UUID additionalCourseUUID){
        Course course = courseRepository.findById(courseUUID)
                .orElseThrow(() -> new ObjectNotFoundException("Курс с id " + courseUUID + " не найден"));

        Course additionalCourse = courseRepository.findById(additionalCourseUUID)
                .orElseThrow(() -> new ObjectNotFoundException("Дополнительный курс с id " + additionalCourseUUID + " не найден"));

        if(!course.getAdditionalCourseList().contains(additionalCourse)){
            course.getAdditionalCourseList().add(additionalCourse);
            courseRepository.save(course);
            log.info("Курс {} добавлен в дополнительные курсы для {}", additionalCourseUUID, courseUUID);
        } else{
            log.warn("Курс {} уже есть в дополнительных курсах для {}", additionalCourseUUID, courseUUID);
        }
        return course;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Course addListOfCourses(UUID uuid, List<Course> additionalCourses){
        Course course = courseRepository.findById(uuid)
                .orElseThrow(() -> new ObjectNotFoundException("Курс с uuid "+ uuid + " не найден"));

        for(Course additionalCourse : additionalCourses){
            if(additionalCourse.getCourseUUID() == null){
                courseRepository.save(additionalCourse);
            }
        }
        course.getAdditionalCourseList().addAll(additionalCourses);
        courseRepository.save(course);
        log.info("Курсы добавлены в дополнительные курсы для курса с uuid {}", uuid);
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
