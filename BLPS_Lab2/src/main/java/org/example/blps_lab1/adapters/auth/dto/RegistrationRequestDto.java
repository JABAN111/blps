package org.example.blps_lab1.adapters.auth.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class RegistrationRequestDto {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String companyName;//NOTE: nullable field, validating only if client specified
}
