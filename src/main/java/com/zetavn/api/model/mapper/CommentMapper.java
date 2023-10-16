package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.repository.CommentRepository;
import com.zetavn.api.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class CommentMapper {

    public static CommentResponse entityToCommentResponse(CommentEntity comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getCommentId());
        commentResponse.setContent(comment.getContent());

        OverallUserResponse u = OverallUserMapper.entityToDto(comment.getUserEntity());
        commentResponse.setUser(u);

        commentResponse.setMediaPath(comment.getMediaPath());

        return commentResponse;
    }


}
