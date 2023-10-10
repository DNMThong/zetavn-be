package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.CategoriesDto;
import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.PostResponse;

import java.util.List;

public class PostMapper {

    public static PostResponse mapToResponse(PostRequest postRequest, CategoriesDto categoriesDto, List<PostMediaDto> postMediaDtos, List<UserMentionDto> postMentions) {
        PostResponse postResponse = new PostResponse();
        postResponse.setUserId(postRequest.getUserId());
        postResponse.setContent(postRequest.getContent());
        postResponse.setAccessModifier(postRequest.getAccessModifier());
        postResponse.setCategory(categoriesDto);
        postResponse.setPostMedias(postMediaDtos);
        postResponse.setPostMentions(postMentions);
        return postResponse;
    }
}
