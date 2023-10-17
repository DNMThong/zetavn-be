package com.zetavn.api.utils;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileUploadUtil {
    public static boolean isImage(MultipartFile file) {
        Tika tika = new Tika();
        try {
            String fileType = tika.detect(file.getInputStream());
            return fileType.startsWith("image/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isVideo(MultipartFile file) {
        Tika tika = new Tika();
        try {
            String fileType = tika.detect(file.getInputStream());
            return fileType.startsWith("video/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
