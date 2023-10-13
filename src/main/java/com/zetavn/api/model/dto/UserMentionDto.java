package com.zetavn.api.model.dto;

import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.model.entity.UserInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMentionDto {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avatar;
    private String poster;
    private RoleEnum role;
    private UserInfoEntity userInfo;
}
