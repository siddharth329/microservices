package com.fileprocessor.models;

import com.fileprocessor.enums.FileProcessingStatus;
import jakarta.persistence.*;
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
@Table(name = "`FileProcessingQueue`")
public class FileProcessingQueue {
    @Id @GeneratedValue private UUID id;

    // File Metadata
    @Column(name = "fileId") private UUID fileId;
    @Column(name = "publicId") private String publicId;
    @Column(name = "bucketName") private String bucketName;
    @Column(name = "fileName") private String fileName;

    // Queue & Processor Metadata
    @Column(name = "status") @Enumerated(EnumType.STRING) private FileProcessingStatus  status;
    @Column(name = "instanceId") private String instanceId;
    @Column(name = "createdAt") @CreationTimestamp  private LocalDateTime createdAt;
    @Column(name = "updatedAt") @UpdateTimestamp private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "FileProcessingQueue{" +
                "id=" + id +
                ", fileId=" + fileId +
                ", publicId='" + publicId + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status=" + status +
                ", instanceId='" + instanceId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
