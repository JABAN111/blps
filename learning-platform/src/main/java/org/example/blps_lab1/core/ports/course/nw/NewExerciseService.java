package org.example.blps_lab1.core.ports.course.nw;

import org.example.blps_lab1.adapters.course.dto.nw.NewExerciseDto;
import org.example.blps_lab1.core.domain.course.nw.NewExercise;

import java.util.List;
import java.util.UUID;

public interface NewExerciseService {

    /**
     * Создает упражнение. Важно, упражнение НИКАК не привязано модулям, чтобы любой модуль
     * мог переиспользовать любые упражнения
     *
     * @param exerciseDto объект типа {@link NewExerciseDto}, все поля обязательны
     * @return {@link NewExercise}
     */
    NewExercise createNewExercise(final NewExerciseDto exerciseDto);

    /**
     * Возвращает именно упражнение без привязки к какому-либо модулю. Данный метод во многом утилитарный для админов
     *
     * @param uuid упражнения
     * @return {@link NewExercise}
     */
    NewExercise getNewExerciseByUUID(final UUID uuid);

    void deleteNewExercise(final UUID id);

    /**
     * Возвращает именно упражнения без привязки к какому-либо модулю. Данный метод во многом утилитарный для админов
     *
     * @return упражнения {@link NewExercise}
     */
    List<NewExercise> getAllExercises();

    /**
     * Обновляет указанное упраженение. Важно, данный метод обновит упражнение и все модули, которые на него ссылаются
     * также обновятся
     *
     * @param uuid        курса, который обновляем
     * @param exerciseDto новые данные курса. Важно, данные должны быть полными
     * @return новое обновленное упражнение
     */
    NewExercise updateNewExercise(UUID uuid, NewExerciseDto exerciseDto);

    Boolean submitAnswer(UUID exerciseUUID, String userAnswer);
}
