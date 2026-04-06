package com.adopt.apigw.model.common;

public enum FieldType {

    UPPER_CASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), NUMBER("0123456789"), LOWER_CASE("abcdefghijklmnopqrstuvwxyz"),
    SYMBOL("!@#$%^&*_=+-/.?<>)");

    String allowedValues;

    FieldType(String allowedValues) {
        this.allowedValues = allowedValues;
    }

    public String getAllowedValues() {
        return allowedValues;
    }

}
