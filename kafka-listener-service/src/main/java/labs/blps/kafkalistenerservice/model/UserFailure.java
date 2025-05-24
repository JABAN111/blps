package labs.blps.kafkalistenerservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UserFailure {
    @Id
    private String username;
    private String email;
    private String password;
    private Boolean isFailed = true;
}
