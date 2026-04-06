package com.adopt.apigw.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerEndDateUpdateMessage {
    private String messageId;
    private String message;
    private Date messageDate;

    private static final String ID = "id";

    private static final String USERNAME = "username";
    private static final String MVNO_ID = "mvnoId";

    private static final String STARTDATE = "startDate";

    private static final String ENDDATE = "endDate";

    private static final String EXPIRYDATE = "expiryDate";

    private Map<String,Object> customerData;

    public CustomerEndDateUpdateMessage() {

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's enddate data updates";
    }

}
