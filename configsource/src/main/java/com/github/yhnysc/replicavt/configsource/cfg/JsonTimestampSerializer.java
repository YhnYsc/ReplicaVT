package com.github.yhnysc.replicavt.configsource.cfg;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class JsonTimestampSerializer implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
    final DateTimeFormatter _formatter = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_DATE).appendLiteral('-')
            .appendPattern("HH.mm.ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 6, 6, true)
            .toFormatter();

    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(_formatter.format(src));
    }

    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return OffsetDateTime.parse(json.getAsString(), _formatter);
    }
}
