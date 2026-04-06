package com.adopt.apigw.soap.Dto;


import javax.xml.bind.annotation.XmlElement;

public class Override {  // Make this class public

    private String name;

    private String value;

    @XmlElement(name = "overrideName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "overrideValue")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
