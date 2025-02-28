package org.example.blps_lab1.courseSignUp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.example.blps_lab1.courseSignUp.dto.ModuleDto;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.example.blps_lab1.courseSignUp.repository.ModuleExerciseRepository;
import org.example.blps_lab1.courseSignUp.repository.ModuleRepository;
import org.example.blps_lab1.lms.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Module createModule(final Module module){
        Module newModule = moduleRepository.save(module);
        log.info("Module created {}", newModule);
        return newModule;
    }

    public Module getModuleById(final Long id){
        var optionalModule = moduleRepository.findById(id);
        if(optionalModule.isEmpty()){
            log.warn("Module with id: {} not exist", id);
            throw new ObjectNotExistException("Модуль с id" + id + " не существует");
        }
        return optionalModule.get();
    }

    public List<Module> saveAll(List<Module> modules){
        return moduleRepository.saveAll(modules);
    }

    public void deleteModule(final Long id){
        Optional<Module> deletingModule = moduleRepository.findById(id);
        if(deletingModule.isEmpty()){
            log.warn("Module with id {} doesnt exist", id);
            throw new ObjectNotExistException("Модуль с id "+ id + " не существует");
        }
        moduleRepository.deleteById(id);
        log.info("Module deleted successfully: {}", id);
    }

    public List<Module> getAllModules(){
        var list = moduleRepository.findAll();
        log.info("Get module list {}", list.size());
        return list;
    }

    public Module updateModule(Long id, ModuleDto moduleDto){
        if(moduleRepository.findById(id).isEmpty()){
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
    }

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
            emailService.informAboutModuleCompletion(user.getEmail(), module.getCourse().getCourseName(), module.getName());
        }
    }
}
