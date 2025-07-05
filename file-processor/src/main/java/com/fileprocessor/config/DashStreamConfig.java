package com.fileprocessor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Configuration
@Getter
public class DashStreamConfig {
    private final List<VideoProfile> videoProfiles;
    private final List<AudioProfile> audioProfiles;
    private final String initSegmentName = "init-stream$RepresentationID$.m4s";
    private final String mediaSegmentName = "chunk-stream$RepresentationID$-$Number%05d$.m4s";
    private final Boolean singleFile = false;

    @Value("${processor.ffmpeg.segment.size:10}")
    private Integer segmentDuration = 10;

    public DashStreamConfig() {
        this.videoProfiles = Arrays.asList(
                new VideoProfile("240p", 240, 426, 400000L, "baseline", 30.0),
                new VideoProfile("360p", 360, 640, 800000L, "baseline", 30.0),
                new VideoProfile("480p", 480, 854, 1200000L, "main", 30.0),
                new VideoProfile("720p", 720, 1280, 2500000L, "main", 30.0),
                new VideoProfile("1080p", 1080, 1920, 5000000L, "high", 30.0)
        );

        this.audioProfiles = Arrays.asList(
                new AudioProfile("64k", 64000L, 1, 48000),
                new AudioProfile("128k", 128000L, 2, 48000)
        );
    }

    public DashStreamConfig(List<VideoProfile> videoProfiles, List<AudioProfile> audioProfiles) {
        this.videoProfiles = videoProfiles;
        this.audioProfiles = audioProfiles;
    }

    public String getInitSegmentNameWithOutputDir(String outputDir) {
        return Paths.get(outputDir, initSegmentName).toString();
    }
    public String getMediaSegmentNameWithOutputDir(String outputDir) {
        return Paths.get(outputDir, mediaSegmentName).toString();
    }

    @Getter
    @AllArgsConstructor
    public static class VideoProfile {
        private String name;
        private Integer height;
        private Integer width;
        private Long bitrate;
        private String profile;
        private Double frameRate;
    }

    @Getter
    @AllArgsConstructor
    public static class AudioProfile {
        private String name;
        private Long bitrate;
        private Integer channels;
        private Integer sampleRate;
    }
}
