package com.adopt.apigw.model.radius;

public enum VoucherFormatType {
    NUMERIC("NUMERIC"), UPPERCASE("UPPERCASE"), LOWERCASE("LOWERCASE"), ALPHANUMERIC("ALPHANUMERIC");
    private String value;

    VoucherFormatType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
