package com.zetavn.api.model.dto;

import com.zetavn.api.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeDto {
    private long postLikeId;
    private UserEntity userEntity;
}
