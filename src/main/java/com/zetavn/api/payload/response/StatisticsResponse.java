package com.zetavn.api.payload.response;

import lombok.Data;

import java.util.List;
@Data
public class StatisticsResponse {
    List<StatisticsOneDayResponse> data;
}
