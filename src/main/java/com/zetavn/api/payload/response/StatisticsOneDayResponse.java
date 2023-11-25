package com.zetavn.api.payload.response;

import lombok.Data;

import java.time.LocalDate;
@Data
public class StatisticsOneDayResponse {
    private LocalDate date;
    private Long newUser;
    private Long newPost;
    private Long totalViewPost;
    private Long totalCommentPost;
    private Long totalSharePost;
    private Long totalLikePost;
    private Long totalBookmarkPost;
    private Long totalFollower;
}
