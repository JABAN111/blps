package org.example.blps_lab1.courseSignUp.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab1.courseSignUp.service.ModuleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modules")
@AllArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping("complete")
    public String completeModule(
            @RequestParam Long userId,
            @RequestParam Long moduleId
    ){
        moduleService.completeModule(userId, moduleId);
        return "Module fully completed";
    }
}
