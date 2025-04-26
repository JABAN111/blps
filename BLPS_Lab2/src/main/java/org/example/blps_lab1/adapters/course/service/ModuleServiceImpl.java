package org.example.blps_lab1.adapters.course.service;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.exception.common.ObjectNotFoundException;
import org.example.blps_lab1.adapters.course.dto.ModuleDto;
import org.example.blps_lab1.core.domain.course.*;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.adapters.db.course.ModuleExerciseRepository;
import org.example.blps_lab1.adapters.db.course.ModuleRepository;
import org.example.blps_lab1.adapters.db.course.UserExerciseProgressRepository;
import org.example.blps_lab1.adapters.db.course.UserModuleProgressRepository;
import org.example.blps_lab1.core.ports.course.CourseProgressService;
import org.example.blps_lab1.core.ports.course.ModuleService;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
// transactional OK
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseProgressService courseProgressService;
    private final ModuleExerciseRepository moduleExerciseRepository;
    private final EmailService emailService;
    private final UserModuleProgressRepository userModuleProgressRepository;
    private final AuthService authService;
    private final UserExerciseProgressRepository userExerciseProgressRepository;
    private final TransactionTemplate transactionTemplate;

    public ModuleServiceImpl(ModuleRepository moduleRepository, CourseProgressService courseProgressService,
                             ModuleExerciseRepository moduleExerciseRepository, EmailService emailService,
                             UserModuleProgressRepository userModuleProgressRepository, AuthService authService,
                             UserExerciseProgressRepository userExerciseProgressRepository,
                             PlatformTransactionManager platformTransactionManager) {
        this.moduleRepository = moduleRepository;
        this.courseProgressService = courseProgressService;
        this.moduleExerciseRepository = moduleExerciseRepository;
        this.emailService = emailService;
        this.userModuleProgressRepository = userModuleProgressRepository;
        this.authService = authService;
        this.userExerciseProgressRepository = userExerciseProgressRepository;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public Module createModule(final Module module) {
        return transactionTemplate.execute(status -> {
            validateModule(module);

            Course course = module.getCourse();
            List<Module> existingModules = moduleRepository.findByCourseOrderByOrderNumberAsc(course);
            if (!existingModules.isEmpty()) {
                module.setIsBlocked(true);
            }
            Module newModule = moduleRepository.save(module);
            newModule.setLocalDateTime(LocalDateTime.now());
            log.info("Module created {}", newModule);
            return newModule;
        });
    }

    public void validateModule(Module module) {
        if (module.getOrderNumber() <= 0) {
            throw new IllegalArgumentException("порядковый номер модуля должен быть больше 0");
        }
        moduleRepository.findByCourseAndOrderNumber(module.getCourse(), module.getOrderNumber())
                .ifPresent(existingModule -> {
                    throw new IllegalArgumentException("Модуль с таким порядковым номером уже существует в данном курсе");
                });
    }

    public Module getModuleById(final Long id) {
        var optionalModule = moduleRepository.findById(id);
        if (optionalModule.isEmpty()) {
            log.warn("Module with id: {} not exist", id);
            throw new ObjectNotExistException("Модуль с id" + id + " не существует");
        }
        return optionalModule.get();
    }


    public void deleteModule(final Long id) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                Optional<Module> deletingModule = moduleRepository.findById(id);
                if (deletingModule.isEmpty()) {
                    log.warn("Module with id {} doesnt exist", id);
                    throw new ObjectNotExistException("Модуль с id " + id + " не существует");
                }
                moduleRepository.deleteById(id);
                log.info("Module deleted successfully: {}", id);
            }
        });
    }

    public List<Module> getAllModules() {
        var list = moduleRepository.findAll();
        log.info("Get module list {}", list.size());
        return list;
    }

    public Module updateModule(Long id, ModuleDto moduleDto) {
        return transactionTemplate.execute(status -> {
            if (moduleRepository.findById(id).isEmpty()) {
                throw new ObjectNotFoundException("Модуль с id" + id + "не найден");
            }
            return moduleRepository.findById(id).map(module -> {
                module.setName(moduleDto.getName());
                module.setIsCompleted(moduleDto.getIsCompleted());
                module.setOrderNumber(moduleDto.getOrderNumber());
                module.setDescription(moduleDto.getDescription());
                return moduleRepository.save(module);
            }).orElseThrow(() -> {
                log.error("Module with id {} can't be updated", id);
                return new RuntimeException("Не получилось обновить модуль");
            });
        });
    }

    public Integer completeModule(Long moduleId) {
        return transactionTemplate.execute(status -> {
            User user = authService.getCurrentUser();

            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new ObjectNotFoundException("Модуль не найден"));

            List<ModuleExercise> moduleExerciseList = moduleExerciseRepository.findByModuleId(moduleId);

            boolean isExercisesCompleted = moduleExerciseList.stream()
                    .map(ModuleExercise::getExercise)
                    .filter(Objects::nonNull)
                    .allMatch(exercise -> {
                        UserExerciseProgress progress = userExerciseProgressRepository.findByUserAndExercise(user, exercise)
                                .orElseThrow(() -> new ObjectNotExistException("Прогресс для упражнения не найден"));

                        return Boolean.TRUE.equals(progress.getIsCompleted());
                    });

            if (!isExercisesCompleted) {
                throw new RuntimeException("Не все задания в модуле завершены");
            }

            UserModuleProgress userModuleProgress = userModuleProgressRepository.findByUserAndModule(user, module)
                    .orElse(new UserModuleProgress(null, user, module, false, 0));

            if (userModuleProgress.getIsCompleted()) {
                throw new RuntimeException("Модуль уже завершен для данного пользователя");
            }

            List<Module> courseModules = moduleRepository.findByCourseOrderByOrderNumberAsc(module.getCourse());

            if (module.getOrderNumber() > 1) {
                Module previousModule = courseModules.stream()
                        .filter(m -> m.getOrderNumber().equals(module.getOrderNumber() - 1))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Предыдущий модуль не найден"));

                userModuleProgressRepository.findByUserAndModule(user, previousModule)
                        .filter(UserModuleProgress::getIsCompleted)
                        .orElseThrow(() -> new IllegalStateException("Нельзя разблокировать новый модуль, пока не завершен предыдущий"));
            }

            int totalPoints = moduleExerciseList.stream()
                    .map(ModuleExercise::getExercise)
                    .filter(Objects::nonNull)
                    .mapToInt(Exercise::getPointsForDifficulty)
                    .sum();

            userModuleProgress.setIsCompleted(true);
            userModuleProgress.setPoints(totalPoints);
            userModuleProgressRepository.save(userModuleProgress);

            courseProgressService.addPoints(user.getId(), module.getCourse().getCourseUUID(), totalPoints);

            courseModules.stream()
                    .filter(m -> m.getOrderNumber().equals(module.getOrderNumber() + 1))
                    .findFirst()
                    .ifPresent(nextModule -> {
                        UserModuleProgress nextModuleProgress = userModuleProgressRepository.findByUserAndModule(user, nextModule)
                                .orElse(new UserModuleProgress(null, user, nextModule, false, 0));

                        nextModuleProgress.setIsCompleted(false);
                        userModuleProgressRepository.save(nextModuleProgress);
                    });

            emailService.informAboutModuleCompletion(user.getEmail(), module.getCourse().getCourseName(), module.getName());

            log.info("Module {} is completed by user {}", moduleId, user.getId());

            return totalPoints;
        });
    }

}
