package com.adopt.apigw.soap.Dto;

import lombok.Data;

@Data
public class WsSubscribeTopUp {

    protected String subscriberId;
    protected int updateAction;
    protected String topUpPackageName;
    protected int subscriptionStatusValue;
    protected long startTime;
    protected long endTime;
}
