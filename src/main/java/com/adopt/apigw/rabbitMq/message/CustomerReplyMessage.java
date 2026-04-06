package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.radius.CustReplyItem;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomerReplyMessage.class)
public class CustomerReplyMessage {
    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
    private static final String ID = "id";
    private static final String CUSTID = "custid";
    private static final String ATTRIBUTE = "attribute";
    private static final String ATTRIBUTEVALUE = "attributevalue";
    private static final String TEMPID = "tempid";
    private static final String MVNOID = "mvnoId";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

    public CustomerReplyMessage(CustReplyItem custReplyItem){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, custReplyItem.getId());
        map.put(CUSTID, custReplyItem.getCustid());
        map.put(ATTRIBUTE, custReplyItem.getAttribute());
        map.put(ATTRIBUTEVALUE, custReplyItem.getAttributevalue());
        map.put(TEMPID, custReplyItem.getTempid());
        map.put(MVNOID, custReplyItem.getMvnoId());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Reply from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }

}
