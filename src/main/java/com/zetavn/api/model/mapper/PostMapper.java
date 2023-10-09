package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.PostMediaResponse;
import com.zetavn.api.payload.response.PostMentionResponse;
import com.zetavn.api.payload.response.PostResponse;

import java.util.ArrayList;
import java.util.List;

public class PostMapper {

    public static PostResponse mapToResponse(PostRequest postRequest) {
        PostResponse postResponse = new PostResponse();
        postResponse.setUserId(postRequest.getUserId());
        postResponse.setContent(postRequest.getContent());
        postResponse.setAccessModifier(postRequest.getAccessModifier());

        // Chuyển đổi PostActivityRequest thành PostActivityResponse
        if (postRequest.getPostActivity() != null) {
            postResponse.setPostActivity(PostActivityMapper.mapToResponse(postRequest.getPostActivity()));
        }

        // Chuyển đổi danh sách PostMediaRequest thành danh sách PostMediaResponse
        if (postRequest.getPostMedias() != null) {
            List<PostMediaResponse> postMediaResponses = new ArrayList<>();
            for (PostMediaRequest postMediaRequest : postRequest.getPostMedias()) {
                postMediaResponses.add(PostMediaMapper.mapToResponse(postMediaRequest));
            }
            postResponse.setPostMedias(postMediaResponses);
        }

        // Chuyển đổi danh sách PostMentionRequest thành danh sách PostMentionResponse
        if (postRequest.getPostMentions() != null) {
            List<PostMentionResponse> postMentionResponses = new ArrayList<>();
            for (PostMentionRequest postMentionRequest : postRequest.getPostMentions()) {
                postMentionResponses.add(PostMentionMapper.mapToResponse(postMentionRequest));
            }
            postResponse.setPostMentions(postMentionResponses);
        }

        return postResponse;
    }
}
