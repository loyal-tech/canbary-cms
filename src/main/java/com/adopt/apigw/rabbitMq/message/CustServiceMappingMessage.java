package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustServiceMappingMessage.class)
public class CustServiceMappingMessage {
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String SERVICE_ID = "serviceId";
    private static final String LEASECIRCUITID = "leasecircuitid";
    private static final String CONNECTIONO = "connectionNo";
    private static final String NICKNAME = "nickname";
    private static final String STOPSERVICE = "stopServiceDate";
    private static final String QOS_POLICY_ID = "qospolicyId";
    private static final String UPLOAD_QOS = "uploadqos";
    private static final String DOWNLOAD_QOS = "downloadqos";
    private static final String UPLOAD_TS = "uploadts";
    private static final String DOWNLOAD_TS = "downloadts";
    private static final String QUOTA_LIST = "quotaList";
    private static final String SERVICE = "service";
    private static final String IS_DELETE = "isDelete";
    private static final String OFFER_PRICE = "offerPrice";
    private static final String TAX_AMOUNT = "taxAmount";
    private static final String CREDIT_DOC_ID = "creditdocid";
    private static final String WALLET_BAL_USED = "walletBalUsed";
    private static final String PURCHASE_TYPE = "purchaseType";

    private String messageId;
    private String message;
    private String operation;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

    private String custServiceMappingData;

    public CustServiceMappingMessage(){}

    public CustServiceMappingMessage (CustomerServiceMapping customerServiceMapping){

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Service Mapping from Api Gateway";
        //this.data = map;
        this.custServiceMappingData = customerServiceMapping.toString();
        this.sourceName = "APIGATEWAY";
        this.operation=operation;
    }

}
