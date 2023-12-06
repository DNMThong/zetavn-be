package com.zetavn.api.payload.response;

import com.zetavn.api.enums.CallTypeEnum;
import com.zetavn.api.payload.request.CallRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallResponse {
    OverallUserResponse from;
    CallTypeEnum type;
    String roomId;

    public CallResponse(CallRequest callRequest) {
        this.from = callRequest.getFrom();
        this.type = callRequest.getType();
        this.roomId = callRequest.getRoomId();
    }
}
