package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.payload.response.OverallUserResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public static CommentResponse entityToCommentResponse(CommentEntity comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getCommentId());
        commentResponse.setContent(comment.getContent());

        OverallUserResponse u = OverallUserMapper.entityToOverallUser(comment.getUserEntity());
        commentResponse.setUser(u);

        commentResponse.setMediaPath(comment.getMediaPath());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        return commentResponse;
    }


}
