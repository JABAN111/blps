package org.example.blps_lab1.adapters.sss;

import io.minio.*;


import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.ports.sss.SimpleStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class MinioServiceImpl implements SimpleStorageService {

    private final String BUCKET_NAME = "certificates";

    @Value("${minio.root.username}")
    private String username;

    @Value("${minio.root.pwd}")
    private String password;

    private String accessKey = "JABA_SUPER_USER_MINIO";

    private String secretKey = "jaba127!368601NO";//NOTE: почему-то не отрабатывает нормально @value

    private final MinioClient minioClient;

    public MinioServiceImpl() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials(accessKey, secretKey)
                .build();
        ensureBucketExists();
    }

    @Override
    public void uploadFile(final String username, final String filename, final File file) throws Exception {

        final String filenameForStoring = getNewFileName(username, filename);

        try (InputStream fileStream = new FileInputStream(file)) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filenameForStoring)
                    .stream(fileStream, file.length(), -1)
                    .contentType("application/octet-stream")
                    .build();

            minioClient.putObject(args);
            log.info("File successfully uploaded: {}", filenameForStoring);
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", filenameForStoring, e);
            throw new Exception(e);
        }
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