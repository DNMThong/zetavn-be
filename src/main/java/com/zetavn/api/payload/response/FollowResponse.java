package com.zetavn.api.payload.response;

import com.zetavn.api.enums.FollowPriorityEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponse {
    private long followId;
    private String followerId;
    private String followingId;
    private FollowPriorityEnum priority;

}
