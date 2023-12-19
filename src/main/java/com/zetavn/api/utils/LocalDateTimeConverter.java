package com.zetavn.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.*;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        if(attribute==null) {
            return null;
        }
        OffsetDateTime timeUtc = attribute.atOffset(ZoneOffset.UTC);
        OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(ZoneOffset.ofHours(-7));
        return Timestamp.valueOf(offsetTime.toLocalDateTime());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null) {
            return null;
        }
        OffsetDateTime timeUtc = dbData.toLocalDateTime().atOffset(ZoneOffset.UTC);
        OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(ZoneOffset.ofHours(7));
        return offsetTime.toLocalDateTime();
    }
}

