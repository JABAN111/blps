package org.example.blps_lab1.adapters.db.saga;

import org.example.blps_lab1.core.domain.saga.FailureRecord;
import org.example.blps_lab1.core.domain.saga.SagaFailedStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FailureRecordRepository extends JpaRepository<FailureRecord, Long> {
    List<FailureRecord> findAllBySagaFailedStep(SagaFailedStep sagaFailedStep);
}
