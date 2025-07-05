package com.streaming.controller;

import com.streaming.models.File;
import com.streaming.response.ApiResponse;
import com.streaming.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/processing")
public class FileProcessingController {
    private final FileProcessingService fileProcessingService;

    @PostMapping("/{fileId}")
    public ResponseEntity<ApiResponse> sendFileForProcessing(
            @PathVariable UUID fileId,
            @RequestParam Boolean override) {
        File file = fileProcessingService.sendFileForProcessing(fileId, override);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Sent File for Processing ", file));
    }
}
