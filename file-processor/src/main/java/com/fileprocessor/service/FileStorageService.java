package com.fileprocessor.service;

import com.fileprocessor.crud.MinioOperationsHelper;
import com.fileprocessor.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String fileName = file.getFileName().toString();
                        String s3UploadKey = outputPrefix + "/" + fileName;
                        String contentType = FileUtils.getFileContentType(fileName);

                        minioOperationsHelper.uploadInputStream("original", s3UploadKey, new FileInputStream(file.toFile()));
                        uploadedFiles.add(fileName);
                    } catch (FileNotFoundException e) {
                        log.error("File not found", e);
                    }
                });

        return uploadedFiles;
    }
}
