package com.commons.messaging.messages;

import com.commons.messaging.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileProcessingQueueMessage {
    private String producer;
    private String consumer;
    private MessageType messageType;

    private UUID fileId;
    private String publicId;
    private String bucketName;
    private String fileName;
}
