package com.zetavn.api.payload.response;

import com.zetavn.api.enums.FriendStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipResponse {
    private Long id;
    private OverallUserResponse senderUser;
    private OverallUserResponse receiverUser;
    private FriendStatusEnum status;
}
