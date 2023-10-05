package com.zetavn.api.payload.response;

import com.zetavn.api.enums.UserStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@ToString
@Builder
public class UserResponse {
    private String userId;
    private String email;
    private String username;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatar;
    private String poster;
    private UserStatusEnum status;
    private Boolean isAuthorized;
    private Boolean isDeleted;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
