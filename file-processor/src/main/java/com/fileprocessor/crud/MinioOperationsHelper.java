package com.fileprocessor.crud;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioOperationsHelper {
    private final MinioClient minioClient;

    public String generatePresignedUrl(String bucketName, String objectName, Integer expirationSeconds) {
        try {
            // Specify the HTTP method (GET for download, PUT for upload, etc.)
            // URL expiry in seconds (e.g., 1 hour)
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) // Specify the HTTP method (GET for download, PUT for upload, etc.)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirationSeconds) // URL expiry in seconds (e.g., 1 hour)
                            .build());

        } catch (Exception e) {
            log.error("Error generating presigned URL");
            e.printStackTrace();
            return null;
        }
    }

    public void uploadMultipartFile(String bucketName, String objectName,
                                    Long size, String contentType, MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadInputStream(String bucketName, String objectName, InputStream inputStream) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, -1, 1024 * 1024 * 5).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
