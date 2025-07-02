package com.streaming.controller;

import com.streaming.models.File;
import com.streaming.response.ApiResponse;
import com.streaming.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/file")
public class FileController {
    private final FileService fileService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> readAll(Pageable pageable) {
        Page<File> filePage = fileService.getFiles(pageable);
        return ResponseEntity.ok(new ApiResponse("Success", filePage));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<ApiResponse> readOne(@PathVariable UUID id) {
        File file = fileService.getFile(id);
        return ResponseEntity.ok(new ApiResponse("Success", file));
    }

    @DeleteMapping("/:id")
    public void delete(@RequestParam UUID id) {
        fileService.deleteFile(id);
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile uploadedFile) {
        File file = fileService.uploadFile(name, uploadedFile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Uploaded Successfully", file));
    }
}
