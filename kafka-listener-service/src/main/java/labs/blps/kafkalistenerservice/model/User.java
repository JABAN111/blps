package labs.blps.kafkalistenerservice.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String username;
    private String email;
    private String password;
}
