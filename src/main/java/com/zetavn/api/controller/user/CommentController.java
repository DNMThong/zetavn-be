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

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getParentCommentsByPostId(@PathVariable String postId) { //CommentResponse
        List<CommentResponse> commentDTO = commentService.getCommentsByPostId(postId);
        return ApiResponse.success(HttpStatus.OK,"Get parentComments with postId: " + postId, commentDTO);
    }

    @GetMapping("/comments/{commentId}/replies")
    public ApiResponse<List<CommentResponse>> getReply(@RequestParam(value = "postId") String postId, //CommentResponse
                                                          @PathVariable("commentId")Long commentId) {
        List<CommentResponse> commentDTO = commentService.getParentCommentsByPostId(postId, commentId);
        return ApiResponse.success(HttpStatus.OK,"success",commentDTO);
    }

    @PutMapping("/comments")
    public ApiResponse<CommentResponse> updateComment(@RequestBody CommentRequest commentRequest) { //CommentRequest
        CommentResponse comment = commentService.updateComment(commentRequest);
        return ApiResponse.success(HttpStatus.OK ,"Comment has been updated successfully", comment);
    }

    @PostMapping("/comments")
    public ApiResponse<CommentResponse> addComment(@RequestParam(value = "postId") String postId, //CommentRequest
                                                 @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.addComment(postId, commentRequest);
        return ApiResponse.success(HttpStatus.CREATED ,"Comment has been created successfully.", comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable("commentId") Long commentId) {
        return ApiResponse.success(HttpStatus.NO_CONTENT,"Comment has been deleted successfully", commentService.deleteComment(commentId));
    }
}
