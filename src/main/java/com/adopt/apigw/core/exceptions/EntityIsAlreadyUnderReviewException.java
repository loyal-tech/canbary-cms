package com.adopt.apigw.core.exceptions;

public class EntityIsAlreadyUnderReviewException extends RuntimeException {
    public EntityIsAlreadyUnderReviewException() {
        super("Module entity is already in under review");
    }

    public EntityIsAlreadyUnderReviewException(String msg) {
        super(msg);
    }
}
