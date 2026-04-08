package com.hostelcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for storing and retrieving uploaded files (images) for complaints.
 * Files are stored locally under the configured upload directory.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Store a file to disk and return its stored filename.
     *
     * @param file Multipart file from the HTTP request
     * @return Unique filename that can be used to retrieve the file
     * @throws IOException if saving fails
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Sanitize the original filename
        String originalFilename = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));

        // Generate a unique name to avoid collisions
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    /**
     * Delete a previously stored file.
     *
     * @param filename Name of the file to delete
     */
    public void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.deleteIfExists(filePath);
    }
}
