package org.example.blps_lab1.adapters.sss;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.ports.sss.SimpleStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Profile("dev")
public class SimpleStorageStubImpl implements SimpleStorageService {
    @Override
    public void uploadFile(String username, String filename, File file) throws Exception {
        log.info("start simulate working...");
        TimeUnit.SECONDS.sleep(2);
        log.info("finish simulate working...");
    }
}
