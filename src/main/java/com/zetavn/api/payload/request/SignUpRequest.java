package com.zetavn.api.payload.request;


import com.zetavn.api.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String firstName;
    private String lastName;
    private String password;
    private String email;

//    private LocalDate birthday;
//    private GenderEnum gender;
}
