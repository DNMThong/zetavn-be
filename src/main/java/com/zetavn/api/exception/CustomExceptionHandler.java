package com.zetavn.api.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zetavn.api.payload.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    // 404 Not Found: Tài nguyên yêu cầu không tồn tại trên máy chủ.
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handlerNotFoundException(NotFoundException ex, WebRequest req) {
//        logger.error(ex.getMessage());
        return ApiResponse.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 Bad Request: Yêu cầu không hợp lệ do cú pháp sai hoặc tham số không hợp lệ.
    @ExceptionHandler({DuplicateRecordException.class,InvalidFieldException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handlerDuplicateRecordException(DuplicateRecordException ex, WebRequest req) {
//        logger.error(ex.getMessage());
        return  ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Xử lý tất cả các exception chưa được khai báo
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handlerException(Exception ex, WebRequest req) {
//        logger.error(ex.getMessage());
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handlerForbiddenException(Exception ex, WebRequest req) {
        logger.error("Access denied ex: {}", ex.getMessage());
        return ApiResponse.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
