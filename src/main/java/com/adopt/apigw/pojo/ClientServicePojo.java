package com.adopt.apigw.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientServicePojo {

    private Integer id;

    private String name;

    private String value;
    
    private Integer mvnoId;

    private Integer displayId;

    private String displayName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
