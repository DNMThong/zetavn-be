package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.dto.UserMentionDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private OverallUserResponse user;
    private String content;
    private String mediaPath;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime updatedAt;
}
