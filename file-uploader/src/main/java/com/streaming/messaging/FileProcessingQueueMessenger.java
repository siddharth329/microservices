package com.streaming.messaging;

import com.commons.messaging.enums.MessageType;
import com.commons.messaging.messages.FileProcessingQueueMessage;
import com.streaming.config.RabbitMQConfig;
import com.streaming.models.File;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileProcessingQueueMessenger {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    public void sendRequestForFileResolutionProcessing(File file) {
        FileProcessingQueueMessage fileProcessingQueueMessage = FileProcessingQueueMessage.builder()
                .producer("streaming")
                .consumer("file-processor")
                .messageType(MessageType.FILE_PROCESSING_QUEUE_REQUEST)
                .fileId(file.getId())
                .publicId(file.getPublicId())
                .bucketName(file.getBucketName())
                .fileName(file.getFileName()).build();
        System.out.println(fileProcessingQueueMessage);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), fileProcessingQueueMessage);
    }
}
