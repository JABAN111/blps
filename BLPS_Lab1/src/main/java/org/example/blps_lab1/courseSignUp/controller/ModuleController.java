package org.example.blps_lab1.courseSignUp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.courseSignUp.dto.ModuleDto;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.service.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/modules")
@AllArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllModules(){
        Map<String, Object> response = new HashMap<>();
        response.put("modules_list", moduleService.getAllModules());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getModuleById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        response.put("module", moduleService.getModuleById(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createModule(@Valid @RequestBody Module module){
        Map<String, Object> response = new HashMap<>();
        Module createdModule = moduleService.createModule(module);
        response.put("created_module", createdModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        moduleService.deleteModule(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateModule(@PathVariable Long id, @Valid @RequestBody ModuleDto moduleDto){
        Map<String, Object> response = new HashMap<>();
        Module updatedModule = moduleService.updateModule(id, moduleDto);
        response.put("message", "module updated");
        response.put("module", updatedModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeModule(
            @RequestParam Long userId,
            @RequestParam Long moduleId
    ){
        int earnedPoints = moduleService.completeModule(userId, moduleId);
        Map<String, Object> response = new HashMap<>();
        response.put("moduleId", moduleId);
        response.put("earnedPoints", earnedPoints);
        response.put("message", "Модуль успешно завершён");
        return ResponseEntity.ok(response);
    }
}
