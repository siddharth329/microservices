package com.commons.enums;

public enum MessageType {
    FILE_PROCESSING_QUEUE_REQUEST("FILE_PROCESSING_QUEUE_REQUEST");

    private String value;
    private MessageType(String value) {
        this.value = value;
    }
}
