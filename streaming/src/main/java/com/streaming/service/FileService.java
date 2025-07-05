package com.streaming.service;

import com.streaming.exception.ResourceNotFoundException;
import com.streaming.models.File;
import com.streaming.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public File getFileByPublicId(String publicId) {
        return fileRepository
                .findByPublicIdAndIsAvailable(publicId, true)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
    }
}
