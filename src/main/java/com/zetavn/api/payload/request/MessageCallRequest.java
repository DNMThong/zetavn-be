package com.zetavn.api.payload.request;

import com.zetavn.api.enums.CallTypeEnum;
import com.zetavn.api.enums.MessageCallStatusEnum;
import com.zetavn.api.enums.MessageCallTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCallRequest {
    Integer duration;
    MessageCallStatusEnum status;
    CallTypeEnum type;
}
