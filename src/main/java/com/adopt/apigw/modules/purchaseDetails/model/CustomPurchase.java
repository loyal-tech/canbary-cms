package com.adopt.apigw.modules.purchaseDetails.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomPurchase {
    public Long purchaseid;
    public Long orderid;
    public Double balanced_used;
    public Integer custid;
    public Long ledger_details_id;
}
