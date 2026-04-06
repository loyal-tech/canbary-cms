package com.adopt.apigw.rabbitMq.message;

import java.util.Date;

public class BillGenMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Object data;


}
