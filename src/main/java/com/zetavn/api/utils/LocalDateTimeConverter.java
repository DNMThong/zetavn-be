package com.zetavn.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.*;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {

        return (attribute == null ? null : Timestamp.valueOf(attribute));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        System.out.println(dbData.toLocalDateTime().toString());
        ZonedDateTime utcZonedDateTime = ZonedDateTime.of(dbData.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime vietnamZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime vietnamDateTime = vietnamZonedDateTime.toLocalDateTime();
        System.out.println(vietnamDateTime.toString());
        return (dbData == null ? null : vietnamDateTime);
    }
}

