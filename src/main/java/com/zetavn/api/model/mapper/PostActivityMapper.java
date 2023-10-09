package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.PostActivityRequest;
import com.zetavn.api.payload.response.PostActivityResponse;

public class PostActivityMapper {

    public static PostActivityResponse mapToResponse(PostActivityRequest postActivityRequest) {
        if (postActivityRequest == null) {
            return null;
        }

        PostActivityResponse postActivityResponse = new PostActivityResponse();
        postActivityResponse.setActivity(postActivityRequest.getActivity());
        postActivityResponse.setDescription(postActivityRequest.getDescription());
        postActivityResponse.setActivityIconPath(postActivityRequest.getActivityIconPath());
        postActivityResponse.setPostActivityParentId(postActivityRequest.getPostActivityParentId());

        return postActivityResponse;
    }

    public static PostActivityRequest mapToRequest(PostActivityResponse postActivityResponse) {
        if (postActivityResponse == null) {
            return null;
        }

        PostActivityRequest postActivityRequest = new PostActivityRequest();
        postActivityRequest.setActivity(postActivityResponse.getActivity());
        postActivityRequest.setDescription(postActivityResponse.getDescription());
        postActivityRequest.setActivityIconPath(postActivityResponse.getActivityIconPath());
        postActivityRequest.setPostActivityParentId(postActivityResponse.getPostActivityParentId());

        return postActivityRequest;
    }
}
