package blps.labs.provider;

import blps.labs.rest.RegistrationControllerImpl;
import blps.labs.service.RegisterService;
import blps.labs.service.RegisterServiceImpl;
import com.google.auto.service.AutoService;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@AutoService(RealmResourceProvider.class)
public class RegistrationProvider implements RealmResourceProvider {
    private final KeycloakSession session;

    public RegistrationProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        RegisterService service = new RegisterServiceImpl(session);
        return new RegistrationControllerImpl(service);
    }

    @Override
    public void close() {

    }
}
