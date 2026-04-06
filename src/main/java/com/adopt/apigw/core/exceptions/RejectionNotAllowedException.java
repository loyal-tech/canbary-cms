package com.adopt.apigw.core.exceptions;

public class RejectionNotAllowedException extends RuntimeException {
    public RejectionNotAllowedException() {
        super("You are not allowed to reject");
    }

    public RejectionNotAllowedException(String msg) {
        super(msg);
    }
}
