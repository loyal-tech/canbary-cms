package com.adopt.apigw.model.common;

public class BooleanWithMessage {
    private boolean allowed;
    private String message;

    public BooleanWithMessage(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getMessage() {
        return message;
    }
}

