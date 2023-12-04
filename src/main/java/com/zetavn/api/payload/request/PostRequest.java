package com.zetavn.api.payload.request;

import com.zetavn.api.enums.PostAccessModifierEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String content;
    private PostAccessModifierEnum accessModifier;
    private Integer activityId;
    private List<PostMediaRequest> medias;
    private List<PostMentionRequest> mentions;
}
