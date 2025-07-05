package com.fileprocessor.service;

import com.fileprocessor.crud.MinioOperationsHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final MinioOperationsHelper minioOperationsHelper;

    public List<String> uploadDirectoryToMinio(String outputPrefix, String directory) throws IOException {
        List<String> uploadedFiles = new ArrayList<>();

        // Deleting all the existing files in the public id folder (outputPrefix) in the bucket

        Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try (FileInputStream fileInputStream =  new FileInputStream(file.toFile())) {
                        String fileName = file.getFileName().toString();
                        String s3UploadKey = outputPrefix + "/" + fileName;

                        minioOperationsHelper.uploadInputStream("original", s3UploadKey, fileInputStream);
                        fileInputStream.close();
                        uploadedFiles.add(fileName);
                    } catch (IOException e) {
                        log.error("File not found", e);
                    }
                });

        return uploadedFiles;
    }
}
