package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = PlanServiceForIntegrationMessage.class)
public class PlanServiceForIntegrationMessage {
    private String messageId;
    private String message;
    private String operation;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

    private String planServiceData;

    public PlanServiceForIntegrationMessage(PlanService planService){
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Plan service from Api Gateway";
        //this.data = map;
        this.planServiceData = planService.toString();
        this.sourceName = "APIGATEWAY";
        this.operation=operation;
    }
}
