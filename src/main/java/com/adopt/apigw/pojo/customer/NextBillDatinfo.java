package com.adopt.apigw.pojo.customer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NextBillDatinfo {
    LocalDate nextBilldate;
    String chargeType;

    public NextBillDatinfo(LocalDate nextBilldate, String chargeType) {
        this.nextBilldate = nextBilldate;
        this.chargeType = chargeType;
    }
}
