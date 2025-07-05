package com.streaming.repository;

import com.streaming.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findByPublicId(String publicId);
}
