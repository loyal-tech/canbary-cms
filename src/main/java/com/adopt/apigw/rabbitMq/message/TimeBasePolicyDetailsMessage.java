package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class TimeBasePolicyDetailsMessage {
    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String FROMDAY = "fromday";
    private static final String TODAY = "today";
    private static final String FROMTIME = "fromtime";
    private static final String TOTIME = "totime";
    private static final String SPEED = "speed";
    private static final String ACCESS = "access";
    private static final String QQSID = "qqsid";
    private static final String POLICY_ID = "policy_id";
    private static final String IS_FREE_QUOTA = "isFreeQuota";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;

    private Map<String, Object> data;

    public TimeBasePolicyDetailsMessage(TimeBasePolicyDetailsDTO timeBasePolicyDetailsDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, timeBasePolicyDetailsDTO.getDetailsid());
        map.put(FROMDAY, timeBasePolicyDetailsDTO.getFromDay());
        map.put(TODAY, timeBasePolicyDetailsDTO.getToDay());
        map.put(FROMTIME, timeBasePolicyDetailsDTO.getFromTime());
        map.put(TOTIME, timeBasePolicyDetailsDTO.getToTime());
        map.put(POLICY_ID , timeBasePolicyDetailsDTO.getTimeBasePolicy().getId());
        //map.put(SPEED, timeBasePolicyDetailsDTO.getSpeed());
        map.put(QQSID, timeBasePolicyDetailsDTO.getQqsid());
        map.put(ACCESS, timeBasePolicyDetailsDTO.getAccess());
        map.put(IS_FREE_QUOTA, timeBasePolicyDetailsDTO.getIsFreeQuota());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Timebasepolicy from Api Gateway";
        this.setData(map);
        this.sourceName = ADOPT_API_GATEWAY;
    }



}
