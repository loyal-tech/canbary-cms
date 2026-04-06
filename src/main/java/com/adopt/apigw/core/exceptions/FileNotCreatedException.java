package com.adopt.apigw.core.exceptions;

import java.io.IOException;

public class FileNotCreatedException extends IOException {
    public FileNotCreatedException() {
        super("File not created");
    }

    public FileNotCreatedException(String msg) {
        super(msg);
    }
}
