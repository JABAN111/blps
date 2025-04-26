package org.example.blps_lab1.adapters.auth.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDto {
    private String description;
    private BigDecimal price;
    private JwtAuthenticationResponse jwt;
    private Long applicationID;
}
