package com.zetavn.api.controller;

import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostLikeService;
import com.zetavn.api.service.PostService;
import com.zetavn.api.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/posts")
@Slf4j
public class PostController {
    @Autowired
    PostService postService;

    @Autowired
    PostLikeService postLikeService;

    @GetMapping("{Id}/post-like")
    public ApiResponse<?> getListLikedUserOfPost(@PathVariable("Id") String postId) {
        return postLikeService.getListLikedUserOfPost(postId);
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getPostById(@PathVariable String postId) {
        return ApiResponse.success(HttpStatus.OK, "Get post success", postService.getPostById(postId));
    }

    @PostMapping("")
    public ApiResponse<?> createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }

    @PutMapping("/{postId}")
    public ApiResponse<?> updatePost(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        return postService.updatePost(postId, postRequest);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<?> deletePost(@PathVariable String postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/postMedia")
    public ApiResponse<?> getPostById(@RequestParam(name = "userId") String userId,
                                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "9", required = false) Integer pageSize) {
        return postService.getPostsWithMediaByUserId(userId, pageNumber, pageSize);
    }
}
