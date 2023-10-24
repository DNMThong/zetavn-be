package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.GenderEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoResponse {

    private String aboutMe;
    private GenderEnum genderEnum;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private String livesAt;
    private String worksAt;
    private String studiedAt;

    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime updateAt;
    private Long totalFriends;
    private Long totalPosts;
    private Long countLikesOfPosts;
    private Long followers;

}
