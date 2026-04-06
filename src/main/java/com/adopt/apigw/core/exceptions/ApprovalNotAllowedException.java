package com.adopt.apigw.core.exceptions;

public class ApprovalNotAllowedException extends RuntimeException {
    public ApprovalNotAllowedException() {
        super("You are not allowed to approve");
    }

    public ApprovalNotAllowedException(String msg) {
        super(msg);
    }
}
