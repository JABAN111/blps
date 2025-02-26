package org.example.blps_lab1.courseSignUp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.example.blps_lab1.courseSignUp.repository.ModuleExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleRepository;
import org.example.blps_lab1.lms.service.EmailService;
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
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void completeModule(Long userId, Long moduleId){
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ObjectNotFoundException("Модуль не найден в completeModule"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден в completeModule"));

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
        emailService.informAboutModuleCompletion(user.getEmail(), module.getCourse().getCourseName(), module.getName());

    }
}
