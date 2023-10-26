package com.zetavn.api.payload.response;

import com.zetavn.api.enums.StatusFriendsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {
    private OverallUserResponse user;
    private Long totalFriends;
    private Long totalPosts;
    private Long countLikesOfPosts;
    private StatusFriendsEnum status;
}
