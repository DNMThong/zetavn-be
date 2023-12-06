package com.zetavn.api.payload.request;

import com.zetavn.api.enums.CallTypeEnum;
import com.zetavn.api.payload.response.OverallUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallRequest {
    String to;
    OverallUserResponse from;
    CallTypeEnum type;
    String roomId;
}
