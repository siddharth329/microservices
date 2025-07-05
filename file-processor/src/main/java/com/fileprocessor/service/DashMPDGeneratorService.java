package com.fileprocessor.service;

import com.fileprocessor.config.DashStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashMPDGeneratorService {

    public FFmpegBuilder generateDashStreamFFmpegBuilder(String mediaPath, String outputDir, DashStreamConfig dashStreamConfig) {

        String manifestPath = Paths.get(outputDir, "manifest.mpd").toString();

        FFmpegBuilder ffmpegBuilder = new FFmpegBuilder();
        ffmpegBuilder
                .overrideOutputFiles(true)
                .setInput(String.format("\"%s\"", mediaPath));

        ffmpegBuilder
                .addOutput(manifestPath)
                .addExtraArgs("-f", "dash")
                .addExtraArgs("-seg_duration", String.valueOf(dashStreamConfig.getSegmentDuration()))
                .addExtraArgs("-use_template", "1")
                .addExtraArgs("-use_timeline", "1")
                .addExtraArgs("-adaptation_sets", "id=0,streams=v id=1,streams=a")
                .addExtraArgs("-init_seg_name", dashStreamConfig.getInitSegmentNameWithOutputDir(outputDir))
                .addExtraArgs("-media_seg_name", dashStreamConfig.getMediaSegmentNameWithOutputDir(outputDir));

        AtomicInteger streamIndex = new AtomicInteger(0);
        for (DashStreamConfig.VideoProfile profile: dashStreamConfig.getVideoProfiles()) {
            ffmpegBuilder
                    .addExtraArgsEnd("-vcodec", "libx264")
                    .addExtraArgsEnd("-profile:v:" + streamIndex.get(), profile.getProfile())
                    .addExtraArgsEnd("-level:v:" + streamIndex.get(), "3.1")
                    .addExtraArgsEnd("-x264opts", "keyint=48:min-keyint=48:no-scenecut")
                    .addExtraArgsEnd("-map", "0:v:0")
                    .addExtraArgsEnd("-b:v:" + streamIndex.get(), String.valueOf(profile.getBitrate()))
                    .addExtraArgsEnd("-s:v:" + streamIndex.get(), profile.getWidth() + "x" + profile.getHeight());
            streamIndex.getAndIncrement();
        }

        for (DashStreamConfig.AudioProfile profile : dashStreamConfig.getAudioProfiles()) {
            ffmpegBuilder
                    // .addOutput(manifestPath)
                    // .setAudioCodec("aac")
                    .addExtraArgsEnd("-map", "0:a:0")
                    .addExtraArgsEnd("-c:a", "aac")
                    .addExtraArgsEnd("-b:a:" + streamIndex.get(), String.valueOf(profile.getBitrate()))
                    .addExtraArgsEnd("-ac", String.valueOf(profile.getChannels()))
                    .addExtraArgsEnd("-ar", String.valueOf(profile.getSampleRate()));
            streamIndex.getAndIncrement();
        }

        if (dashStreamConfig.getSingleFile()) {
            ffmpegBuilder.addExtraArgs("-single_file", "1");
        }

        log.info("FFmpeg Builder Result: {}", ffmpegBuilder.build().toString());

        return ffmpegBuilder;
    }
}
