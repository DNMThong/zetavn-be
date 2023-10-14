package com.zetavn.api.payload.response;

import com.zetavn.api.enums.PostAccessModifierEnum;
import com.zetavn.api.model.dto.PostActivityDto;
import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.dto.UserMentionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String postId;
    private String userId;
    private String content;
    private PostAccessModifierEnum accessModifier;
    private PostActivityDto postActivity;
    private List<PostMediaDto> postMedias;
    private List<UserMentionDto> postMentions;
}
