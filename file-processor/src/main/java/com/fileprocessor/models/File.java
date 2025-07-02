package com.fileprocessor.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class File {
    @Id @GeneratedValue private UUID id;
    @Column(name = "name") private String name;
    @Column(name = "bucketName") private String bucketName;
    @Column(name = "fileName") private String fileName;
    @Column(name = "contentType") private String contentType;
    @Column(name = "publicId", unique = true) private String publicId;
    @Column(name = "videoDuration") private Long videoDuration;
    @Column(name = "size") private Long size;
    @Column(name = "bitrate") private Long bitrate;

    @Column(name = "createdAt") @CreationTimestamp private LocalDateTime createdAt;
    @Column(name = "updatedAt") @UpdateTimestamp private LocalDateTime updatedAt;
}