package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.enums.NotiFriendRequestEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {

    private OverallUserResponse user;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
    private NotiFriendRequestEnum status;
}
