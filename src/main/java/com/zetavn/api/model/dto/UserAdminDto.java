package com.zetavn.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.payload.response.UserInfoResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class UserAdminDto {
    private String id;
    private String email;
    private String username;
    private String phone;
    private String password;
    private String display;
    private String firstName;
    private String lastName;
    private String avatar;
    private String poster;
    private Boolean isDeleted;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;
    private Boolean isAuthorized;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime updatedAt;
    private UserInfoResponse information;
}
