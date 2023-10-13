package com.zetavn.api.controller.user;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.ApiResponse;
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

    @GetMapping("/posts/{id}/comments")
    public ApiResponse<List<CommentResponse>> getParentCommentsByPostId(@PathVariable String id) { //CommentResponse
        return commentService.getCommentsByPostId(id);
    }

    @GetMapping("/comments/{commentId}/replies")
    public ApiResponse<List<CommentResponse>> getReply(@RequestParam(value = "postId") String postId, //CommentResponse
                                                          @PathVariable("commentId")Long commentId) {
        return commentService.getParentCommentsByPostId(postId, commentId);
    }

    @PutMapping("/comments/{id}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable("id") Long id,
                                                      @RequestParam("content") String content,
                                                      @RequestParam("media") String media) {
        return commentService.updateComment(id, content, media);
    }

    @PostMapping("/comments")
    public ApiResponse<CommentResponse> addComment(@RequestParam("id") String id,
                                                 @RequestBody CommentRequest commentRequest) {

        return commentService.addComment(id, commentRequest);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable("commentId") Long commentId) {
        return ApiResponse.success(HttpStatus.NO_CONTENT,"Comment has been deleted successfully", commentService.deleteComment(commentId));
    }
}
