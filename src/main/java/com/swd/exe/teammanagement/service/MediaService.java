package com.swd.exe.teammanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String uploadMedia(MultipartFile file);
}
