package com.zetavn.api.payload.response;

import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.dto.UserMentionDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private OverallUserResponse user;
    private String content;
    private String mediaPath;
}
