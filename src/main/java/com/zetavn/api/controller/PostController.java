package com.zetavn.api.controller;

import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostLikeService;
import com.zetavn.api.service.PostService;
import com.zetavn.api.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return postService.createPost(postRequest, id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public ApiResponse<?> updatePost(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return postService.updatePost(postId, postRequest, id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<?> removePost(@PathVariable String postId) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return postService.removePost(id, postId);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/postMedia")
    public ApiResponse<?> getPostById(@RequestParam(name = "userId") String userId,
                                      @RequestParam(name = "type") String type,
                                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "9", required = false) Integer pageSize) {
        System.out.println(pageSize);
        return postService.getPostsWithMediaByUserId(userId, type, pageNumber, pageSize);
    }
}
