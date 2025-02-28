package org.example.blps_lab1.authorization.controller;




import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;

import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;

import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.common.service.MinioService;
import org.example.blps_lab1.export.certificate.CertificateExporter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;




@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private AuthService authService;
    private CertificateExporter certificateExporter;
    private MinioService minioService;

    @PostMapping("/sign-up")
    public ApplicationResponseDto signUp(@RequestBody RegistrationRequestDto request){
        return authService.signUp(request);
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody LoginRequest request){
        return authService.signIn(request);
    }
    
}
