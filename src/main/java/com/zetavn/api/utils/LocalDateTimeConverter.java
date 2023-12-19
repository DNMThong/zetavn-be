package com.zetavn.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return (attribute == null ? null : Timestamp.valueOf(attribute));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.of(dbData.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime vietnamZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime vietnamDateTime = vietnamZonedDateTime.toLocalDateTime();

        return (dbData == null ? null : vietnamDateTime);
    }
}

