package com.adopt.apigw.soap.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberAccountDTO {

    protected String name;
    protected String activated;
    protected String password;
    protected String locationLock;
    private List<ServiceSubscription> serviceSubscriptions;
}

