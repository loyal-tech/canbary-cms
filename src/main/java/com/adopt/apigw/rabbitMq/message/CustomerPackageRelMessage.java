package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomerPackageRelMessage.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerPackageRelMessage {

    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String END_DATE = "endDate";
    private static final String EXPIRY_DATE = "expiryDate";
    private static final String CUST_PLAN_STATUS = "custPlanStatus";
    private static final String CUST_SERVICE_MAPPING_ID = "custServiceMappingId";
    private static final String SKIP_QUOTA_UPDATE = "skipQuotaUpdate";
    private String operation;

    private String messageId;
    private String message;
    private String sourceName;
//    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Map<String, Object> data;

    public CustomerPackageRelMessage(){}

    public CustomerPackageRelMessage (CustPlanMapppingPojo custPlanMappping, String operation){

        Map<String, Object> map = new HashMap<>();
        map.put(ID, custPlanMappping.getId());
        map.put(CUST_ID, custPlanMappping.getCustid());
        if(custPlanMappping.getEndDate() != null)
            map.put(END_DATE, custPlanMappping.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if(custPlanMappping.getExpiryDate() != null)
            map.put(EXPIRY_DATE, custPlanMappping.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if(custPlanMappping.getCustPlanStatus() != null){
            map.put(CUST_PLAN_STATUS , custPlanMappping.getCustPlanStatus());
        }
        if(custPlanMappping.getCustServiceMappingId() != null){
            map.put(CUST_SERVICE_MAPPING_ID , custPlanMappping.getCustServiceMappingId());
        }
        if(custPlanMappping.getSkipQuotaUpdate() != null) {
            map.put(SKIP_QUOTA_UPDATE , custPlanMappping.getSkipQuotaUpdate());
        } else {
            map.put(SKIP_QUOTA_UPDATE , false);
        }


//        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Package rel from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
        this.operation=operation;


    }




    }
