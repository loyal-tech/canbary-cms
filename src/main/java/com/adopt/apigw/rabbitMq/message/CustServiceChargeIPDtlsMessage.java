package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CustServiceChargeIPDetails;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class CustServiceChargeIPDtlsMessage {

    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String CUST_SERVICE_MAPPING_ID = "custservicemappingid";
    private static final String STATIC_IP_ADDRESS = "static_ip_address";
    private static final String STATIC_IP_START_DATE = "static_ip_start_date";
    private static final String STATIC_IP_END_DATE = "static_ip_end_date";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

public CustServiceChargeIPDtlsMessage(CustServiceChargeIPDetails custServiceChargeIPDetails) {
    Map<String, Object> map = new HashMap<>();
    map.put(ID, custServiceChargeIPDetails.getId());
    map.put(CUST_ID, custServiceChargeIPDetails.getCustId());
    map.put(CUST_SERVICE_MAPPING_ID, custServiceChargeIPDetails.getCustServiceMappingId());
    map.put(STATIC_IP_ADDRESS, custServiceChargeIPDetails.getStaticIPAdrress());
    map.put(STATIC_IP_START_DATE, custServiceChargeIPDetails.getStaticIPStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    map.put(STATIC_IP_END_DATE, custServiceChargeIPDetails.getStaticIPEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    this.messageDate = new Date();
    this.messageId = UUID.randomUUID().toString();
    this.message = "CustServiceChargeIPDtls from Api Gateway";
    this.data = map;
    this.sourceName = ADOPT_API_GATEWAY;
}
}
