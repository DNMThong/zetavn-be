package com.zetavn.api.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private Long commentId;
    private Long commentEntityParentId;
    private String userId;
    private String content;
    private String mediaPath;
}
