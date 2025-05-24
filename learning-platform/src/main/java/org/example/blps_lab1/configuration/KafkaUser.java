package org.example.blps_lab1.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUser {
    private String username;
    private String email;
    private String password;
}
