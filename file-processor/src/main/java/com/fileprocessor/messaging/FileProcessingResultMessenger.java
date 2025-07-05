package com.fileprocessor.messaging;

import com.commons.messaging.enums.MessageType;
import com.commons.messaging.messages.FileProcessingResultMessage;
import com.fileprocessor.config.RabbitMQConfig;
import com.fileprocessor.response.DashGenerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileProcessingResultMessenger {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    public void sendRequestForFileResolutionProcessing(DashGenerationResult dashGenerationResult) {
        FileProcessingResultMessage fileProcessingResultMessage = FileProcessingResultMessage.builder()
                .producer("file-processor")
                .consumer("file-uploader")
                .messageType(MessageType.FILE_PROCESSING_RESULT_RESPONSE)
                .success(dashGenerationResult.getSuccess())
                .message(dashGenerationResult.getMessage())
                .thumbnailUrl(dashGenerationResult.getThumbnailUrl())
                .duration(dashGenerationResult.getDuration()).build();
        System.out.println(fileProcessingResultMessage);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), fileProcessingResultMessage);
    }
}
