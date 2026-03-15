package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageUploadController {

    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    };

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "文件不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            response.put("success", false);
            response.put("message", "不支持的文件类型，仅支持 jpg、png、gif、bmp、webp 图片格式");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            byte[] fileContent = file.getBytes();
            String base64Encoded = Base64.getEncoder().encodeToString(fileContent);
            
            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", contentType);
            response.put("base64", base64Encoded);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
