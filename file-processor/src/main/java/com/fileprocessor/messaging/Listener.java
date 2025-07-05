package com.fileprocessor.messaging;

import com.commons.messaging.messages.FileProcessingQueueMessage;
import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.models.FileProcessingQueue;
import com.fileprocessor.repository.FileRepository;
import com.fileprocessor.repository.FileProcessingQueueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {
    private final FileRepository fileRepository;
    private final FileProcessingQueueRepository fileProcessingQueueRepository;

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.fileprocessing.queue}")
    public void listen(FileProcessingQueueMessage fileProcessingQueueMessage) {
        System.out.println("Received message: " + fileProcessingQueueMessage);

        Boolean jobAlreadyExists = fileProcessingQueueRepository.existsByFileIdAndStatusIn(
                fileProcessingQueueMessage.getFileId(),
                List.of(FileProcessingStatus.PROCESSING, FileProcessingStatus.QUEUED));

        if (!jobAlreadyExists) {
            FileProcessingQueue fileProcessingQueue = fileProcessingQueueRepository
                    .findByFileId(fileProcessingQueueMessage.getFileId())
                    .orElseGet(() -> FileProcessingQueue.builder()
                            .fileId(fileProcessingQueueMessage.getFileId())
                            .publicId(fileProcessingQueueMessage.getPublicId())
                            .bucketName(fileProcessingQueueMessage.getBucketName())
                            .fileName(fileProcessingQueueMessage.getFileName()).build());

            fileProcessingQueue.setStatus(FileProcessingStatus.QUEUED);
            fileProcessingQueueRepository.save(fileProcessingQueue);
        }
    }
}
