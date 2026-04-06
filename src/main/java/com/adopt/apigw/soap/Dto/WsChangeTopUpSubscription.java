package com.adopt.apigw.soap.Dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;

@Data
public class WsChangeTopUpSubscription {

    protected String subscriberId;
    protected Integer updateAction;
    protected Integer topUpSubscriptionId;
    protected Integer subscriptionStatusValue;

}
