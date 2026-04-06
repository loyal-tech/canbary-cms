package com.adopt.apigw.kafka;

import com.google.gson.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonConfig {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, typeOfSrc, context) -> new JsonPrimitive(src.format(formatter)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), formatter))
                .create();
    }
}