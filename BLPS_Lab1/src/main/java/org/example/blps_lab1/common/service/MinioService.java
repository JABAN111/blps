package org.example.blps_lab1.common.service;

import io.minio.*;

import io.minio.messages.Item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.*;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MinioService  {

    private final String BUCKET_NAME = "certificates";

    @Value("${minio.root.username}")
    private String username;

    @Value("${minio.root.pwd}")
    private String password;

    private String accessKey = "JABA_SUPER_USER_MINIO";

    private String secretKey = "jaba127!368601NO";//NOTE: почему-то не отрабатывает нормально @value

    private final MinioClient minioClient;

    public MinioService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials(accessKey, secretKey)
                .build();
        ensureBucketExists();
    }

    public String uploadFile(final String username, final String filename, final File file) {

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
            return filenameForStoring;
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", filenameForStoring, e);
            throw new RuntimeException(e);
        }
    }


    public byte[] downloadFile(final String filename) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filename)
                    .build();

            InputStream fileStream = minioClient.getObject(args);
            byte[] fileBytes = fileStream.readAllBytes();

            fileStream.close();

            log.info("File successfully downloaded: {}", filename);

            return fileBytes;
        } catch (Exception e) {
            log.error("Error occurred while downloading file", e);
            throw new RuntimeException(e);
        }
    }


    public void removeFile(final String username, final String filename) {
        String filenameForStoring = getNewFileName(username, filename);

        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filenameForStoring)
                    .build();
            minioClient.removeObject(args);
            log.info("File successfully removed: {}", filenameForStoring);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> listFilesWithPrefix(String username) {
        String prefix = username.trim() + "/";
        return listFilesWithPrefixOrWithout(prefix);
    }

    //FIXME: удалить? method for admin only
    public List<String> listFilesWithoutPrefix() {
        return listFilesWithPrefixOrWithout(null);
    }

    private List<String> listFilesWithPrefixOrWithout(String prefix) {
        ListObjectsArgs.Builder listObjectsArgsBuilder = ListObjectsArgs.builder().bucket(BUCKET_NAME);

        if (prefix != null) {
            listObjectsArgsBuilder.prefix(prefix);
        }

        Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgsBuilder.build());
        List<String> fileNames = new ArrayList<>();

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                fileNames.add(item.objectName());
            } catch (Exception e) {
                log.error("Error while processing item: {}", e.getMessage());
                throw new RuntimeException("Error while processing item", e);
            }
        }

        return fileNames;
    }

    private String getNewFileName(String username, String filename) {
        return username.trim() + "/" + filename.trim();
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