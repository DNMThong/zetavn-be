package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.UserStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@ToString
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String phone;
    private String display;
    private String firstName;
    private String lastName;
    private String avatar;
    private String poster;
    private Boolean isAuthorized;

    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime updatedAt;
}
