package com.fileprocessor.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegConfig {
    @Value("${processor.ffmpeg.ffmpeg.path}")
    private String ffmpegPath;

    @Value("${processor.ffmpeg.ffprobe.path}")
    private String ffprobePath;

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg(ffmpegPath);
    }

    @Bean
    public FFprobe ffprobe() throws IOException {
        return new FFprobe(ffprobePath);
    }

    @Bean
    FFmpegExecutor ffmpegExecutor() throws IOException {
        return new FFmpegExecutor(ffmpeg(),  ffprobe());
    }
}
