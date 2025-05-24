package org.example.blps_lab1.adapters.sss;

import io.minio.*;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.ports.sss.SimpleStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
@Profile("!stage")
public class MinioServiceImpl implements SimpleStorageService {

    private final String BUCKET_NAME = "certificates";

    @Value("${minio.root.username}")
    private String username;

    @Value("${minio.root.pwd}")
    private String password;

    @Value("${minio.endpoint}")
    private String endpoint;

    private MinioClient minioClient;

    @PostConstruct
    public void reinitMinio() {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint.trim())
                .credentials(username, password)
                .build();
        ensureBucketExists();
    }

    @Override
    public void uploadFile(final String username, final String filename, final File file) {
        final String filenameForStoring = getNewFileName(username, filename);

        try (InputStream fileStream = new FileInputStream(file)) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filenameForStoring)
                    .stream(fileStream, file.length(), -1)
                    .contentType("application/octet-stream")
                    .build();

            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", filenameForStoring, e);
            throw new S3Exception(e.getMessage());
        }
        log.info("File successfully uploaded: {}", filenameForStoring);
    }

    private String getNewFileName(String username, String filename) {
        return username.trim() + "/" + filename.trim() + ".pdf";
    }

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while creating bucket: " + BUCKET_NAME + " for MinIO", e);
        }
    }


}