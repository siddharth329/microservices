package com.commons.messaging;

import com.commons.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private String producer;
    private String consumer;
    private MessageType messageType;
    private Object message;
}
