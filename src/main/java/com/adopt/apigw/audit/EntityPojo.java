package com.adopt.apigw.audit;

import lombok.Data;

@Data
public class EntityPojo {

    private Integer id;

    private String classPath;

    private String className;

    public EntityPojo(Integer id, String classPath, String className) {
        super();
        this.id = id;
        this.classPath = classPath;
        this.className = className;
    }

}
