package com.zetavn.api.controller;

import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.payload.request.PostLikeRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v0/post-like")
public class PostLikeController {
    @Autowired
    PostLikeService postLikeService;

    @PostMapping("")
    public ApiResponse<PostLikeEntity> createPostLike(@RequestBody PostLikeRequest postLikeRequest){
        return postLikeService.createPostLike(postLikeRequest);
    }
    @DeleteMapping("")
    public ApiResponse<?> remove (@RequestBody PostLikeRequest postLikeRequest){
        return postLikeService.removePostLike(postLikeRequest);
    }
}
