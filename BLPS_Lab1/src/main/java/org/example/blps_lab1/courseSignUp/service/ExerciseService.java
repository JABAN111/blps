package org.example.blps_lab1.courseSignUp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.example.blps_lab1.courseSignUp.dto.ExerciseDto;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.example.blps_lab1.courseSignUp.models.UserExerciseProgress;
import org.example.blps_lab1.courseSignUp.repository.ExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleRepository;
import org.example.blps_lab1.courseSignUp.repository.UserExerciseProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleExerciseRepository moduleExerciseRepository;
    private final CourseProgressService courseProgressService;
    private final AuthService authService;
    private final UserExerciseProgressRepository userExerciseProgressRepository;

    public Exercise createExercise(final ExerciseDto exerciseDto){
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
    }

    public Exercise getExerciseById(final Long id){
        var optionalExercise = exerciseRepository.findById(id);
        if(optionalExercise.isEmpty()){
            log.warn("Exercise with id: {} not exist", id);
            throw new ObjectNotExistException("Задание с id" + id + " не существует");
        }
        return optionalExercise.get();
    }

    public List<Exercise> saveAll(List<Exercise> exercises){
        return exerciseRepository.saveAll(exercises);
    }

    public void deleteExercise(final Long id){
        Optional<Exercise> deletingExercise = exerciseRepository.findById(id);
        if(deletingExercise.isEmpty()){
            log.warn("Exercise with id {} doesnt exist", id);
            throw new ObjectNotExistException("Задание с id "+ id + " не существует");
        }
        exerciseRepository.deleteById(id);
        log.info("Exercise deleted successfully: {}", id);
    }

    public List<Exercise> getAllExercises(){
        var list = exerciseRepository.findAll();
        log.info("Get exercise list {}", list.size());
        return list;
    }

    public Exercise updateExercise(Long id, ExerciseDto exerciseDto){
        if(exerciseRepository.findById(id).isEmpty()){
            throw new ObjectNotFoundException("Задание с id" + id + "не найден");
        }
        return exerciseRepository.findById(id).map(exercise -> {
            exercise.setName(exerciseDto.getName());
            exercise.setDescription(exerciseDto.getDescription());
            exercise.setAnswer(exerciseDto.getAnswer());
            exercise.setDifficultyLevel(exerciseDto.getDifficultyLevel());
            return exerciseRepository.save(exercise);
        }).orElseThrow(() ->{
            log.error("Exercise with id {} can't be updated", id);
            return new RuntimeException("Не получилось обновить задание");
        });
    }

    @Transactional
    public boolean submitAnswer(Long exerciseId, String userAnswer) {
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


            courseProgressService.addPoints(user.getId(),
                    exercise.getModuleExercises().get(0).getModule().getCourse().getCourseId(),
                    points);

            log.info("Exercise {} завершено пользователем {}", exerciseId, user.getId());
            return true;
        }

        log.info("Exercise {} ответ пользователя {} неверный", exerciseId, user.getId());
        return false;
    }



    public List<ExerciseDto> convertToExerciseDto(List<Exercise> exercises){
        return exercises.stream()
                .map(this::convertToExerciseDto)
                .collect(Collectors.toList());
    }

    public ExerciseDto convertToExerciseDto(Exercise exercise){
        Long moduleId = (exercise.getModuleExercises() != null && !exercise.getModuleExercises().isEmpty())
                ? exercise.getModuleExercises().get(0).getModule().getId() : null;

        return new ExerciseDto(
                exercise.getName(),
                exercise.getDescription(),
                moduleId,
                exercise.getDifficultyLevel(),
                exercise.getAnswer(),
                exercise.getLocalDateTime()
        );
    }
}
