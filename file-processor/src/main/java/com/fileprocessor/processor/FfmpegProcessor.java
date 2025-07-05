package com.fileprocessor.processor;

import com.fileprocessor.config.DashStreamConfig;
import com.fileprocessor.crud.MinioOperationsHelper;
import com.fileprocessor.repository.FileRepository;
import com.fileprocessor.response.DashGenerationResult;
import com.fileprocessor.models.FileProcessingQueue;
import com.fileprocessor.repository.FileProcessingQueueRepository;
import com.fileprocessor.service.DashMPDGeneratorService;
import com.fileprocessor.service.FileStorageService;
import com.fileprocessor.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class FfmpegProcessor {
    private final FFprobe ffprobe;
    private final FFmpegExecutor ffmpegExecutor;
    private final MinioOperationsHelper minioOperationsHelper;
    private final DashMPDGeneratorService dashMPDGeneratorService;
    private final FileStorageService fileStorageService;
    private final FileUtils fileUtils;
    private final FileRepository fileRepository;
    private final FileProcessingQueueRepository fileProcessingQueueRepository;

    public DashGenerationResult process(FileProcessingQueue fileProcessingQueue, DashStreamConfig dashStreamConfig) {
        // Generating presigned url to give the input to minio
        String mediaPath = minioOperationsHelper.generatePresignedUrl(
                fileProcessingQueue.getBucketName(),
                fileProcessingQueue.getFileName(),
                360000);

        String outputDir = null;
        try {
            outputDir = fileUtils.createTempDirectory();
            log.info("Creating temporary output directory {}", outputDir);

            FFmpegProbeResult probeResult = ffprobe.probe(mediaPath);
            log.info("FFMPEG Probe result: {}", probeResult);

            // Generating dash stream command using the FFmpeg Builder
            FFmpegBuilder ffmpegBuilder = dashMPDGeneratorService.generateDashStreamFFmpegBuilder(
                    mediaPath,
                    outputDir,
                    dashStreamConfig);

            // Executing Dash Stream conversion
            FFmpegJob job = ffmpegExecutor.createJob(ffmpegBuilder);
            log.info("FFmpeg Job Started");

            Long startTime = System.currentTimeMillis();
            job.run();
            Long endTime = System.currentTimeMillis();
            log.info("FFMPEG Job Completed in: {}ms", (endTime - startTime));

            // Uploading all the files to the folder
            List<String> uploadedFiles = fileStorageService.uploadDirectoryToMinio(fileProcessingQueue.getPublicId(), outputDir);
            String mpdUrl = String.format("s3://%s/%s/manifest.mpd", "original", fileProcessingQueue.getPublicId());

            return DashGenerationResult.builder()
                    .fileId(fileProcessingQueue.getFileId())
                    .mpdUrl(mpdUrl)
                    .generatedFiles(uploadedFiles)
                    .duration(probeResult.getFormat().duration)
                    .success(true).build();

        } catch (Exception e) {
            return DashGenerationResult.builder()
                    .fileId(fileProcessingQueue.getFileId())
                    .message(e.getMessage()).build();
        } finally {
            if (outputDir != null) {
                fileUtils.cleanupTempDirectory(outputDir);
            }
        }
    }
}