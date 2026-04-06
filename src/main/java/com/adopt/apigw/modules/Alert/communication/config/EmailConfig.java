package com.adopt.apigw.modules.Alert.communication.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailConfig {

    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
}
