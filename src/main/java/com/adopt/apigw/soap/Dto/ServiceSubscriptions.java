package com.adopt.apigw.soap.Dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceSubscriptions {

    @XmlElement(name = "ServiceSubscription")
    private List<ServiceSubscription> serviceSubscriptions = new ArrayList<>(); // Initialize to avoid null

    public List<ServiceSubscription> getServiceSubscriptions() {
        return serviceSubscriptions;
    }

    public void setServiceSubscriptions(List<ServiceSubscription> serviceSubscriptions) {
        this.serviceSubscriptions = serviceSubscriptions;
    }
}

