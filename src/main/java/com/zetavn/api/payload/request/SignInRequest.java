package com.zetavn.api.payload.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class SignInRequest {
    private String username;
    private String password;
}
