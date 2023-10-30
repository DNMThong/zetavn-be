package com.zetavn.api.service;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    FileUploadResponse upload(MultipartFile file,String folder,String resourceType);

    FileUploadResponse uploadBase64(String base64,String folder,String resourceType);
    ApiResponse<?> delete(String id);
}
