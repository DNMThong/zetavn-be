package com.zetavn.api.payload.response;

import com.zetavn.api.model.dto.UserAdminDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatisticNewUserResponse {
    private List<UserAdminDto> users;
}
