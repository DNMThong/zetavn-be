package com.zetavn.api.payload.response;

import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.model.mapper.OverallUserMapper;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserContactResponse {
    private OverallUserPrivateResponse user;
    private MessageResponse newMessage;
    private Integer totalUnreadMessage;

    public void incrementTotalUnreadMessage() {
        ++this.totalUnreadMessage;
    }
}
