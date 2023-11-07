package com.zetavn.api.controller;

import com.cloudinary.utils.ObjectUtils;
import com.zetavn.api.payload.request.DeleteFileRequest;
import com.zetavn.api.payload.request.UploadImageBase64Response;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FileUploadResponse;
import com.zetavn.api.payload.response.UploadVideoBase64Response;
import com.zetavn.api.service.CloudinaryService;
import com.zetavn.api.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v0/upload")
public class FileUploadController {
    @Autowired
    CloudinaryService cloudinaryService;

    @PostMapping("")
    public ApiResponse<?> uploadFile(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> fileUploadResponseList = new ArrayList<>();
            for(MultipartFile file: files) {
                if(!FileUploadUtil.isImage(file)&&!FileUploadUtil.isVideo(file))
                    return ApiResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Unsupported media type!");
            }
            for(MultipartFile file: files) {
                String folder = "";
                String resourceType = "";
                if(FileUploadUtil.isImage(file)) {
                    folder = "images/";
                    resourceType = "image";
                }else if(FileUploadUtil.isVideo(file)) {
                    folder = "videos/";
                    resourceType = "video";
                }
                FileUploadResponse fileUploadResponse = cloudinaryService.upload(file,folder,resourceType);
                if(fileUploadResponse==null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
                }
                fileUploadResponseList.add(fileUploadResponse);
            }
            return ApiResponse.success(HttpStatus.CREATED,"Upload success",fileUploadResponseList);
        }catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
        }
    }

    @PostMapping("/images")
    public ApiResponse<?> uploadImageBase64(@RequestBody UploadImageBase64Response imagesBase64) {
        try {
            List<FileUploadResponse> fileUploadResponseList = new ArrayList<>();
            for(String base64: imagesBase64.getImages()) {
                FileUploadResponse fileUploadResponse = cloudinaryService.uploadBase64(base64,"images/","image");
                if(fileUploadResponse==null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
                }
                fileUploadResponseList.add(fileUploadResponse);
            }
            return ApiResponse.success(HttpStatus.CREATED,"Upload success",fileUploadResponseList);
        }catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
        }
    }

    @PostMapping("/videos")
    public ApiResponse<?> uploadVideoBase64(@RequestBody UploadVideoBase64Response videoBase64Response) {
        try {
            List<FileUploadResponse> fileUploadResponseList = new ArrayList<>();
            for(String base64: videoBase64Response.getVideos()) {
                FileUploadResponse fileUploadResponse = cloudinaryService.uploadBase64(base64,"videos/","video");
                if(fileUploadResponse==null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
                }
                fileUploadResponseList.add(fileUploadResponse);
            }
            return ApiResponse.success(HttpStatus.CREATED,"Upload success",fileUploadResponseList);
        }catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST,"Upload fail");
        }
    }

    @DeleteMapping("")
    public ApiResponse<?> deleteFile(@RequestBody DeleteFileRequest param) {
        return cloudinaryService.delete(param.getId());
    }
}
