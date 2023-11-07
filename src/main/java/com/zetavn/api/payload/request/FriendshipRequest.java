package com.zetavn.api.payload.request;

import com.zetavn.api.enums.FriendStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FriendshipRequest {
    private String senderId;
    private String receiverId;
}
