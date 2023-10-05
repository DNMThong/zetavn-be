package com.zetavn.api.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class SignInResponse {

    private UserResponse userInfo;

    private JwtResponse tokens;

}
