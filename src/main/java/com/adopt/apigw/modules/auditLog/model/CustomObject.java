package com.adopt.apigw.modules.auditLog.model;

public class CustomObject {
    private String value;

    public CustomObject(String value) {
        if(value !=null) {
            this.value = value;
        }else{
            this.value=null;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CustomObject(Boolean boolValue) {
        if (boolValue != null) {
            this.value = boolValue.toString();
        } else {
            this.value = null; // or any default representation you prefer for null values
        }
    }

}
