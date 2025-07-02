package com.fileprocessor.enums;

public enum MessageType {
    FILE_PROCESSING_REQUEST("FILE_PROCESSING_REQUEST");

    private String value;
    private MessageType(String value) {
        this.value = value;
    }
}
