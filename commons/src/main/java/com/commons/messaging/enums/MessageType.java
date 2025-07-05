package com.commons.messaging.enums;

public enum MessageType {
    FILE_PROCESSING_QUEUE_REQUEST("FILE_PROCESSING_QUEUE_REQUEST"),
    FILE_PROCESSING_RESULT_RESPONSE("FILE_PROCESSING_RESULT_RESPONSE");

    private String value;
    MessageType(String value) {
        this.value = value;
    }
}
