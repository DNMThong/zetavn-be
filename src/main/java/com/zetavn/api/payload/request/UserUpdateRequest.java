package com.zetavn.api.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String email;
    private String username;
    private String phone;;
    private String firstName;
    private String lastName;
}
