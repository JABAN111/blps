package org.example.blps_lab1.adapters.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDto {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String companyName;//NOTE: nullable field, validating only if client specified
}
