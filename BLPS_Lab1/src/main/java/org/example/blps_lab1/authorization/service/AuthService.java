package org.example.blps_lab1.authorization.service;

import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;

import org.example.blps_lab1.authorization.models.User;


public interface AuthService {
    
    ApplicationResponseDto signUp(RegistrationRequestDto request);
    JwtAuthenticationResponse signIn(LoginRequest request);


    User getCurrentUser();


}
