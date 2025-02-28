package org.example.blps_lab1.courseSignUp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.impl.AuthServiceImpl;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.example.blps_lab1.courseSignUp.dto.ExerciseDto;
import org.example.blps_lab1.courseSignUp.models.DifficultyLevel;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.example.blps_lab1.courseSignUp.repository.ExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Exercise createExercise(final ExerciseDto exerciseDto){
        Module module = moduleRepository.findById(exerciseDto.getModuleId())
                        .orElseThrow(() -> new ObjectNotExistException("Модуль с id " + exerciseDto.getModuleId() + " не найден"));

        Exercise newExercise = Exercise.builder()
                .name(exerciseDto.getName())
                .description(exerciseDto.getDescription())
                .isCompleted(exerciseDto.getIsCompleted())
                .answer(exerciseDto.getAnswer())
                .difficultyLevel(exerciseDto.getDifficultyLevel())
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
            exercise.setIsCompleted(exerciseDto.getIsCompleted());
            exercise.setAnswer(exerciseDto.getAnswer());
            exercise.setDifficultyLevel(exerciseDto.getDifficultyLevel());
            return exerciseRepository.save(exercise);
        }).orElseThrow(() ->{
            log.error("Exercise with id {} can't be updated", id);
            return new RuntimeException("Не получилось обновить задание");
        });
    }

    public boolean submitAnswer(Long id, String userAnswer){
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Задание с id" + id + " не найдено"));

        if((exercise.getAnswer().trim().equalsIgnoreCase(userAnswer.trim()))){
            exercise.setIsCompleted(true);
            exerciseRepository.save(exercise);

            int points = exercise.getPointsForDifficulty();
            courseProgressService.addPoints(authService.getCurrentUser().getId(), exercise.getModuleExercises().get(0).getModule().getCourse().getCourseId(), points);

            log.info("Exercise {} marked as completed", id);
            return true;
        }
        log.info("Exercise {} answer is incorrect", id);
        return false;
    }

}
