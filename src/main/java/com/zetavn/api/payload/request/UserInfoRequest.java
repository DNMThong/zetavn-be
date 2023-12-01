package com.zetavn.api.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.GenderEnum;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserInfoRequest {

    private String email;
    private String username;
    private String phone;;
    private String firstName;
    private String lastName;

    private String aboutMe;
    private GenderEnum genderEnum;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private String livesAt;
    private String worksAt;
    private String studiedAt;

}
