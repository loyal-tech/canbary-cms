package com.adopt.apigw.modules.Alert.communication.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SMSConfig {

//    @Value("${amazon.secretKey}")
//    private String secretKey;
//    @Value("${amazon.accessKey}")
//    private String accessKey;
    private String destination;
    private String message;
    private String source;
    private String templateId;
//    @Value("${amazon.region}")
//    private String region;
//    @Value("${amazon.message.type}")
//    private String messageType;

    public SMSConfig(String destination, String message,String source,String templateId) {
        this.destination = destination;
        this.message = message;
        this.source = source;
        this.templateId = templateId;
    }

    public SMSConfig() {
    }
}
