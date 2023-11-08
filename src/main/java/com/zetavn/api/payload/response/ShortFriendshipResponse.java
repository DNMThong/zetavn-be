package com.zetavn.api.payload.response;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.enums.StatusFriendsEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortFriendshipResponse {

    private OverallUserResponse targetUser;
    private StatusFriendsEnum status;

}
