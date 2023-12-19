package com.zetavn.api.controller;

import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.payload.request.PostLikeRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v0/post-like")
public class PostLikeController {
    @Autowired
    PostLikeService postLikeService;

    @GetMapping("")
    public ApiResponse<?> checkPostLike(@RequestParam(name = "postId") String postId){
        try {
            String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return postLikeService.checkPostLike(userId,postId);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("")
    public ApiResponse<?> createPostLike(@RequestBody PostLikeRequest postLikeRequest){
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return postLikeService.createPostLike(postLikeRequest, id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @DeleteMapping("")
    public ApiResponse<?> remove (@RequestBody PostLikeRequest postLikeRequest){
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return postLikeService.removePostLike(postLikeRequest, id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
