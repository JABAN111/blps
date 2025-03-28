package org.example.blps_lab1.authorization.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationResponseDto {
    private String description;
    private BigDecimal price;
    private JwtAuthenticationResponse jwt;
}
