package com.streaming.messaging;

import com.commons.messaging.enums.MessageType;
import com.commons.messaging.messages.FileProcessingMessage;
import com.streaming.config.RabbitMQConfig;
import com.streaming.models.File;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileProcessorQueue {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    @Value("${spring.rabbitmq.queue}") private String queueName;

    public void sendRequestForFileResolutionProcessing(File file) {
        FileProcessingMessage fileProcessingMessage = FileProcessingMessage.builder()
                .producer("streaming")
                .consumer("file-processor")
                .messageType(MessageType.FILE_PROCESSING_QUEUE_REQUEST)
                .fileId(file.getId()).build();
        System.out.println(fileProcessingMessage);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), fileProcessingMessage);
        rabbitTemplate.convertAndSend(queueName, fileProcessingMessage);
    }
}
