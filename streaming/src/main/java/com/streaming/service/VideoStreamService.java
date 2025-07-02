package com.streaming.service;

import com.streaming.crud.MinioOperationsHelper;
import com.streaming.models.File;
import com.streaming.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.InputStream;

@CrossOrigin(origins = "*")
@Service
@RequiredArgsConstructor
public class VideoStreamService {
    private final FileRepository fileRepository;
    private final MinioOperationsHelper minioOperationsHelper;
    private final FileService fileService;

    public InputStream getManifestMPD(String publicId) {
        File file = fileService.getFileByPublicId(publicId);
        return minioOperationsHelper.getObject("original", String.format("%s/manifest.mpd", file.getPublicId()));
    }

    public InputStream getChunkFile(String publicId, String fileName) {
        File file = fileService.getFileByPublicId(publicId);
        return minioOperationsHelper.getObject(
                "original",
                String.format("%s/%s", file.getPublicId(),  fileName));
    }
}
