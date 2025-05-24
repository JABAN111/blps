package org.example.blps_lab1.adapters.course.service.nw;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.course.dto.nw.NewModuleDto;
import org.example.blps_lab1.adapters.course.mapper.NewModuleMapper;
import org.example.blps_lab1.adapters.db.course.NewCourseRepository;
import org.example.blps_lab1.adapters.db.course.NewExerciseRepository;
import org.example.blps_lab1.adapters.db.course.NewModuleRepository;
import org.example.blps_lab1.adapters.db.course.StudentRepository;
import org.example.blps_lab1.core.domain.course.nw.NewExercise;
import org.example.blps_lab1.core.domain.course.nw.NewModule;
import org.example.blps_lab1.core.exception.course.InvalidFieldException;
import org.example.blps_lab1.core.exception.course.NotExistException;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.ports.course.nw.NewModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewModuleServiceImpl implements NewModuleService {

    private final NewModuleRepository newModuleRepository;
    private final NewExerciseRepository newExerciseRepository;

    private final TransactionTemplate transactionTemplate;
    private final NewCourseRepository newCourseRepository;
    private final AuthService authService;
    private final StudentRepository studentRepository;

    public NewModuleServiceImpl(NewModuleRepository newModuleRepository, NewExerciseRepository newExerciseRepository, PlatformTransactionManager trManager, NewCourseRepository newCourseRepository, AuthService authService, StudentRepository studentRepository) {
        this.newModuleRepository = newModuleRepository;
        this.newExerciseRepository = newExerciseRepository;
        this.transactionTemplate = new TransactionTemplate(trManager);
        this.newCourseRepository = newCourseRepository;
        this.authService = authService;
        this.studentRepository = studentRepository;
    }

    @Override
    public NewModule createModule(NewModuleDto module) {
        if (module == null) {
            log.error("user try to create null module");
            throw new InvalidFieldException("Не хватает информации для создания курса");
        }
        if (module.getName().isEmpty() ||
                module.getDescription().isEmpty()) {
            throw new InvalidFieldException("Поля name, description и total points являются обязательными");
        }

        return newModuleRepository.save(NewModuleMapper.toEntity(module));
    }

    @Override
    public NewModule linkExercise(UUID moduleUUID, UUID exerciseUUID) {
        return transactionTemplate.execute(status -> {
            var exerciseEntity = newExerciseRepository.findById(exerciseUUID).orElseThrow(() -> new NotExistException("Упражнения с uuid: " + exerciseUUID + " не существует"));
            var moduleEntity = newModuleRepository.findById(moduleUUID).orElseThrow(() -> new NotExistException("Модуль с uuid: " + exerciseUUID + " не существует"));

            moduleEntity.getExercises().add(exerciseEntity);
            return newModuleRepository.save(moduleEntity);
        });
    }

    /**
     * Возвращает именно модуль без привязки к какому-либо курсу.
     * Данный метод во многом утилитарный для админских ролей
     *
     * @param uuid модуля
     * @return {@link NewModule}
     */
    @Override
    public NewModule getModuleByUUID(UUID uuid) {
        return newModuleRepository.findById(uuid).orElseThrow(() -> new NotExistException("Модуль с uuid: " + uuid + " не существует"));
    }

    @Override
    public void deleteModule(UUID uuid) {
        transactionTemplate.execute(status -> {
            var module = newModuleRepository.findById(uuid).orElseThrow(() -> new NotExistException("Модуля с таким uuid не существует"));

            newCourseRepository.removeByNewModuleList(List.of(module));
            newModuleRepository.deleteById(uuid);

            return 0;
        });
    }

    @Override
    public List<NewModule> getAllModules(Long courseID) {
        return List.of();
    }

    /**
     * Возвращает именно модули без привязки к какому-либо курсу.
     * Данный метод во многом утилитарный для админских ролей
     *
     * @return list of = {@link NewModule}
     */
    @Override
    public List<NewModule> getAllModules() {
        return newModuleRepository.findAll();
    }

    @Override
    public NewModule updateModule(UUID uuid, NewModuleDto moduleDto) {
        return transactionTemplate.execute(status -> {
            if (moduleDto.getName().isEmpty() ||
                    moduleDto.getDescription().isEmpty()) {
                throw new InvalidFieldException("Поля name, description и total points являются обязательными");
            }
            var oldEntity = newModuleRepository.findById(uuid).orElseThrow(() -> new NotExistException("модуля с заданным " +
                    "uuid не существует"));

            var newEntity = NewModuleMapper.toEntity(moduleDto);
            newEntity.setCreatedAt(oldEntity.getCreatedAt());
            newEntity.setUuid(uuid);
            newEntity.setExercises(oldEntity.getExercises());

            newModuleRepository.save(newEntity);
            return newEntity;
        });
    }

    @Override
    public Boolean isModuleComplete(UUID uuid) {
        return transactionTemplate.execute(status -> {
            var moduleEntity = newModuleRepository.findById(uuid).orElseThrow(() -> new NotExistException("модуля с заданным " +
                    "uuid не существует"));

            var moduleExercises = moduleEntity.getExercises();
            var sumPoints = moduleExercises.stream()
                    .mapToInt(NewExercise::getPoints)
                    .sum();
            var requiredPoints = sumPoints * 0.75;


            var student = studentRepository.findByUsid(authService.getCurrentUser().getId()).orElseThrow(() -> new NotExistException("Пользователь временно недоступен"));

            Set<UUID> finishedExerciseIds = student.getFinishedExercises()
                    .stream()
                    .map(NewExercise::getUuid)
                    .collect(Collectors.toSet());

            int earnedPoints = moduleExercises.stream()
                    .filter(e -> finishedExerciseIds.contains(e.getUuid()))
                    .mapToInt(NewExercise::getPoints)
                    .sum();
            return earnedPoints >= requiredPoints;
        });
    }
}
