package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.sql.Date;

@Data
public class CustIPDetailsDTO {

    private String ipAddress;
    private Date ipPurchaseDate;
    private Date ipExpiredDate;
}
