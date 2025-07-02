package com.fileprocessor.messaging;

import com.commons.messaging.Message;
import com.fileprocessor.config.DashStreamConfig;
import com.fileprocessor.crud.MinioOperationsHelper;
import com.fileprocessor.models.DashGenerationResult;
import com.fileprocessor.models.File;
import com.fileprocessor.processor.FfmpegProcessor;
import com.fileprocessor.repository.FileRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {
    private final FileRepository fileRepository;
    private final FfmpegProcessor  ffmpegProcessor;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(Message message) {

        System.out.println("Received message: " + message);

        // Getting the UUID from the message
        UUID fileId = UUID.fromString(message.getMessage().toString());

        // Getting the File from the database via the UUID
        File file = fileRepository
                .findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));


        // Passing that for ffmpeg and processing it
        // ffmpegProcessor.processVideoWithResolution(fileUrl, 640, 480);
        DashGenerationResult dashGenerationResult = ffmpegProcessor.process(file, new DashStreamConfig());
        log.info("Dash generation result: " + dashGenerationResult);

    }
}
