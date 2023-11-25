package com.zetavn.api.payload.response;

import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

@Data
@AllArgsConstructor
public class PostLikeResponse {

    String postId;

    String userId;
}
