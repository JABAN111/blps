package labs.blps.kafkalistenerservice.repository;

import labs.blps.kafkalistenerservice.model.UserFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFailureRepository extends JpaRepository<UserFailure, String> {
    List<UserFailure> findAllByIsFailed(Boolean isFailed);
}
