package com.adopt.apigw.soap.MtnDebitRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receiverinfo", propOrder = {
        "fromfri"
})
public class receiverinfo {

    @XmlElement(required = true)
    protected String fri;

    @XmlElement
    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }
}
