package com.fileprocessor.enums;

public enum FileProcessingStatus {
    QUEUED("QUEUED"),
    PROCESSING("PROCESSING"),
    FINISHED("FINISHED"),
    ERROR("ERROR");

    private String value;
    FileProcessingStatus(String value) {
        this.value = value;
    }
}
