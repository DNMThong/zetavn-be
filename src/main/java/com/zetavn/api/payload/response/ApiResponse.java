package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {
    private int code;
    private HttpStatus status;
    private String message;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime time = LocalDateTime.now();
    private T data;

    public ApiResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.time = LocalDateTime.now();
        this.data = null;
    }
    public ApiResponse(HttpStatus status, String message,T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.time = LocalDateTime.now();
        this.data = data;
    }
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message);
    }
    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }
}
