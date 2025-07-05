package com.fileprocessor.processor;

import com.fileprocessor.config.DashStreamConfig;
import com.fileprocessor.crud.MinioOperationsHelper;
import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.enums.FileStatus;
import com.fileprocessor.exception.ApiException;
import com.fileprocessor.repository.FileRepository;
import com.fileprocessor.response.DashGenerationResult;
import com.fileprocessor.models.File;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

    public DashGenerationResult process(File file, FileProcessingQueue fileProcessingQueue, DashStreamConfig dashStreamConfig) {
        // Generating presigned url to give the input to minio
        String mediaPath = minioOperationsHelper.generatePresignedUrl(
                file.getBucketName(),
                file.getFileName(),
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
            Long startTime = System.currentTimeMillis();
            FFmpegJob job = ffmpegExecutor.createJob(ffmpegBuilder);
            job.run();
            Long endTime = System.currentTimeMillis();
            log.info("FFMPEG Job Completed in: {}ms", (endTime - startTime));

            log.info("FFmpeg Job Completed");
            log.info("Files created are: {}", Arrays.stream(Objects.requireNonNull(new java.io.File(outputDir).listFiles())).toList().stream().map(java.io.File::getName).collect(Collectors.joining(" | ")));

            // Uploading all the files to the folder
            List<String> uploadedFiles = fileStorageService.uploadDirectoryToMinio(file.getPublicId(), outputDir);
            String mpdUrl = String.format("s3://%s/%s/manifest.mpd", "xyz", file.getPublicId());

            return new DashGenerationResult(mpdUrl, uploadedFiles, probeResult.getFormat().duration, null);

        } catch (Exception e) {
            file.setFileStatus(FileStatus.ERROR);
            fileProcessingQueue.setStatus(FileProcessingStatus.ERROR);
            fileProcessingQueue.setInstanceId(null);

            fileRepository.save(file);
            fileProcessingQueueRepository.save(fileProcessingQueue);
            throw new ApiException(e.getMessage());
        } finally {
            if (outputDir != null) {
                fileUtils.cleanupTempDirectory(outputDir);
            }
        }
    }
}