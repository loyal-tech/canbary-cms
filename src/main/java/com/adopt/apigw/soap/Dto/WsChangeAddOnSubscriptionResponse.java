package com.adopt.apigw.soap.Dto;

import lombok.Data;
import org.joda.time.DateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WsChangeAddOnSubscriptionResponse {

    private Integer addOnId;
    private String addOnName;
    private String addOnStatus;
    private Integer addonSubscriptionId;
    private LocalDateTime endTime;
    private String parameter1;
    private String parameter2;
    private String subscriberIdentity;
    private LocalDateTime usageResetTime;
}
