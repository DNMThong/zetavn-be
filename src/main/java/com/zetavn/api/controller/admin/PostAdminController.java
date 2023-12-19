package com.zetavn.api.controller.admin;

import com.zetavn.api.payload.request.UpdatePostForAdminRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/admins/posts")
@PreAuthorize("hasAuthority('ADMIN')")
public class PostAdminController {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @GetMapping()
    public ApiResponse<?> getAllPosts(@RequestParam(name = "status", defaultValue = " ", required = false) String status,
                                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return postService.getAllPostsForAdminByStatus(status,pageNumber,pageSize);
    }

    @PutMapping()
    public ApiResponse<?> update(@RequestBody UpdatePostForAdminRequest request) {
        return postService.updatePostForAdmin(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOnePost(@PathVariable String id) {
        return postService.getOnePostForAdmin(id);
    }

    @PutMapping("/lock/{id}")
    public ApiResponse<?> lockPost(@PathVariable String id) {
        return postService.lockPostForAdmin(id);
    }
}
