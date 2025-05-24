package labs.blps.kafkalistenerservice.service;

import labs.blps.kafkalistenerservice.model.UserFailure;

public interface UserFailureService {

    void saveFailedUser(UserFailure userFailure);
    void recoverAll();

}
