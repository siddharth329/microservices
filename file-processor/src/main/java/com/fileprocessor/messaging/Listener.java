package com.fileprocessor.messaging;

import com.commons.messaging.messages.FileProcessingMessage;
import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.enums.FileStatus;
import com.fileprocessor.exception.ApiException;
import com.fileprocessor.models.File;
import com.fileprocessor.models.FileProcessingQueue;
import com.fileprocessor.repository.FileRepository;
import com.fileprocessor.repository.FileProcessingQueueRepository;
import com.fileprocessor.service.FileProcessingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {
    private final FileRepository fileRepository;
    private final FileProcessingQueueRepository fileProcessingQueueRepository;

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(FileProcessingMessage fileProcessingMessage) {
        System.out.println("Received message: " + fileProcessingMessage);

        File file = fileRepository
                .findById(fileProcessingMessage.getFileId())
                .orElseThrow(() -> new ApiException("File not found"));
        file.setIsAvailable(false);
        file.setFileStatus(FileStatus.QUEUED);

        Boolean jobAlreadyExists = fileProcessingQueueRepository.existsByFile_IdAndStatusIn(
                file.getId(),
                List.of(FileProcessingStatus.PROCESSING, FileProcessingStatus.QUEUED));
        if (!jobAlreadyExists) {
            FileProcessingQueue fileProcessingQueue = fileProcessingQueueRepository
                    .findByFile_Id(file.getId())
                    .orElseGet(() -> FileProcessingQueue.builder().file(file).build());
            fileProcessingQueue.setStatus(FileProcessingStatus.QUEUED);

            fileRepository.save(file);
            fileProcessingQueueRepository.save(fileProcessingQueue);
        }
    }
}
