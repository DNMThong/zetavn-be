package com.zetavn.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatus {
    private int id;
    private String title;
    private String name;
    private String desc;
    private String pic;
    private List<ActivityStatusDetail> details;
}
