package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanPricesMessage {

    private  Long planId;
    private  Double offerPriceUpdated;
    private  Double taxAmountUpdated;
}
