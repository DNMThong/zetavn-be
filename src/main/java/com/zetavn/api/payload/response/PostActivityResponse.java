package com.zetavn.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostActivityResponse {
    private String activity;
    private String description;
    private String activityIconPath;
    private Long postActivityParentId;
}
