package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.common.ResultCode;
import com.loveapp.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {
    
    @Value("${upload.path:./uploads/}")
    private String uploadPath;
    
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_TYPES = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请选择文件");
        }
        
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ResultCode.FILE_TOO_LARGE);
        }
        
        String originalName = file.getOriginalFilename();
        String ext = originalName != null ? 
                originalName.substring(originalName.lastIndexOf(".")).toLowerCase() : "";
        
        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equals(ext)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }
        
        // 生成文件名
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String relativePath = dateFolder + "/" + fileName;
        
        // 保存文件
        File destDir = new File(uploadPath + dateFolder).getAbsoluteFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        File destFile = new File(uploadPath + relativePath).getAbsoluteFile();
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("url", "/uploads/" + relativePath);
        result.put("name", originalName);
        
        return Result.success(result);
    }
}
