package com.example.demo.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "上传文件不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isValidImageType(contentType, extension)) {
            response.put("success", false);
            response.put("message", "仅支持图片类型文件");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String base64DataUrl = "data:" + contentType + ";base64," + base64Image;

            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("fileName", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("contentType", contentType);
            response.put("base64", base64DataUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isValidImageType(String contentType, String extension) {
        boolean validContentType = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        boolean validExtension = extension != null && ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
        return validContentType || validExtension;
    }
}
