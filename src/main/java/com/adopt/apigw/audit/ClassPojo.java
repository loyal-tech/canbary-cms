package com.adopt.apigw.audit;

import lombok.Data;

@Data
public class ClassPojo {

    Integer id;

    String className;

    public ClassPojo(Integer id, String className) {
        super();
        this.id = id;
        this.className = className;
    }

}
