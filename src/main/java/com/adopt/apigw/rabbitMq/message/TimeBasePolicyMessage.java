package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
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
public class TimeBasePolicyMessage {

    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "policy_id";
    private static final String NAME = "policy_name";
    private static final String ISDELETED = "isDeleted";
    private static final String STATUS = "status";
    private static final String MVNOID = "mvnoId";
    private static final String TIMEBASEPOLICYDTOLIST = "timeBasePolicyDetailsList";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;


    public TimeBasePolicyMessage(TimeBasePolicyDTO timeBasePolicyDTO){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, timeBasePolicyDTO.getId());
        map.put(NAME, timeBasePolicyDTO.getName());
        map.put(STATUS,timeBasePolicyDTO.getStatus());
        map.put(ISDELETED, timeBasePolicyDTO.getIsDeleted());
        map.put(MVNOID, timeBasePolicyDTO.getMvnoId());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Time base policy from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }




}
