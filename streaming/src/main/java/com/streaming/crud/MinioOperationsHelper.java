package com.streaming.crud;

import com.streaming.exception.ApiException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioOperationsHelper {
    private final MinioClient minioClient;

    public InputStream getObject(String bucketName, String objectName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e.getMessage());
        }
    }
}
