package com.zetavn.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMentionDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String display;
    private String avatar;
    private String poster;
}
