package com.adopt.apigw.pojo.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewAdjustedPaymentPojo {

    private String referenceNumber;  //paymentid

    private String paymode;    //payment mode

    private Double amount;   //payment amount

    private Double adjustedAmount; //adjusted amount

    private LocalDate paymentdate; //payment date

    private String type;//type of the payment

    private String status ; //status of the payment
}
