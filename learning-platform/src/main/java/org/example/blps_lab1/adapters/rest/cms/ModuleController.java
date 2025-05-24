package org.example.blps_lab1.adapters.rest.cms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.adapters.course.dto.nw.NewModuleDto;
import org.example.blps_lab1.adapters.course.mapper.NewModuleMapper;
import org.example.blps_lab1.core.ports.course.nw.NewModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("cmsModuleController")
@RequestMapping("/api/v1/cms/modules")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Module-controller", description = "контроллер для управления молулями")
public class ModuleController {
    private final NewModuleService newModuleService;

    @PostMapping
    @Operation(summary = "создание модуля")
    public ResponseEntity<Map<String, Object>> createModule(@RequestBody NewModuleDto moduleDto) {
        Map<String, Object> response = new HashMap<>();
        var createdModule = newModuleService.createModule(moduleDto);
        var toRet = NewModuleMapper.toDto(createdModule);
        response.put("created_module", toRet);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/link")
    @Operation(summary = "привязывает упражнение к модулю")
    public ResponseEntity<Map<String, Object>> linkExerciseToModule(@RequestParam UUID moduleUUID, @RequestParam UUID exerciseUUID) {
        Map<String, Object> response = new HashMap<>();
        var toRet = NewModuleMapper.toDto(newModuleService.linkExercise(moduleUUID, exerciseUUID));
        response.put("update_module", toRet);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping
    @Operation(summary = "получение всех существующих модулей, без указания курса")
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = new HashMap<>();
        var toRet = newModuleService.getAllModules()
                .stream()
                .map(NewModuleMapper::toDto)
                .toList();
        response.put("update_module", toRet);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "получение определенного модуля вне зависимости от курса")
    public ResponseEntity<Map<String, Object>> getSpecificModule(@PathVariable UUID uuid) {
        Map<String, Object> response = new HashMap<>();
        var toRet = NewModuleMapper.toDto(newModuleService.getModuleByUUID(uuid));
        response.put("update_module", toRet);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }


    @DeleteMapping("/{uuid}")
    @Operation(summary = "удаление модуля")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable @Parameter(description = "") UUID uuid){
        Map<String, Object> response = new HashMap<>();
        newModuleService.deleteModule(uuid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{moduleUUID}")
    @Operation(summary = "обновление модуля")
    public ResponseEntity<Map<String, Object>> updateModule(@PathVariable @Parameter(description = "uuid модуля, который вы хотиите обновить") UUID moduleUUID,
                                                            @RequestBody @Parameter(description = "новые значения") NewModuleDto moduleDto) {
        Map<String, Object> response = new HashMap<>();
        var updatedModule = NewModuleMapper.toDto(newModuleService.updateModule(moduleUUID, moduleDto));
        response.put("message", "module updated");
        response.put("module", updatedModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
