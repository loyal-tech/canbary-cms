package com.adopt.apigw.soap.Dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class WsChangeAddOnSubscription {

    protected String subscriberId;
    protected String alternateId;
    protected Integer updateAction;
    protected Integer addOnSubscriptionId;
    protected BigInteger subscriptionStatusValue;
    protected String subscriptionStatusName;
    protected String addOnName;
    protected String subscriptionOrder;
    protected String startTime;
    protected String endTime;
    protected String rejectReason;
    protected String parameter1;
    protected String parameter2;

}
