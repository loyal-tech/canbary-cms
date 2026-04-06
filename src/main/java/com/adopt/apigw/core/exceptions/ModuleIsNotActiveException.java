package com.adopt.apigw.core.exceptions;

public class ModuleIsNotActiveException extends RuntimeException {
    public ModuleIsNotActiveException() {
        super("Module entity is not active");
    }

    public ModuleIsNotActiveException(String msg) {
        super(msg);
    }
}
