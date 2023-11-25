package com.zetavn.api.payload.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StatisticRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDay;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDay;
}
