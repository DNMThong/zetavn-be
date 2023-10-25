package com.zetavn.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OverallUserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String display;
    private String avatar;
    private String poster;
    private Integer totalFriends;
    private Integer totalPosts;
    private Integer countLikesOfPosts;
    private Integer followers;
}
