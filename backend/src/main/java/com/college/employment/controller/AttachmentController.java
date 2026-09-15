/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.college.employment.controller;

import com.college.employment.common.api.Result;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.Attachment;
import com.college.employment.infrastructure.mapper.AttachmentMapper;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件管理接口
 * 安全校验：限制文件格式（jpg/png/pdf）、大小（最大10MB）
 */
@Slf4j
@RestController
@RequestMapping("/api/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentMapper attachmentMapper;

    /** 允许的文件格式 */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "pdf"
    ));

    /** 允许的 MIME 类型 */
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp",
            "application/pdf"
    ));

    /** 最大文件大小: 10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
        }
    }

    /**
     * 上传附件
     */
    @PostMapping("/upload")
    public Result<Attachment> upload(
            Authentication authentication,
            @RequestParam Long recordId,
            @RequestParam("file") MultipartFile file) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        // 1. 安全校验 - 文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.fail("文件大小不能超过 10MB，当前文件大小: " + (file.getSize() / 1024 / 1024) + "MB");
        }

        // 2. 安全校验 - 文件格式（扩展名）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.fail("文件名不能为空");
        }
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return Result.fail("不支持的文件格式: ." + extension + "，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // 3. 安全校验 - MIME 类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return Result.fail("不支持的文件类型: " + contentType + "，仅支持图片和PDF文件");
        }

        try {
            // 4. 生成唯一文件名，防止路径遍历攻击
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String safeFilename = uuid + "." + extension;
            Path relativePath = Paths.get(dateDir, safeFilename);
            Path targetPath = Paths.get(uploadDir, dateDir).toAbsolutePath().normalize();
            Files.createDirectories(targetPath);
            Path filePath = targetPath.resolve(safeFilename);

            // 5. 保存文件（使用流式写入 + 绝对路径，避免 Windows + Tomcat 下 transferTo 的相对路径解析陷阱）
            try (java.io.InputStream in = file.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 6. 记录到数据库
            Attachment attachment = new Attachment();
            attachment.setRecordId(recordId);
            attachment.setFileName(originalFilename);
            attachment.setFilePath(relativePath.toString().replace("\\", "/"));
            attachment.setFileSize(file.getSize());
            attachment.setFileType(contentType);
            attachment.setCreateTime(LocalDateTime.now());
            attachmentMapper.insert(attachment);

            log.info("用户 {} 上传了附件: {} ({} bytes)", userDetails.getUsername(), originalFilename, file.getSize());
            return Result.ok("上传成功", attachment);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载/预览附件
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 防止路径遍历
            Path filePath = Paths.get(uploadDir).resolve(attachment.getFilePath()).normalize();
            if (!filePath.startsWith(Paths.get(uploadDir).normalize())) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 图片类型支持浏览器预览，PDF 触发下载
            String disposition = attachment.getFileType() != null &&
                    attachment.getFileType().startsWith("image/") ?
                    "inline" : "attachment";

            String encodedFilename = URLEncoder.encode(attachment.getFileName(), "UTF-8")
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            attachment.getFileType() != null ? attachment.getFileType() : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (IOException e) {
            log.error("文件下载失败: id={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取某条就业记录的附件列表
     */
    @GetMapping("/list/{recordId}")
    public Result<List<Attachment>> list(@PathVariable Long recordId) {
        List<Attachment> attachments = attachmentMapper.findByRecordId(recordId);
        return Result.ok(attachments);
    }

    /**
     * 删除附件
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            return Result.fail("附件不存在");
        }
        // 删除物理文件
        try {
            Path filePath = Paths.get(uploadDir).resolve(attachment.getFilePath()).normalize();
            if (filePath.startsWith(Paths.get(uploadDir).normalize())) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("删除物理文件失败: {}", e.getMessage());
        }
        attachmentMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
