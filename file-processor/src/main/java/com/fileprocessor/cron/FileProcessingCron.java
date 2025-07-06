package com.fileprocessor.cron;

import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.repository.FileProcessingQueueRepository;
import com.fileprocessor.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessingCron {
    private final FileProcessingQueueRepository fileProcessingQueueRepository;
    private final FileProcessingService fileProcessingService;

    private static final List<FileProcessingStatus> currentExecutionStatus = List.of(FileProcessingStatus.PROCESSING);
    private static final List<FileProcessingStatus> executionStatusToPick = List.of(FileProcessingStatus.QUEUED);

    @Value("${processor.ffmpeg.parallel.job}") private Integer numberOfJobsAllowed;
    @Value("${instance.id}") private String instanceId;

    @Scheduled(cron = "${cron.processor.timing.function}")
    public void fileProcessingJob() {
        Integer currentRunningJobs = fileProcessingQueueRepository.countAllByInstanceIdAndStatusIn(instanceId, currentExecutionStatus);
        int availableJobCount = numberOfJobsAllowed - currentRunningJobs;

        log.info("Number of jobs allowed: {}", numberOfJobsAllowed);
        log.info("Cron executed with current jobs: {}",  currentRunningJobs);
        log.info("Cron executed with available jobs: {}",  availableJobCount);

        if (availableJobCount > 0) {
            Pageable pageable = PageRequest.of(
                    0,
                    availableJobCount,
                    Sort.by("createdAt").ascending()); // FIFO

            fileProcessingQueueRepository
                    .findAllByStatusIn(executionStatusToPick, pageable)
                    .getContent()
                    .parallelStream()
                    .forEach(fileProcessingService::processFile);
        }
    }
}
