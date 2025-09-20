package com.beibeijava.controller;

import com.beibeijava.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "文件上传相关的接口")
@Slf4j
public class FileUploadController {

    @Value("${beibei.upload.path:uploads}")
    private String uploadPath;

    @Value("${beibei.upload.avatar-path:avatars}")
    private String avatarPath;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传头像", description = "上传用户头像文件")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {

        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg")
                    && !contentType.startsWith("image/png")
                    && !contentType.startsWith("image/gif"))) {
                return Result.error("只支持JPG、PNG、GIF格式的图片");
            }

            // 检查文件大小 (2MB)
            long maxSize = 2 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过2MB");
            }

            // 创建上传目录 - 使用绝对路径
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            // 确保使用项目根目录的绝对路径
            String projectDir = System.getProperty("user.dir");
            Path uploadDir = Paths.get(projectDir, uploadPath, avatarPath, dateDir);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir.toString());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(fileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 构建访问URL
            String fileUrl = "/" + avatarPath + "/" + dateDir + "/" + fileName;

            log.info("头像上传成功: {}", filePath.toString());

            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success("上传成功", result);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/avatar")
    @Operation(summary = "删除头像", description = "删除指定的头像文件")
    public Result<String> deleteAvatar(@RequestParam String url) {
        try {
            if (url == null || url.trim().isEmpty()) {
                return Result.error("文件URL不能为空");
            }

            // 移除开头的斜杠
            String relativePath = url.startsWith("/") ? url.substring(1) : url;

            // 使用项目根目录的绝对路径
            String projectDir = System.getProperty("user.dir");
            Path filePath = Paths.get(projectDir, uploadPath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("头像删除成功: {}", filePath.toString());
                return Result.success("删除成功");
            } else {
                return Result.error("文件不存在");
            }

        } catch (IOException e) {
            log.error("头像删除失败", e);
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }
}