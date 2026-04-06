package com.adopt.apigw.rabbitMq.message;


import com.adopt.apigw.modules.Customers.SendCustomerPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOnlinePaymentRevenueMessage implements Serializable {
    private String messageId;
    private String message;
    private String sourceName;
    private Map<String, Object> customerData;

    private static final String TITLE = "title";

    private static final String CUSTOMERID = "custid";

    private static  final String AMOUNT = "amount";




    public SendOnlinePaymentRevenueMessage(SendCustomerPaymentDTO sendCustomerPaymentDTO){
        Map<String, Object> map = new HashMap<>();
        if(sendCustomerPaymentDTO.getCustId() != null) {
            map.put(CUSTOMERID, sendCustomerPaymentDTO.getCustId().toString());
        }
        if(sendCustomerPaymentDTO.getAmount() != null) {
            map.put(AMOUNT , sendCustomerPaymentDTO.getAmount().toString());
        }
       // this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer payment send to Revenue";
        this.customerData = map;
        this.sourceName = "RADIUS";
    }

}
