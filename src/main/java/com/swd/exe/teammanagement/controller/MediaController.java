package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponseString;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponseString> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            String imageId = mediaService.uploadMedia(file);
            return ResponseEntity.ok(ApiResponseString.success(imageId));
        } catch (AppException exception) {
            int status = exception.getErrorCode().getHttpStatusCode().value();
            log.warn("Business error while uploading media", exception);
            return ResponseEntity.status(status)
                    .body(ApiResponseString.error(status, exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            log.warn("Invalid media upload request: {}", exception.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseString.error(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        } catch (Exception exception) {
            log.error("Unexpected error while uploading media", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseString.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ErrorCode.MEDIA_UPLOAD_FAILED.getMessage()));
        }
    }
}
