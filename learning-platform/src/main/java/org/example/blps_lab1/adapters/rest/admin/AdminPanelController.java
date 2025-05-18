package org.example.blps_lab1.adapters.rest.admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.example.blps_lab1.adapters.admin.AdminPanelServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@AllArgsConstructor
public class AdminPanelController {

    private AdminPanelServiceImpl adminPanelService;

    /**
     * Метод для выдачи роли определенному пользователю админской роли
     */
    @PatchMapping("update-role")
    public void updateRole(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String role = body.get("role");
        adminPanelService.updateRole(email, role);
    }
}
