package com.zetavn.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatusDetail {
    private int id;
    private String name;
    private String pic;
    private String desc;
}
