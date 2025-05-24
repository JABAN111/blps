package blps.labs.rest;

import blps.labs.service.RegisterService;
import blps.labs.service.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;


public class RegistrationControllerImpl implements RegistrationController{

    private final RegisterService service;

    public RegistrationControllerImpl(RegisterService service) {
        this.service = service;
    }

    @POST
    @Path("/register")
    @Consumes({"application/json", "application/json;charset=UTF-8"})
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response register(User user) {
        if (user == null) {
            throw new BadRequestException("request must contain user data");
        }
        if (service.register(user)) {
            return Response
                    .ok()
                    .entity(Map.of("success", true,"message","user successfully registered"))
                    .build();
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(Map.of("success", false,"message","fail to save user in keycloak system"))
                .build();
    }
}
