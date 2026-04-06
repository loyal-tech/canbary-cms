package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCountMessage {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private HashMap<Integer, Long> count;
}
