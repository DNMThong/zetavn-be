package com.zetavn.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.PostAccessModifierEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String id;
    private UserMentionDto user;
    private String content;
    private PostAccessModifierEnum accessModifier;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime updateAt;
    private PostActivityDto activity;
    List<PostMediaDto> medias;
    List<UserMentionDto> mentions;
}
