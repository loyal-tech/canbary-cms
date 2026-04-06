package com.adopt.apigw.soap.Dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceSubscription {  // Make this class public

    @XmlElement(name = "ServiceId")
    public String serviceId;

    @XmlElement(name = "Override")
    public List<Override> overrides;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<Override> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<Override> overrides) {
        this.overrides = overrides;
    }
}
