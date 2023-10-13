package com.zetavn.api.payload.response;

import com.zetavn.api.enums.FriendStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipResponse {
    private Long id;
    private String senderUserId;
    private String receiverUserId;
    private FriendStatusEnum status;
}
