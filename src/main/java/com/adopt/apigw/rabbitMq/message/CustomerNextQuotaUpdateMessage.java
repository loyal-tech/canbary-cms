package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class CustomerNextQuotaUpdateMessage {
    Integer custId;
    LocalDate nextQuotaResetDate;
    LocalDate nextBillDate;

    public CustomerNextQuotaUpdateMessage(Customers customers) {
        this.custId = customers.getId();
        this.nextQuotaResetDate = customers.getNextQuotaResetDate();
        this.nextBillDate = customers.getNextBillDate();
    }
}
