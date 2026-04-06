package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateMessage {
    private String messageId;
    private String message;
    private Date messageDate;

    private static final String ID = "id";

    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";
    private static final String STATUS = "status";
    private static final String PASSWORD = "password";
    private static final String NAS_PORT_ID = "nasPortId";

    private static final String STARTDATE = "startDate";

    private static final String ENDDATE = "endDate";

    private static final String EXPIRYDATE = "expiryDate";

    private Map<String,Object> customerData;

    public CustomerUpdateMessage(Customers customers) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, customers.getId());
        map.put(MVNO_ID, customers.getMvnoId());
        map.put(USERNAME, customers.getUsername());
        map.put(STATUS, customers.getStatus());
        map.put(NAS_PORT_ID,customers.getNasPortId());
        map.put(PASSWORD,customers.getPassword());
        this.customerData = map;
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's status updates";

    }
}
