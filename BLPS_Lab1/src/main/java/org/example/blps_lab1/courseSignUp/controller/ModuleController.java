package org.example.blps_lab1.courseSignUp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.courseSignUp.dto.ModuleDto;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.service.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/modules")
@AllArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllModules(){
        Map<String, Object> response = new HashMap<>();
        List<Module> moduleList = moduleService.getAllModules();
        List<ModuleDto> moduleDto = moduleService.convertToModelDto(moduleList);
        response.put("modules_list", moduleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getModuleById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        Module module = moduleService.getModuleById(id);
        ModuleDto moduleDto = moduleService.convertToModelDto(module);
        response.put("module", moduleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createModule(@Valid @RequestBody Module module){
        Map<String, Object> response = new HashMap<>();
        Module createdModule = moduleService.createModule(module);
        ModuleDto moduleDto = moduleService.convertToModelDto(createdModule);
        response.put("created_module", moduleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        moduleService.deleteModule(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateModule(@PathVariable Long id, @Valid @RequestBody ModuleDto moduleDto){
        Map<String, Object> response = new HashMap<>();
        Module updatedModule = moduleService.updateModule(id, moduleDto);
        response.put("message", "module updated");
        response.put("module", updatedModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/complete/{moduleId}")
    public ResponseEntity<Map<String, Object>> completeModule(@PathVariable Long moduleId){
        int earnedPoints = moduleService.completeModule(moduleId);
        Map<String, Object> response = new HashMap<>();
        response.put("moduleId", moduleId);
        response.put("earnedPoints", earnedPoints);
        response.put("message", "Модуль успешно завершён");
        return ResponseEntity.ok(response);
    }
}
