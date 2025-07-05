package com.fileprocessor.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class DashGenerationResult {
    private UUID fileId;
    private String mpdUrl;
    private List<String> generatedFiles;
    private double duration;
    private String thumbnailUrl;
    private Boolean success;
    private String message;
}
