package blps.labs.rest;

import blps.labs.service.User;
import jakarta.ws.rs.core.Response;

public interface RegistrationController {

    Response register(User user);

}
