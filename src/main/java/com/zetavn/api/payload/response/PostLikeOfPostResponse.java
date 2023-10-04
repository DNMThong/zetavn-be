package com.zetavn.api.payload.response;

import lombok.Data;

import java.util.List;
@Data
public class PostLikeOfPostResponse {

    private Integer likeCount;

    private List<UserResponse> likedUsers;
}
