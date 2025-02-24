package org.example.blps_lab1.courseSignUp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.example.blps_lab1.courseSignUp.repository.ModuleExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseProgressService courseProgressService;
    private final ModuleExerciseRepository moduleExerciseRepository;

    public void completeModule(Long userId, Long moduleId){
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module not found in completeModule"));

        List<ModuleExercise> moduleExerciseList = moduleExerciseRepository.findByModuleId(moduleId);

        boolean isExercisesCompleted = moduleExerciseList.stream()
                .map(ModuleExercise::getExercise)
                .allMatch(Exercise::getIsCompleted);

        if(isExercisesCompleted){
            module.setIsCompleted(true);
            moduleRepository.save(module);
            log.info("Module {} is completed by user {}", moduleId, userId);

            courseProgressService.addPoints(userId, module.getCourse().getCourseId(), 10);
        }

    }
}
