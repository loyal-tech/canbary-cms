package com.adopt.apigw.exception;

public class AccessLevelGroupNotFound extends Exception {

    private final String message;

    public AccessLevelGroupNotFound(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AccessLevelGroupNotFound{" +
                "message='" + message + '\'' +
                '}';
    }
}
