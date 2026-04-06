package com.adopt.apigw.MicroSeviceDataShare;

import lombok.Data;

@Data
public class PartnerAmountMessage {
    private  Integer partnerId;
    private Double comrelval;
    private Double creditconsume;
    private Double balance;
    private Double credit;
    private Integer renewcust_count;
    private Integer newCustomer_count;
}
