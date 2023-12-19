package com.zetavn.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.*;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        OffsetDateTime timeUtc = attribute.atOffset(ZoneOffset.UTC);
        OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(ZoneOffset.ofHours(-7));
        System.out.println(offsetTime.toLocalDateTime().toString());
        return (attribute == null ? null : Timestamp.valueOf(offsetTime.toLocalDateTime()));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.of(dbData.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime vietnamZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime vietnamDateTime = vietnamZonedDateTime.toLocalDateTime();

        return (dbData == null ? null : vietnamDateTime);
    }
}

