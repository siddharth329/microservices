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
    @OneToOne @JoinColumn(name = "fileId", unique = true) private File file;
    @Column(name = "status") @Enumerated(EnumType.STRING) private FileProcessingStatus  status;
    @Column(name = "instanceId") private String instanceId;
    @Column(name = "createdAt") @CreationTimestamp  private LocalDateTime createdAt;
    @Column(name = "updatedAt") @UpdateTimestamp private LocalDateTime updatedAt;
}
