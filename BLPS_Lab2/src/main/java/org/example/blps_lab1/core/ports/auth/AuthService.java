package org.example.blps_lab1.core.ports.auth;

import org.example.blps_lab1.adapters.auth.dto.ApplicationResponseDto;
import org.example.blps_lab1.adapters.auth.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.adapters.auth.dto.LoginRequest;
import org.example.blps_lab1.adapters.auth.dto.RegistrationRequestDto;

import org.example.blps_lab1.core.domain.auth.User;

import java.util.UUID;


public interface AuthService {

    /**
     * Регистрирует пользователя на платформе и возвращает ему jwt токен
     * @param request включает в себя поля для регистрации поля
     * @return jwt токена
     */
    JwtAuthenticationResponse signUp(RegistrationRequestDto request);

    /**
     * Регистрирует пользователя на платформе И СОЗДАЕТ ЗАЯВКУ НА ЗАПИСЬ НА КУРС.
     * Статус заявки подтверждается отдельно, здесь создается именно заявка: <code>Application</code>
     * @param request включает в себя поля для регистрации поля
     * @param courseUUID uuid курса, на который записывается пользователь.
     *                   Если курса не существует, выбрасывает ошибку <code>CourseNotExistException()</code>
     *                   Если uuid не указан, выбрасывает ошибку <code>FieldNotSpecifiedException()</code>
     * @return ApplicationResponseDto
     */
    ApplicationResponseDto signUp(RegistrationRequestDto request, UUID courseUUID);

    JwtAuthenticationResponse signIn(LoginRequest request);


    User getCurrentUser();


}
