package com.adopt.apigw.converter;

import com.adopt.apigw.model.common.FieldType;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FieldTypeConverter implements AttributeConverter<List<FieldType>, String> {

    @Override
    public String convertToDatabaseColumn(List<FieldType> attribute) {
        // TODO Auto-generated method stub
        return attribute.stream().map(otpType -> otpType.name()).collect(Collectors.joining(","));
    }

    @Override
    public List<FieldType> convertToEntityAttribute(String dbData) {
        // TODO Auto-generated method stub

        return Arrays.asList(dbData.split(",")).stream().map(s -> FieldType.valueOf(s))
                .collect(Collectors.toList());
    }
}
