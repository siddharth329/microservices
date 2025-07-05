package com.streaming.service;

import com.streaming.enums.FileStatus;
import com.streaming.exception.ApiException;
import com.streaming.messaging.FileProcessorQueue;
import com.streaming.models.File;
import com.streaming.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final FileService fileService;
    private final FileProcessorQueue fileProcessorQueue;
    private final FileRepository fileRepository;

    public File sendFileForProcessing(UUID fileId, Boolean override){
        File file = fileService.getFile(fileId);

        if (file.getFileStatus().equals(FileStatus.PROCESSING)) throw new ApiException("File is already processing");
        if (file.getFileStatus().equals(FileStatus.PROCESSED) && !override) throw new ApiException("File is already processed");

        fileProcessorQueue.sendRequestForFileResolutionProcessing(file);

        file.setFileStatus(FileStatus.SENT_FOR_PROCESSING);
        file.setIsAvailable(false);
        return fileRepository.save(file);
    }
}
