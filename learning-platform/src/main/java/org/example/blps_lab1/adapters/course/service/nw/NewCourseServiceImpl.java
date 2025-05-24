package org.example.blps_lab1.adapters.course.service.nw;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.example.blps_lab1.adapters.course.dto.nw.NewCourseDto;
import org.example.blps_lab1.adapters.course.mapper.NewCourseMapper;
import org.example.blps_lab1.adapters.db.auth.ApplicationRepository;
import org.example.blps_lab1.adapters.db.course.NewCourseRepository;
import org.example.blps_lab1.adapters.db.course.NewModuleRepository;
import org.example.blps_lab1.adapters.db.course.StudentRepository;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.domain.course.nw.NewExercise;
import org.example.blps_lab1.core.exception.course.InvalidFieldException;
import org.example.blps_lab1.core.exception.course.NotExistException;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.core.ports.course.nw.NewCourseService;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewCourseServiceImpl implements NewCourseService {
    private final TransactionTemplate transactionTemplate;
    private final NewCourseRepository newCourseRepository;
    private final ApplicationRepository applicationRepository;
    private final NewModuleRepository newModuleRepository;
    private final StudentRepository studentRepository;
    private final UserService userService;

    public NewCourseServiceImpl(PlatformTransactionManager transactionManager, NewCourseRepository newCourseRepository, ApplicationRepository applicationRepository, NewModuleRepository newModuleRepository, StudentRepository studentRepository, UserService userService) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.newCourseRepository = newCourseRepository;
        this.applicationRepository = applicationRepository;
        this.newModuleRepository = newModuleRepository;
        this.studentRepository = studentRepository;
        this.userService = userService;
    }

    @Override
    public NewCourse createCourse(NewCourseDto dto) {
        validateRequiredFieldsOrThrow(dto);
        return newCourseRepository.save(NewCourseMapper.toEntity(dto));
    }

    @Override
    public NewCourse find(UUID uuid) {
        return newCourseRepository.findById(uuid).orElseThrow(() -> new NotExistException("Курс с uuid " + uuid + " не найден"));
    }

    @Override
    public List<NewCourse> addAll(List<NewCourse> listDtos) {
        if (listDtos == null) {
            log.warn("addAll got null list of dtos");
            throw new InvalidFieldException("Курсы к созданию не указаны");
        }

        return newCourseRepository.saveAll(listDtos);
    }

    @Override
    public NewCourse getCourseByUUID(UUID uuid) {
        return newCourseRepository.findById(uuid).orElseThrow(() -> new NotExistException("Курс с uuid " + uuid + " не найден"));
    }

    @Override
    public void deleteCourse(UUID courseUUID) {
        transactionTemplate.execute(status -> {
            applicationRepository.deleteAllByNewCourse_Uuid(courseUUID);

            newCourseRepository.deleteById(courseUUID);
            return 0;
        });
    }

    @Override
    public List<NewCourse> getAllCourses() {
        return newCourseRepository.findAll();
    }

    @Override
    public NewCourse updateCourse(UUID courseUUID, NewCourseDto courseDto) {
        return transactionTemplate.execute(status -> {
            validateRequiredFieldsOrThrow(courseDto);

            var oldEntity = newCourseRepository.findById(courseUUID).orElseThrow(() -> new NotExistException("Курс с uuid " + courseUUID + " не найден"));
            var newEntity = NewCourseMapper.toEntity(courseDto);

            newEntity.setUuid(courseUUID);
            newEntity.setCreationTime(oldEntity.getCreationTime());
            newEntity.setAdditionalCourseList(oldEntity.getAdditionalCourseList());
            newEntity.setAdditionalCourseList(oldEntity.getAdditionalCourseList());
            if (oldEntity.getNewModuleList() != null) {
                newEntity.getNewModuleList().addAll(oldEntity.getNewModuleList());
            }
            return newCourseRepository.save(newEntity);
        });
    }

    @Override
    public List<NewCourse> enrollStudent(Long studentID, UUID courseUUID) {
        return transactionTemplate.execute(status -> {
            var studentEntity = studentRepository.findById(studentID).orElseThrow(() -> new NotExistException("Студент с id: " + studentID + " не найден"));
            var courseEntity = newCourseRepository.findById(courseUUID).orElseThrow(() -> new NotExistException("Курс с uuid " + courseUUID + " не найден"));

            studentEntity.getCourses().add(courseEntity);
            return studentRepository.save(studentEntity).getCourses();
        });

    }

    @Override
    public NewCourse addAdditionalCourses(UUID courseUUID, UUID additionalCourseUUID) {
        return transactionTemplate.execute(status -> {
            var coreCourse = newCourseRepository.findById(courseUUID).orElseThrow(() -> new NotExistException("Курс с uuid " + courseUUID + " не найден"));
            var additionalCourse = newCourseRepository.findById(additionalCourseUUID).orElseThrow(() -> new NotExistException("Курс с uuid " + additionalCourseUUID + " не найден"));

            coreCourse.getAdditionalCourseList().add(additionalCourse);
            return newCourseRepository.save(coreCourse);
        });
    }

    @Override
    public NewCourse linkModule(UUID courseUUID, UUID moduleUUID) {
        return transactionTemplate.execute(status -> {
            var courseEntity = newCourseRepository.findById(courseUUID).orElseThrow(() -> new NotExistException("Курса с uuid: " + courseUUID + " не существует"));
            var moduleEntity = newModuleRepository.findById(moduleUUID).orElseThrow(() -> new NotExistException("Модуль с uuid: " + moduleUUID + " не существует"));

            courseEntity.getNewModuleList().add(moduleEntity);
            return newCourseRepository.save(courseEntity);
        });
    }

    @Override
    public Boolean isCourseFinished(UUID courseUUID) {
        return transactionTemplate.execute(status -> {
            var courseEntity = newCourseRepository.findById(courseUUID).orElseThrow(() -> new NotExistException("Курса с uuid: " + courseUUID + " не существует"));

            for (var module : courseEntity.getNewModuleList()) {
                if (!isModuleFinished(module.getUuid())) {
                    return false;
                }
            }
            return true;
        });

    }

    /**
     * Утилитарный метод для проверки полей {@link NewCourseDto}
     * Проверяет обязательные поля
     * В случае невалидных данных выбрасывает ошибку {@link InvalidFieldException}
     *
     * @param dto {@link NewCourseDto}
     * @throws InvalidFieldException в случае невалидных данных
     */
    private void validateRequiredFieldsOrThrow(NewCourseDto dto) throws InvalidFieldException {
        if (dto == null) {
            log.warn("course dto somehow is nil");
            throw new InvalidFieldException("Не хватает информации для создания курса");
        }
        if (dto.getName().isEmpty() ||
                dto.getDescription().isEmpty() ||
                dto.getPrice() == null ||
                dto.getTopic() == null) {
            log.warn("user not specified some not null fields, {}", dto);
            throw new InvalidFieldException("Поля name, description, price и topic являются обязательными");
        }
    }

    private UserXml getCurrentUser() {
        //copypaste cause depend cycle
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            return userService.getUserByEmail(username);
        } else {
            throw new IllegalStateException("Current user is not authenticated");
        }
    }

    private Boolean isModuleFinished(UUID uuid) {
        var moduleEntity = newModuleRepository.findById(uuid).orElseThrow(() -> new NotExistException("модуля с заданным " +
                "uuid не существует"));

        var moduleExercises = moduleEntity.getExercises();
        var sumPoints = moduleExercises.stream()
                .mapToInt(NewExercise::getPoints)
                .sum();
        var requiredPoints = sumPoints * 0.75;


        var student = studentRepository.findByUsid(getCurrentUser().getId()).orElseThrow(() -> new NotExistException("Пользователь временно недоступен"));

        Set<UUID> finishedExerciseIds = student.getFinishedExercises()
                .stream()
                .map(NewExercise::getUuid)
                .collect(Collectors.toSet());

        int earnedPoints = moduleExercises.stream()
                .filter(e -> finishedExerciseIds.contains(e.getUuid()))
                .mapToInt(NewExercise::getPoints)
                .sum();
        return earnedPoints >= requiredPoints;
    }
}
