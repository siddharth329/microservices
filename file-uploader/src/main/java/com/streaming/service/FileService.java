package com.streaming.service;

import com.streaming.crud.MinioOperationsHelper;
import com.streaming.enums.FileStatus;
import com.streaming.exception.FileUploadException;
import com.streaming.exception.ResourceNotFoundException;
import com.streaming.messaging.FileProcessingQueueMessenger;
import com.streaming.models.File;
import com.streaming.repository.FileRepository;
import com.commons.utils.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final MinioOperationsHelper minioOperationsHelper;
    private final FileProcessingQueueMessenger fileProcessingQueueMessenger;
    @Value("${minio.bucketName}") private String bucketName;

    public Page<File> getFiles(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }

    public File getFile(UUID id) {
        return fileRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }

    public void deleteFile(UUID id) {
        File file = this.getFile(id);
        minioOperationsHelper.deleteObject(bucketName, file.getName());
        fileRepository.delete(file);
    }

    public File uploadFile(String name, MultipartFile uploadedFile, Boolean process) {
        String publicId = RandomStringGenerator.generateRandomString(50);
        String contentType = uploadedFile.getContentType();
        String fileName = Optional
                .ofNullable(uploadedFile.getOriginalFilename())
                .orElseThrow(() -> new FileUploadException("Invalid File Name"))
                .trim().replace(" ", "_");
        String storedFileName = String.format("%s_%s.%s",
                name.trim().toLowerCase().replace(" ", "_"),
                RandomStringGenerator.generateRandomString(15),
                fileName.split("\\.")[1]);

        minioOperationsHelper.uploadMultipartFile(bucketName, storedFileName, uploadedFile.getSize(), contentType, uploadedFile);

        FileStatus fileStatus = process ? FileStatus.SENT_FOR_PROCESSING : FileStatus.UPLOADED;
        File file = File.builder()
                .name(name)
                .bucketName(bucketName)
                .publicId(publicId)
                .fileName(storedFileName)
                .contentType(contentType)
                .fileStatus(fileStatus)
                .size(uploadedFile.getSize())
                .isAvailable(false).build();
        File savedFile = fileRepository.save(file);

        if (process) fileProcessingQueueMessenger.sendRequestForFileResolutionProcessing(savedFile);

        return savedFile;
    }

    public File setFileAvailability(UUID id, Boolean isAvailable) {
        File file = this.getFile(id);
        file.setIsAvailable(isAvailable);
        return fileRepository.save(file);
    }
}
