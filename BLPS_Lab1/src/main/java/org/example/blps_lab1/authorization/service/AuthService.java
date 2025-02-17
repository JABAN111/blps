package org.example.blps_lab1.authorization.service;

import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private UserService userService;


   /* public ResponseEntity<Map<String, Object>> login(LoginRequest loginRequest){
        User user = userService.validateUser(loginRequest);
        Map<String, Object> successResponse = new HashMap<>();

    }*/
}
