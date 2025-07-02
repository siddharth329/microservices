package com.streaming.controller;

import com.streaming.service.VideoStreamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/video")
public class VideoStreamController {
    private final VideoStreamService videoStreamService;

    @GetMapping("/{publicId}/manifest.mpd")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String publicId) {
        InputStream inputStream = videoStreamService.getManifestMPD(publicId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/dash+xml"));
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    private MediaType getContentType(String segmentPath) {
        if (segmentPath.endsWith(".m4s") || segmentPath.endsWith(".mp4")) {
            return MediaType.parseMediaType("video/mp4");
        } else if (segmentPath.endsWith(".webm")) {
            return MediaType.parseMediaType("video/webm");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    // http://127.0.0.1:8080
    // /api/v1/video
    // /4gaNq54VDV9U4rX8GLZCXJUMTDhhJPw22CajvGTJYtFizRi6TN
    // /temp/dash-8e333a91-619d-4089-9312-881fcc2b4d67
    // /init-stream5.m4s

    @GetMapping("/{publicId}/temp/{tempDirectoryPath}/{streamFileName}")
    public ResponseEntity<InputStreamResource> downloadChunk(
            @PathVariable String publicId,
            @PathVariable String tempDirectoryPath,
            @PathVariable String streamFileName,
            HttpServletRequest request) {

        // String requestURI = request.getRequestURI();
        // String segmentPath = requestURI.substring(requestURI.indexOf("/segments/") + 10);
        // Handle range requests for adaptive streaming
        // String rangeHeader = request.getHeader("Range");
        // if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
        //    return handleRangeRequest(videoId, segmentPath, rangeHeader);
        // }

        InputStream segmentStream = videoStreamService.getChunkFile(publicId, streamFileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType(streamFileName));
        headers.setCacheControl("max-age=3600"); // Cache segments for 1 hour

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(segmentStream));
    }

//    private ResponseEntity<InputStreamResource> handleRangeRequest(
//            String videoId, String segmentPath, String rangeHeader) throws IOException {
//
//        long fileSize = videoStreamingService.getSegmentSize(videoId, segmentPath);
//
//        // Parse range header (e.g., "bytes=0-1023")
//        String range = rangeHeader.substring(6);
//        String[] ranges = range.split("-");
//        long start = Long.parseLong(ranges[0]);
//        long end = ranges.length > 1 && !ranges[1].isEmpty()
//                ? Long.parseLong(ranges[1])
//                : fileSize - 1;
//
//        InputStream segmentStream = videoStreamingService.getSegmentRange(videoId, segmentPath, start, end);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(getContentType(segmentPath));
//        headers.add("Accept-Ranges", "bytes");
//        headers.add("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
//        headers.setContentLength(end - start + 1);
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .headers(headers)
//                .body(new InputStreamResource(segmentStream));
//    }
}
