package com.zetavn.api.model.dto;

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
    private String postId;
    private UserMentionDto userEntity;
    private String content;
    private PostAccessModifierEnum accessModifier;
    private LocalDateTime createdAt;
    private PostActivityDto postActivity;
    List<PostMediaDto> postMedias;
    List<PostMentionDto> postMentions;
}
