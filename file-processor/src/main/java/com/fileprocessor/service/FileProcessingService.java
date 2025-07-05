package com.fileprocessor.service;

import com.fileprocessor.config.DashStreamConfig;
import com.fileprocessor.enums.FileStatus;
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
    private final FileRepository fileRepository;
    private final FfmpegProcessor ffmpegProcessor;
    private final FileProcessingQueueRepository fileProcessingQueueRepository;
    @Value("${instance.id}") private String instanceId;

    public void processFile(FileProcessingQueue fileProcessingQueue) {
        File file = fileRepository
                .findById(fileProcessingQueue.getFile().getId())
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setFileStatus(FileStatus.PROCESSING);
        fileRepository.save(file);

        fileProcessingQueue.setInstanceId(instanceId);
        FileProcessingQueue fpq = fileProcessingQueueRepository.save(fileProcessingQueue);

        DashGenerationResult dashGenerationResult = ffmpegProcessor.process(
                file,
                fpq,
                new DashStreamConfig());
        log.info("Dash generation result: {}", dashGenerationResult);

        file.setVideoDuration(dashGenerationResult.getDuration());
        fileRepository.save(file);
        fileProcessingQueueRepository.deleteById(fpq.getId());
    }
}
