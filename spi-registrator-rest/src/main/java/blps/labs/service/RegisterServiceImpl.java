package blps.labs.service;

import org.keycloak.models.*;

public class RegisterServiceImpl implements RegisterService {

    private final KeycloakSession session;

    public RegisterServiceImpl(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Boolean register(User user) {
        try {
            RealmModel realm = session.getContext().getRealm();
            UserModel userModel = session.users().addUser(realm, user.getUsername());
            userModel.setEnabled(true);
            userModel.setEmail(user.getEmail());

            UserCredentialModel credential = UserCredentialModel.password(user.getPassword(), false);
            SubjectCredentialManager credManager = userModel.credentialManager();
            credManager.updateCredential(credential);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
