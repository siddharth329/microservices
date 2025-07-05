package com.fileprocessor.service;

import com.fileprocessor.config.DashStreamConfig;
import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.enums.FileStatus;
import com.fileprocessor.messaging.FileProcessingResultMessenger;
import com.fileprocessor.repository.FileRepository;
import com.fileprocessor.repository.FileProcessingQueueRepository;
import com.fileprocessor.response.DashGenerationResult;
import com.fileprocessor.models.File;
import com.fileprocessor.models.FileProcessingQueue;
import com.fileprocessor.processor.FfmpegProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {
    private final FfmpegProcessor ffmpegProcessor;
    private final FileProcessingQueueRepository fileProcessingQueueRepository;
    private final FileProcessingResultMessenger fileProcessingResultMessenger;
    @Value("${instance.id}") private String instanceId;

    public void processFile(FileProcessingQueue fileProcessingQueue) {
        fileProcessingQueue.setInstanceId(instanceId);
        fileProcessingQueue.setStatus(FileProcessingStatus.PROCESSING);
        FileProcessingQueue fpq = fileProcessingQueueRepository.save(fileProcessingQueue);
        System.out.println(fpq);

        DashGenerationResult dashGenerationResult = ffmpegProcessor.process(fpq, new DashStreamConfig());
        System.out.println(dashGenerationResult);
        fileProcessingQueueRepository.deleteById(fpq.getId());
        fileProcessingResultMessenger.sendRequestForFileResolutionProcessing(dashGenerationResult);
    }
}
