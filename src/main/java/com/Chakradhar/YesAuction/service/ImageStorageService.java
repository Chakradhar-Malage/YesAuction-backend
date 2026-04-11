package com.Chakradhar.YesAuction.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path uploadDir;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            System.out.println("Image upload directory created at: " + this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // Generate unique filename
        String originalExtension = getFileExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + originalExtension;

        Path filePath = uploadDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), filePath);
            return filename;   // We only store filename in DB
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot) : ".jpg";
    }
}