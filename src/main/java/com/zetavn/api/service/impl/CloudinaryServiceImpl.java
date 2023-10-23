package com.zetavn.api.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FileUploadResponse;
import com.zetavn.api.service.CloudinaryService;
import com.zetavn.api.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

@Service @Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    Cloudinary cloudinary;

    @Override
    public FileUploadResponse upload(MultipartFile file,String folder,String resourceType){
       try {
           Map<?,?> map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                   "folder", folder,
                   "resource_type", resourceType
           ));
           FileUploadResponse fileUploadResponse = new FileUploadResponse();
           fileUploadResponse.setId(String.valueOf(map.get("public_id")));
           fileUploadResponse.setUrl(String.valueOf(map.get("url")));
           fileUploadResponse.setType(String.valueOf(map.get("resource_type")));
           fileUploadResponse.setWidth(Integer.valueOf(map.get("width")+""));
           fileUploadResponse.setHeight(Integer.valueOf(map.get("height")+""));

           return fileUploadResponse;
       }catch (Exception e) {
           log.error(e.getMessage());
           return null;
       }
    }

    @Override
    public FileUploadResponse uploadBase64(String base64, String folder, String resourceType) {
        try {
            Map<?,?> map = cloudinary.uploader().upload(base64, ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", resourceType
            ));
            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setId(String.valueOf(map.get("public_id")));
            fileUploadResponse.setUrl(String.valueOf(map.get("url")));
            fileUploadResponse.setType(String.valueOf(map.get("resource_type")));
            fileUploadResponse.setWidth(Integer.valueOf(map.get("width")+""));
            fileUploadResponse.setHeight(Integer.valueOf(map.get("height")+""));

            return fileUploadResponse;
        }catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public ApiResponse<?> delete(String id) {
        try {
            cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
            return ApiResponse.success(HttpStatus.OK,"Delete file success",null);
        }catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Delele file fail");
        }
    }
}
