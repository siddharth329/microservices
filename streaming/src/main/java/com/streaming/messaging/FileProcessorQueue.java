package com.streaming.messaging;

import com.commons.enums.MessageType;
import com.commons.messaging.Message;
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
        Message message = Message.builder()
                .producer("streaming")
                .consumer("file-processor")
                .messageType(MessageType.FILE_PROCESSING_QUEUE_REQUEST)
                .message(file.getId()).build();
        System.out.println(message);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), message);
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
