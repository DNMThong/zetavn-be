package com.zetavn.api.payload.request;

import com.zetavn.api.model.dto.PostMediaDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private String path;
}
