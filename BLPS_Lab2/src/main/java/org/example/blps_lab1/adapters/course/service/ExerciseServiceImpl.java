package org.example.blps_lab1.adapters.course.service;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.exception.common.ObjectNotFoundException;
import org.example.blps_lab1.adapters.course.dto.ExerciseDto;
import org.example.blps_lab1.core.domain.course.Exercise;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.core.domain.course.ModuleExercise;
import org.example.blps_lab1.core.domain.course.UserExerciseProgress;
import org.example.blps_lab1.adapters.db.course.ExerciseRepository;
import org.example.blps_lab1.adapters.db.course.ModuleExerciseRepository;
import org.example.blps_lab1.adapters.db.course.ModuleRepository;
import org.example.blps_lab1.adapters.db.course.UserExerciseProgressRepository;
import org.example.blps_lab1.core.ports.course.CourseProgressService;
import org.example.blps_lab1.core.ports.course.ExerciseService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
// transactional OK
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleExerciseRepository moduleExerciseRepository;
    private final CourseProgressService courseProgressService;
    private final AuthService authService;
    private final UserExerciseProgressRepository userExerciseProgressRepository;
    private final TransactionTemplate transactionTemplate;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, ModuleRepository moduleRepository,
                               ModuleExerciseRepository moduleExerciseRepository,
                               CourseProgressService courseProgressService,
                               AuthService authService, PlatformTransactionManager platformTransactionManager,
                               UserExerciseProgressRepository userExerciseProgressRepository) {
        this.exerciseRepository = exerciseRepository;
        this.moduleRepository = moduleRepository;
        this.moduleExerciseRepository = moduleExerciseRepository;
        this.courseProgressService = courseProgressService;
        this.authService = authService;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.userExerciseProgressRepository = userExerciseProgressRepository;
    }

    public Exercise createExercise(final ExerciseDto exerciseDto) {
        return transactionTemplate.execute(status -> {
            Module module = moduleRepository.findById(exerciseDto.getModuleId())
                    .orElseThrow(() -> new ObjectNotExistException("Модуль с id " + exerciseDto.getModuleId() + " не найден"));

            Exercise newExercise = Exercise.builder()
                    .name(exerciseDto.getName())
                    .description(exerciseDto.getDescription())
                    .answer(exerciseDto.getAnswer())
                    .difficultyLevel(exerciseDto.getDifficultyLevel())
                    .localDateTime(LocalDateTime.now())
                    .build();

            newExercise = exerciseRepository.save(newExercise);
            ModuleExercise moduleExercise = new ModuleExercise();
            moduleExercise.setModule(module);
            moduleExercise.setExercise(newExercise);
            moduleExerciseRepository.save(moduleExercise);
            log.info("Exercise created {} and linked with module {}", newExercise.getId(), module.getId());
            return newExercise;
        });
    }

    public Exercise getExerciseById(final Long id) {
        return exerciseRepository.findById(id).orElseThrow(() -> new ObjectNotExistException("Задание с id" + id + " не существует"));
    }

    public void deleteExercise(final Long id) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                exerciseRepository.findById(id).orElseThrow(() -> new ObjectNotExistException("Задание с id " + id + " не существует"));
                exerciseRepository.deleteById(id);
                log.info("Exercise deleted successfully: {}", id);
            }
        });
    }

    public List<Exercise> getAllExercises() {
        var list = exerciseRepository.findAll();
        log.info("Get exercise list {}", list.size());
        return list;
    }

    public Exercise updateExercise(Long id, ExerciseDto exerciseDto) {
        return transactionTemplate.execute(status -> {
            exerciseRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Задание с id" + id + "не найден"));
            return exerciseRepository.findById(id).map(exercise -> {
                exercise.setName(exerciseDto.getName());
                exercise.setDescription(exerciseDto.getDescription());
                exercise.setAnswer(exerciseDto.getAnswer());
                exercise.setDifficultyLevel(exerciseDto.getDifficultyLevel());
                return exerciseRepository.save(exercise);
            }).orElseThrow(() -> {
                log.error("Exercise with id {} can't be updated", id);
                return new RuntimeException("Не получилось обновить задание");
            });
        });
    }

    public Boolean submitAnswer(Long exerciseId, String userAnswer) {
        return transactionTemplate.execute(status -> {
            User user = authService.getCurrentUser();
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new ObjectNotFoundException("Задание с id " + exerciseId + " не найдено"));

            UserExerciseProgress progress = userExerciseProgressRepository.findByUserAndExercise(user, exercise)
                    .orElseGet(() -> {
                        UserExerciseProgress newProgress = new UserExerciseProgress(user, exercise);
                        return userExerciseProgressRepository.save(newProgress);
                    });

            if (exercise.getAnswer().trim().equalsIgnoreCase(userAnswer.trim())) {
                if (Boolean.TRUE.equals(progress.getIsCompleted())) {
                    log.info("Exercise {} уже завершено пользователем {}", exerciseId, user.getId());
                    return true;
                }

                progress.setIsCompleted(true);
                int points = exercise.getPointsForDifficulty();
                progress.setPoints(points);
                log.info("Updating progress for exercise {}: isCompleted = {}, points = {}",
                        exercise.getId(), progress.getIsCompleted(), progress.getPoints());
                userExerciseProgressRepository.saveAndFlush(progress);

                var listExercises = exercise.getModuleExercises();
                if (listExercises == null) {
                    return false;
                }
                courseProgressService.addPoints(user.getId(),
                       listExercises.get(0).getModule().getCourse().getCourseUUID(),
                        points);

                log.info("Exercise {} завершено пользователем {}", exerciseId, user.getId());
                return true;
            }

            log.info("Exercise {} ответ пользователя {} неверный", exerciseId, user.getId());
            return false;
        });
    }



}
