package org.example.blps_lab1.adapters.rest.cms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.adapters.course.dto.ModuleDto;
import org.example.blps_lab1.adapters.course.mapper.ModuleMapper;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.core.ports.course.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("cmsModuleController")
@RequestMapping("/api/v1/modules")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Module-controller", description = "контроллер для управления молулями")
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping
    @Operation(summary = "создание модуля")
    public ResponseEntity<Map<String, Object>> createModule(@Valid @RequestBody Module module){
        Map<String, Object> response = new HashMap<>();
        Module createdModule = moduleService.createModule(module);
        ModuleDto moduleDto = ModuleMapper.toDto(createdModule);
        response.put("created_module", moduleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удаление модуля")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable @Parameter(description = "") Long id){
        Map<String, Object> response = new HashMap<>();
        moduleService.deleteModule(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "обновление модуля")
    public ResponseEntity<Map<String, Object>> updateModule(@PathVariable @Parameter(description = "") Long id, @Valid @RequestBody @Parameter(description = "") ModuleDto moduleDto){
        Map<String, Object> response = new HashMap<>();
        Module updatedModule = moduleService.updateModule(id, moduleDto);
        response.put("message", "module updated");
        response.put("module", updatedModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
