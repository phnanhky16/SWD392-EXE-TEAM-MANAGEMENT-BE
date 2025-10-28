package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.config.R2Properties;
import com.swd.exe.teammanagement.entity.MediaFile;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.MediaFileRepository;
import com.swd.exe.teammanagement.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/webp",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/webp", "webp",
            "image/png", "png",
            "image/jpeg", "jpeg",
            "image/jpg", "jpg"
    );

    private final S3Client s3Client;
    private final R2Properties r2Properties;
    private final MediaFileRepository mediaFileRepository;

    @Override
    public String uploadMedia(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        UUID imageId = UUID.randomUUID();
        String extension = resolveExtension(file, contentType);
        String objectKey = String.format("images/%s.%s", imageId, extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException | SdkException exception) {
            log.error("Failed to upload media to R2", exception);
            throw new AppException(ErrorCode.MEDIA_UPLOAD_FAILED);
        }

        MediaFile mediaFile = MediaFile.builder()
                .id(imageId)
                .bucketKey(objectKey)
                .contentType(contentType)
                .size(file.getSize())
                .createdAt(LocalDateTime.now())
                .build();
        mediaFileRepository.save(mediaFile);

        return imageId.toString();
    }

    private String resolveExtension(MultipartFile file, String contentType) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }
        return EXTENSION_BY_CONTENT_TYPE.getOrDefault(contentType, "webp");
    }
}
