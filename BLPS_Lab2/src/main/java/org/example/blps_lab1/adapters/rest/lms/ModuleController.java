package org.example.blps_lab1.adapters.rest.lms;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.course.dto.ModuleDto;
import org.example.blps_lab1.adapters.course.mapper.ModuleMapper;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.core.ports.course.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("lmsModuleController")
@RequestMapping("/api/v1/modules")
@AllArgsConstructor
@Slf4j
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping("/course/{courseID}")
    public ResponseEntity<Map<String, Object>> getAllModules(@PathVariable Long courseID){
        Map<String, Object> response = new HashMap<>();
        List<Module> moduleList = moduleService.getAllModules(courseID);
        List<ModuleDto> moduleDto = ModuleMapper.toDto(moduleList);
        response.put("modules_list", moduleDto);
        log.info("sending data about size() = {}", moduleDto.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getModuleById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        Module module = moduleService.getModuleById(id);
        ModuleDto moduleDto = ModuleMapper.toDto(module);
        response.put("module", moduleDto);
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
