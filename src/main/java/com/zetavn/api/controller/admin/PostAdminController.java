package com.zetavn.api.controller.admin;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v0/admins/posts")
public class PostAdminController {
    @Autowired
    PostService postService;
    @GetMapping()
    public ApiResponse<?> getAllPosts( @RequestParam(name = "status",defaultValue = " ",required = false) String status,
                                       @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        return postService.getAllPostsForAdminByStatus(status,pageNumber,pageSize);
    }
    @PutMapping()
    public ApiResponse<?>update(@RequestParam String id, @RequestParam String status){
        return postService.updatePostForAdmin(id,status);
    }
}
