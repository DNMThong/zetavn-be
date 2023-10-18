package com.zetavn.api.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverallUserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String display;
    private String avatar;
    private String poster;
}
