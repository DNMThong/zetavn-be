package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.response.PostMediaResponse;

public class PostMediaMapper {

    public static PostMediaResponse mapToResponse(PostMediaRequest postMediaRequest) {
        if (postMediaRequest == null) {
            return null;
        }

        PostMediaResponse postMediaResponse = new PostMediaResponse();
        postMediaResponse.setMediaPath(postMediaRequest.getMediaPath());
        postMediaResponse.setMediaType(postMediaRequest.getMediaType());

        return postMediaResponse;
    }

    public static PostMediaRequest mapToRequest(PostMediaResponse postMediaResponse) {
        if (postMediaResponse == null) {
            return null;
        }

        PostMediaRequest postMediaRequest = new PostMediaRequest();
        postMediaRequest.setMediaPath(postMediaResponse.getMediaPath());
        postMediaRequest.setMediaType(postMediaResponse.getMediaType());

        return postMediaRequest;
    }
}
