package org.example.blps_lab1.adapters.sss;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.ports.sss.SimpleStorageService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class SimpleStorageServiceWithRetry {

    private final SimpleStorageService delegate;


    public SimpleStorageServiceWithRetry(SimpleStorageService delegate) {
        this.delegate = delegate;
    }


    @Retryable(
            retryFor = {IOException.class, S3Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000))
    public void uploadWithRetry(String bucket, String key, File file) {
        log.info("retry started for bucket={}, key={}", bucket, key);
        delegate.uploadFile(bucket, key, file);
    }

}
