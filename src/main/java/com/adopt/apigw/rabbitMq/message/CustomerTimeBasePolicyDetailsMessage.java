package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomerTimeBasePolicyDetailsMessage.class)
public class CustomerTimeBasePolicyDetailsMessage {
    //TimeBasePolicy
    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String FROMDAY = "fromday";
    private static final String TODAY = "today";

    private static final String QQSID = "qqsid";
    private static final String FROMTIME = "fromtime";
    private static final String TOTIME = "totime";
    private static final String SPEED = "speed";
    private static final String ACCESS = "access";
    private static final String CUSTID = "custid";
    private static final String PLANID = "planid";
    private static final String QUOTADTLID = "quotadtlid";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;

    private Map<String, Object> data;

    public CustomerTimeBasePolicyDetailsMessage(TimeBasePolicyDetails timeBasePolicyDetailsDTO, CustQuotaDetails custQuotaDtlsPojo) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, timeBasePolicyDetailsDTO.getDetailsid());
        map.put(FROMDAY, timeBasePolicyDetailsDTO.getFromDay());
        map.put(TODAY, timeBasePolicyDetailsDTO.getToDay());
        map.put(FROMTIME, timeBasePolicyDetailsDTO.getFromTime());
        map.put(TOTIME, timeBasePolicyDetailsDTO.getToTime());
        //map.put(SPEED, timeBasePolicyDetailsDTO.getSpeed());
        map.put(QQSID, timeBasePolicyDetailsDTO.getQqsid());
        map.put(ACCESS, timeBasePolicyDetailsDTO.getAccess());
        map.put(CUSTID, custQuotaDtlsPojo.getCustomer().getId());
        map.put(PLANID, custQuotaDtlsPojo.getPostpaidPlan().getId());
        map.put(QUOTADTLID, custQuotaDtlsPojo.getId());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Timebasepolicy from Api Gateway";
        this.setData(map);
        this.sourceName = ADOPT_API_GATEWAY;
    }
}
