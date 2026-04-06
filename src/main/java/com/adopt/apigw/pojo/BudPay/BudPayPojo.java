package com.adopt.apigw.pojo.BudPay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudPayPojo {

    String email;

    String amount;

    String callback;

    Integer mvnoid;

    String reference;

    Integer custId;

    Integer planId;

    String customerUsername;

    String totalPlanPrice;
}
