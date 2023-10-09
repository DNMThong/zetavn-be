package com.zetavn.api.payload.response;

import com.zetavn.api.enums.PostAccessModifierEnum;
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
    private PostActivityResponse postActivity;
    private List<PostMediaResponse> postMedias;
    private List<PostMentionResponse> postMentions;
}
