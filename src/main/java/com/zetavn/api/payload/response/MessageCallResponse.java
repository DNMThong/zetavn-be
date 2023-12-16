package com.zetavn.api.payload.response;

import com.zetavn.api.enums.CallTypeEnum;
import com.zetavn.api.enums.MessageCallStatusEnum;
import com.zetavn.api.enums.MessageCallTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCallResponse {
    Integer duration;
    MessageCallStatusEnum status;
    CallTypeEnum type;
}
