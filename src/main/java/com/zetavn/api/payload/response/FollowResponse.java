package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.FollowPriorityEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FollowResponse {
    private long followId;
    private OverallUserResponse userFollower;
    private OverallUserResponse userFollowing;
    private FollowPriorityEnum priority;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
}
