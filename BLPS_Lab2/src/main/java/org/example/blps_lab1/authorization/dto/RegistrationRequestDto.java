package org.example.blps_lab1.authorization.dto;


import lombok.Data;

@Data
public class RegistrationRequestDto {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String companyName;//NOTE: nullable field, validating only if client specified 

    private Long courseId;
}
