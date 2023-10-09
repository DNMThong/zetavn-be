package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.response.PostMentionResponse;

public class PostMentionMapper {

    public static PostMentionResponse mapToResponse(PostMentionRequest postMentionRequest) {
        if (postMentionRequest == null) {
            return null;
        }

        PostMentionResponse postMentionResponse = new PostMentionResponse();
        postMentionResponse.setUserId(postMentionRequest.getUserId());

        return postMentionResponse;
    }

    public static PostMentionRequest mapToRequest(PostMentionResponse postMentionResponse) {
        if (postMentionResponse == null) {
            return null;
        }

        PostMentionRequest postMentionRequest = new PostMentionRequest();
        postMentionRequest.setUserId(postMentionResponse.getUserId());

        return postMentionRequest;
    }
}

