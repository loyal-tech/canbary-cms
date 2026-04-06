package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCOADMupdateMessage {
    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
    private static final String STAFF_USER_SEND_RADIUS = "staff user send radius";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> customerData;


    public CustomerCOADMupdateMessage(Customers customers, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, customers.getId());
        map.put(MVNO_ID, customers.getMvnoId());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }
}
