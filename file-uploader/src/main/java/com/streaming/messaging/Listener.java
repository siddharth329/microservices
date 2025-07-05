package com.streaming.messaging;

import com.commons.messaging.messages.FileProcessingResultMessage;
import com.streaming.service.FileProcessingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {
    private final FileProcessingService fileProcessingService;

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.fileuploader.queue}")
    public void listen(FileProcessingResultMessage fileProcessingResultMessage) {
        System.out.println("Received message: " + fileProcessingResultMessage);
        fileProcessingService.updateFileStatusAfterProcessing(fileProcessingResultMessage);
    }
}
