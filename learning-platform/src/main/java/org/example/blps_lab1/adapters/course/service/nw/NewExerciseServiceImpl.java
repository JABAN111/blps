package org.example.blps_lab1.adapters.course.service.nw;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import org.aspectj.weaver.ast.Not;
import org.example.blps_lab1.adapters.course.dto.nw.NewExerciseDto;
import org.example.blps_lab1.adapters.course.mapper.NewExerciseMapper;
import org.example.blps_lab1.adapters.db.course.NewExerciseRepository;
import org.example.blps_lab1.adapters.db.course.NewModuleRepository;
import org.example.blps_lab1.adapters.db.course.StudentRepository;
import org.example.blps_lab1.core.domain.course.nw.NewExercise;
import org.example.blps_lab1.core.exception.course.InvalidFieldException;
import org.example.blps_lab1.core.exception.course.NotExistException;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.ports.course.nw.NewExerciseService;
import org.springframework.boot.actuate.metrics.startup.StartupTimeMetricsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NewExerciseServiceImpl implements NewExerciseService {
    private final NewExerciseRepository newExerciseRepository;
    private final TransactionTemplate transactionTemplate;
    private final NewModuleRepository newModuleRepository;
    private final StudentRepository studentRepository;
    private final AuthService authService;

    public NewExerciseServiceImpl(NewExerciseRepository newExerciseRepository, PlatformTransactionManager transactionManager, NewModuleRepository newModuleRepository, StudentRepository studentRepository, AuthService authService) {
        this.newExerciseRepository = newExerciseRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.newModuleRepository = newModuleRepository;
        this.studentRepository = studentRepository;
        this.authService = authService;
    }

    /**
     * Создает упражнение. Важно, упражнение НИКАК не привязано модулям, чтобы любой модуль
     * мог переиспользовать любые упражнения
     *
     * @param exerciseDto объект типа {@link NewExerciseDto}, все поля обязательны
     * @return {@link NewExercise}
     */
    @Override
    public NewExercise createNewExercise(NewExerciseDto exerciseDto) {
        if (exerciseDto == null) {
            log.warn("exercise dto somehow is nil");
            throw new InvalidFieldException("Не хватает информации для создания упражнения");
        }
        if (exerciseDto.getName().isEmpty()
                || exerciseDto.getDescription().isEmpty()
                || exerciseDto.getAnswer().isEmpty()
                || exerciseDto.getPoints() == null
        ) {
            log.warn("user not specified some of the fields");
            throw new InvalidFieldException("Поля name, description, answer и points обязательны");
        }
        if (exerciseDto.getPoints() < 0) {
            log.warn("user try to specify negative value for points: {}", exerciseDto.getPoints());
            throw new InvalidFieldException("Поля points должно быть целым положительным числом");
        }

        return newExerciseRepository.save(
                NewExerciseMapper.toEntity(exerciseDto)
        );
    }

    /**
     * Возвращает именно упражнение без привязки к какому-либо модулю.
     * Данный метод во многом утилитарный для админских ролей
     *
     * @param uuid упражнения
     * @return {@link NewExercise}
     */
    @Override
    public NewExercise getNewExerciseByUUID(UUID uuid) {
        return newExerciseRepository.findById(uuid).orElseThrow(() -> new NotExistException("упражнения с uuid: " + uuid + " не существует"));
    }


    @Override
    public void deleteNewExercise(UUID uuid) {
        transactionTemplate.execute(status -> {
            var exercise = newExerciseRepository.findById(uuid).orElseThrow(() -> new NotExistException("упражнения с uuid: " + uuid + " не существует"));

            newModuleRepository.removeByExercises(List.of(exercise));
            newExerciseRepository.delete(exercise);

            return 0;
        });
    }

    /**
     * Возвращает именно упражнения без привязки к какому-либо модулю. Данный метод во многом утилитарный для админов
     *
     * @return упражнения {@link NewExercise}
     */
    @Override
    public List<NewExercise> getAllExercises() {
        return newExerciseRepository.findAll();
    }

    /**
     * Обновляет указанное упраженение. Важно, данный метод обновит упражнение и все модули, которые на него ссылаются
     * также обновятся
     *
     * @param uuid        курса, который обновляем
     * @param exerciseDto новые данные курса. Важно, данные должны быть полными
     * @return новое обновленное упражнение
     */
    @Override
    public NewExercise updateNewExercise(UUID uuid, NewExerciseDto exerciseDto) {
        return transactionTemplate.execute(status -> {
            var exerciseToUpdate = newExerciseRepository.findById(uuid).orElseThrow(() -> new NotExistException("Упражнения с таким uuid не существует"));
            var toSave = NewExerciseMapper.toEntity(exerciseDto);
            toSave.setUuid(exerciseToUpdate.getUuid());
            return newExerciseRepository.save(toSave);
        });
    }

    @Override
    public Boolean submitAnswer(UUID exerciseUUID, String userAnswer) {
        return transactionTemplate.execute(status -> {
            var exercise = newExerciseRepository.findById(exerciseUUID).orElseThrow(() -> new NotExistException("упражнения с uuid: " + exerciseUUID + " не существует"));
            var userID = authService.getCurrentUser().getId();

            var student = studentRepository.findByUsid(userID).orElseThrow(() -> new NotExistException("Пользователь временно недоступен для операций"));
            if (exercise.getAnswer().equals(userAnswer)) {
                student.getFinishedExercises().add(exercise);
                return true;
            }
            return false;
        });
    }
}
