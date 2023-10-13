package com.zetavn.api.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostActivityRequest {
    private String activity;
    private String description;
    private String activityIconPath;
    private Long postActivityParentId;
}
