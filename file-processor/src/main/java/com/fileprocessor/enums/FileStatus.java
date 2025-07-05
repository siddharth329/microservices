package com.fileprocessor.enums;

public enum FileStatus {
    SENT_FOR_PROCESSING("SENT_FOR_PROCESSING"),
    QUEUED("QUEUED"),
    PROCESSING("PROCESSING"),
    UPLOADED("UPLOADED"),
    PROCESSED("PROCESSED"),
    ERROR("ERROR");

    public String value;
    FileStatus(String value) {
        this.value = value;
    }
}
