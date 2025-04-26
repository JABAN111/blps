package org.example.blps_lab1.core.ports.sss;

import java.io.File;

public interface SimpleStorageService {
    void uploadFile(final String username, final String filename, final File file) throws Exception;
}
