package com.streaming.service;

import com.commons.messaging.messages.FileProcessingResultMessage;
import com.streaming.enums.FileStatus;
import com.streaming.exception.ApiException;
import com.streaming.messaging.FileProcessingQueueMessenger;
import com.streaming.models.File;
import com.streaming.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final FileService fileService;
    private final FileProcessingQueueMessenger fileProcessingQueueMessenger;
    private final FileRepository fileRepository;

    public File sendFileForProcessing(UUID fileId, Boolean override){
        File file = fileService.getFile(fileId);

        if (file.getFileStatus().equals(FileStatus.PROCESSING)) throw new ApiException("File is already processing");
        if (file.getFileStatus().equals(FileStatus.PROCESSED) && !override) throw new ApiException("File is already processed");

        fileProcessingQueueMessenger.sendRequestForFileResolutionProcessing(file);

        file.setFileStatus(FileStatus.SENT_FOR_PROCESSING);
        file.setIsAvailable(false);
        return fileRepository.save(file);
    }

    public void updateFileStatusAfterProcessing(FileProcessingResultMessage fileProcessingResultMessage) {
        assert fileProcessingResultMessage.getFileId() != null;
        File file = fileService.getFile(fileProcessingResultMessage.getFileId());

        if (fileProcessingResultMessage.getSuccess()) {
            file.setFileStatus(FileStatus.PROCESSED);
            file.setVideoDuration(fileProcessingResultMessage.getDuration());
            file.setThumbnail(fileProcessingResultMessage.getThumbnailUrl());
        } else {
            file.setFileStatus(FileStatus.ERROR);
        }

        fileRepository.save(file);
    }
}
