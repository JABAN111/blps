package org.example.blps_lab1.core.ports.course.nw;

import org.example.blps_lab1.adapters.course.dto.nw.NewModuleDto;
import org.example.blps_lab1.core.domain.course.nw.NewModule;

import java.util.List;
import java.util.UUID;

public interface NewModuleService {
    NewModule createModule(final NewModuleDto module);
    NewModule linkExercise(final UUID moduleUUID, final UUID exerciseUUID);
    NewModule getModuleByUUID(final UUID uuid);
    void deleteModule(final UUID uuid);
    List<NewModule> getAllModules(Long courseID);
    List<NewModule> getAllModules();
    NewModule updateModule(UUID uuid, NewModuleDto moduleDto);
    Boolean isModuleComplete(UUID uuid);
}
