package org.example.blps_lab1.adapters.rest.auth;


import org.example.blps_lab1.adapters.auth.dto.ApplicationResponseDto;

import org.example.blps_lab1.adapters.auth.dto.JwtAuthenticationResponse;

import org.example.blps_lab1.adapters.auth.dto.LoginRequest;
import org.example.blps_lab1.adapters.auth.dto.RegistrationRequestDto;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;

    /**
     * Создает пользователя системы И создает student, привязанного к этому пользователю.
     * Любой пользователь также является студентом платформы и может проходить курсы
     * Не любой пользователь является админом и соответственно может не иметь доступа к админским ресурсам
     *
     * @param request пользовательские данные см {@link RegistrationRequestDto}
     * @return обертку с jwt токеном
     */
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody RegistrationRequestDto request) {
        return authService.signUp(request);
    }

    /**
     * Все аналогично ручке {@code signUp(@RequestBody RegistrationRequestDto request)},
     * но дополнительно привязывает пользователя на определенный курс
     *
     * @param request пользовательские данные см {@link RegistrationRequestDto}
     * @param courseUUID курс, на который пользователь планирует оставить заявку на запись
     * @return обертку с jwt токеном
     */
    @PostMapping("/sign-up/{courseUUID}")
    public ApplicationResponseDto signUp(@RequestBody RegistrationRequestDto request, @PathVariable UUID courseUUID) {
        return authService.signUp(request, courseUUID);
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody LoginRequest request) {
        return authService.signIn(request);
    }

}
