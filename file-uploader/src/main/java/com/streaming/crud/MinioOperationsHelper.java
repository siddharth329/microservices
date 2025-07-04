package com.streaming.crud;

import com.streaming.exception.FileDeleteException;
import com.streaming.exception.FileUploadException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioOperationsHelper {
    private final MinioClient minioClient;

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
            throw new FileUploadException("Error uploading file");
        }
    }

    public void deleteObject(String bucketName, String objectName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new FileDeleteException(e.getMessage());
        }
    }

    public InputStream getObject(String bucketName, String objectName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
