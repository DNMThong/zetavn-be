package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.PostNotificationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostNotificationResponse {
    private Long id;
    private String postId;
    private Long relatedId;
    private OverallUserResponse interacting;
    private OverallUserResponse receiving;
    private PostNotificationEnum type;
    private Boolean isRead;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    private Boolean isCancel;
}
