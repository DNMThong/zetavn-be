package com.zetavn.api.payload.request;

import com.zetavn.api.enums.FriendStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipRequest {
    private String senderUserId;
    private String receiverUserId;
    private FriendStatusEnum status;
}
