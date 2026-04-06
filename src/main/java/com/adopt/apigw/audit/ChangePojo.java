package com.adopt.apigw.audit;

import lombok.Data;

@Data
public class ChangePojo {

    private String property;

    private String left;

    private String right;

    public ChangePojo(String property, String left, String right) {
        super();
        this.property = property;
        this.left = left;
        this.right = right;
    }
}
