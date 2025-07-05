package com.fileprocessor.repository;

import com.fileprocessor.enums.FileProcessingStatus;
import com.fileprocessor.models.FileProcessingQueue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface FileProcessingQueueRepository extends JpaRepository<FileProcessingQueue, UUID> {
    Page<FileProcessingQueue> findAllByStatusIn(Collection<FileProcessingStatus> statuses, Pageable pageable);
    Integer countAllByInstanceIdAndStatusIn(String instanceId, Collection<FileProcessingStatus> statuses);
    Boolean existsByFile_IdAndStatusIn(UUID fileId, Collection<FileProcessingStatus> statuses);
    Optional<FileProcessingQueue> findByFile_Id(UUID fileId);
}
