package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.Country;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.Data;

import java.io.IOException;

@Data

public class SaveStateSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;

    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;

    private String lastModifiedByName;
}

class CountrySerializer extends JsonSerializer<Country> {
    @Override
    public void serialize(Country city, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // Serialize only the necessary fields of the Partner entity
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", city.getId());
        jsonGenerator.writeStringField("name", city.getName());
        // Serialize other necessary fields...
        jsonGenerator.writeEndObject();
    }
}

class CountryDeserializer extends JsonDeserializer<Country> {
    @Override
    public Country deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // Deserialize the necessary fields and construct a Partner object
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Integer id = node.get("id").asInt();
        String name = node.get("name").asText();
        // Deserialize other necessary fields...
        return new Country(id, name);
    }
}
