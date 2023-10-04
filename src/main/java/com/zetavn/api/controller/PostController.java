package com.zetavn.api.controller;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostLikeOfPostResponse;
import com.zetavn.api.payload.response.PostLikeResponse;
import com.zetavn.api.service.PostLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v0/posts")
public class PostController {
    @Autowired
    PostLikeService postLikeService;
    @GetMapping("{postId}/post-like")
    public ApiResponse<PostLikeOfPostResponse> getListLikedUserOfPost(@PathVariable("postId") String postId){
    return postLikeService.getListLikedUserOfPost(postId);
    }
}
