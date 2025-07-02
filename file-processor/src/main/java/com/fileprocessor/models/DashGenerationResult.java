package com.fileprocessor.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashGenerationResult {
    private String mpdUrl;
    private List<String> generatedFiles;
    private double duration;

    @Override
    public String toString() {
        return "DashGenerationResult{" +
                "mpdUrl='" + mpdUrl + '\'' +
                ", generatedFiles=" + generatedFiles +
                ", duration=" + duration +
                '}';
    }
}
