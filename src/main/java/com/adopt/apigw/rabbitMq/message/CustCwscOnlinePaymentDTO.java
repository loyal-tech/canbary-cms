package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustCwscOnlinePaymentDTO {
    private Integer custId;
    private Integer mvnoId;
    private String type;
    private String paymentStatus;
    private Long reference;
    private Integer paymentOwnerId;
    private String amount;
}
