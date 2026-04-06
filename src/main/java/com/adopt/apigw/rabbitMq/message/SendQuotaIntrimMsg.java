package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendQuotaIntrimMsg {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private Map<String, Object> quotaData = new HashMap<>();
}
