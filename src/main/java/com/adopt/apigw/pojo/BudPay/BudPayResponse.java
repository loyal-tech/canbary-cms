package com.adopt.apigw.pojo.BudPay;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudPayResponse {
    String status;

    String authorizationUrl ;

    String accessCode;

    String reference;

    String payerId;
}
