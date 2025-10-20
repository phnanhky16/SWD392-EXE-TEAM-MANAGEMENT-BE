package com.swd.exe.teammanagement.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;
    Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException;
}
