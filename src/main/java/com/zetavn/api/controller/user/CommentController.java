package com.zetavn.api.controller.user;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    private CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<Paginate<List<CommentResponse>>> getParentCommentsByPostId(@PathVariable String postId,
                                                                                  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                                  @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return commentService.getCommentsByPostId(postId, pageNumber, pageSize);
    }

    @GetMapping("/comments/{commentId}/replies")
    public ApiResponse<Paginate<List<CommentResponse>>> getReply(@RequestParam(value = "postId") String postId,
                                                                 @PathVariable("commentId")Long commentId,
                                                                 @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                 @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return commentService.getParentCommentsByPostId(postId, commentId, pageNumber, pageSize);
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable("commentId") Long id,
                                                      @RequestBody CommentRequest commentRequest) {
        return commentService.updateComment(id, commentRequest);
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CommentResponse> addComment(@PathVariable("postId") String postId,
                                                 @RequestBody CommentRequest commentRequest) {
        return commentService.addComment(postId, commentRequest);
    }
    @PostMapping("/posts/{postId}/comments/{id}")
    public ApiResponse<CommentResponse> addComment(@PathVariable("postId") String postId,
                                                   @PathVariable("id") Long id,
                                                   @RequestBody CommentRequest commentRequest) {
        return commentService.addSubComment(postId, id,commentRequest);
    }


    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
