package com.zetavn.api.payload.request;

import com.zetavn.api.enums.FollowPriorityEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequest {
    private String followerId;
    private String followingId;
    private FollowPriorityEnum priority;

}
