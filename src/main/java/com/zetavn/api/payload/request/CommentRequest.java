package com.zetavn.api.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long commentId;
    private Long commentEntityParentId;
    private String userId;
    private String content;
    private String mediaPath;
}
